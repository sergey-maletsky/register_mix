package com.firstlinesoftware.rmrs.server.services.sync.tezis;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by rburnashev on 12.02.15.
 */
public class TezisRestClient {

    private static final long GET_SESSION_TIMEOUT = 30L;

    private static final String LOGIN_URL = "{0}login?u=restuser&p=mCkFgcn75Run9mVtgCGa&l=ru";
    private static final String DEPARTMENT_EMPLOYEE_QUERY = "{0}query.xml?s={1}" +
            "&e=df$Employee&q=select+d+from+df$Employee+d+where+d.department.=:code&code_type=string&code={2}";
    private static final String ALL_DEPARTMENTS_QUERY = "{0}query.xml?s={1}&e=df$Department&q=select+d+from+df$Department+d";
    private static final String GET_DEPARTMENT_QUERY = "{0}query.xml?s={1}&e=df$Department&q=select+d+from+df$Department+d+where+d.parentDepartment.id={2}";
    private static final String GET_ROOT_DEPARTMENT_QUERY = "{0}query.xml?s={1}&e=df$Department&q=select+d+from+df$Department+d+where+d.parentDepartment+is+null";
    private static final String GET_EMPLOYEES_BY_DEPARTMENT_QUERY = "{0}query.json?s={1}&e=df$Employee&q=select+d+from+df$Employee+d+where+d.department.id={2}&view=edit";

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String rest_url;

    private final SAXParser sax;

    private final FutureTask<String> session = new FutureTask<String>(new Callable<String>() {
        @Override
        public String call() throws Exception {
            HttpURLConnection conn = null;
            try {
                final String login_url = MessageFormat.format(LOGIN_URL, rest_url);
                conn = (HttpURLConnection) new URL(login_url).openConnection();
                conn.setRequestMethod("GET");
                int resp = conn.getResponseCode();

                if (resp != HttpURLConnection.HTTP_OK)
                    throw new IOException("Bad response from server on login: " + resp + " -- " + conn.getResponseMessage()
                            + "\nURL: " + login_url);

                byte[] buf = new byte[256];
                int sz, off = 0;
                InputStream is = conn.getInputStream();
                while ((sz = is.read(buf, off, buf.length - off)) > 0) {
                    off += sz;
                    if (off >= buf.length)
                        break;
                }
                is.close();

                return new String(buf, 0, off);
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
    });

    public TezisRestClient(String rest_url) {
        this.rest_url = rest_url;
        objectMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        final Thread t = new Thread(session);
        t.start();

        try {
            sax = SAXParserFactory.newInstance().newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public TezisDept getDepartments() {
        try {
            final String sessionKey = session.get(GET_SESSION_TIMEOUT, TimeUnit.SECONDS);
            final List<TezisDept> list = getRootDepartments(sessionKey);
            if (list.size() != 1) {
                return null;
            }

            final TezisDept root = list.get(0);

            getDepartmentChildren(sessionKey, root);

            return root;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public List<TezisDept> getRootDepartments(final String sessionKey) {
        final String url = MessageFormat.format(GET_ROOT_DEPARTMENT_QUERY, rest_url, sessionKey);
        return getTezisDepts(url);
    }

    public List<TezisDept> getDepartmentsByParent(final String sessionKey, final String parentId) {
        final String url = MessageFormat.format(GET_DEPARTMENT_QUERY, rest_url, sessionKey, "'" + parentId + "'");
        return getTezisDepts(url);
    }

    public List<TezisDept> getTezisDepts(String url) {
        try {
            final Dept2Parser parser = new Dept2Parser();

            sax.parse(query(url), parser, null);
            final List<TezisDept> list = parser.getDepts();

            if (list.size() <= 0) {
                return null;
            }
            return list;

        } catch (SAXException | IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<TezisDept> getDepartmentEmployees() {
        try {
            final String sessionKey = session.get(GET_SESSION_TIMEOUT, TimeUnit.SECONDS);
            final String url = MessageFormat.format(ALL_DEPARTMENTS_QUERY, rest_url, sessionKey);
            final Dept2Parser parser = new Dept2Parser();

            sax.parse(query(url), parser, null);
            final List<TezisDept> depts = parser.getDepts();

            for (TezisDept dept : depts) {
                final String employees_url = MessageFormat.format(DEPARTMENT_EMPLOYEE_QUERY, rest_url, sessionKey, dept.code);
                final EmployeeParser employeeParser = new EmployeeParser();

                sax.parse(query(employees_url), employeeParser, null);
                final List<TezisEmployee> employees = employeeParser.getEmployees();

                dept.employees = employees;
                for (TezisEmployee employee : employees) {
                    employee.departmentId = dept.id;
                }
            }

            return depts;
        } catch (InterruptedException | ExecutionException | SAXException | IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream query(final String url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        int resp = conn.getResponseCode();

        if (resp != HttpURLConnection.HTTP_OK)
            throw new IOException("Bad response from server on login: " + resp + " -- " + conn.getResponseMessage()
                    + "\n URL: " + url);

        return conn.getInputStream();
    }

    private void getDepartmentChildren(final String sessionKey, final TezisDept parent) {
        parent.subDepts = getDepartmentsByParent(sessionKey, parent.id);
        parent.employees = getDepEmployees(sessionKey, parent.id);
        if (parent.subDepts != null) {
            for (TezisDept dep : parent.subDepts) {
                getDepartmentChildren(sessionKey, dep);
            }
        }
    }

    private List<TezisEmployee> getDepEmployees(final String sessionKey, final String depId) {
        final String url = MessageFormat.format(GET_EMPLOYEES_BY_DEPARTMENT_QUERY, rest_url, sessionKey, "'" + depId + "'");
        List<TezisEmployee> tezisEmployees = new ArrayList<>();
        try {
            List<Map> listJsonEmployee = (List<Map>) objectMapper.readValue(query(url), List.class);
            if (listJsonEmployee != null && !listJsonEmployee.isEmpty()) {
                for (Map<String, Object> emp : listJsonEmployee) {
                    final Map user = (Map) emp.get("user");
                    if (user != null) {
                        final String active = (String) user.get("active");
                        if ("true".equals(active)) {
                            TezisEmployee employee = new TezisEmployee();
                            employee.id = emp.get("id").toString().substring(13);
                            employee.firstName = (String) emp.get("firstName");
                            employee.lastName = (String) emp.get("lastName");
                            employee.middleName = (String) emp.get("middleName");
                            employee.email = (String) emp.get("email");
                            employee.login = (String) user.get("login");
                            employee.positionName = getPosition(emp);
                            employee.departmentId = ((Map) emp.get("department")).get("id").toString().substring(18);
                            tezisEmployees.add(employee);
                        }
                    }
                }
            }
            return tezisEmployees;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getPosition(Map<String, Object> emp) {
        final List relations = (List) emp.get("employeeDepartmentPositionRelations");
        if (relations != null) {
            for (Object relation : relations) {
                if (relation instanceof Map) {
                    final Object position = ((Map) relation).get("position");
                    if (position instanceof Map) {
                        final Object name = ((Map) position).get("name");
                        if (name instanceof String) {
                            return ((String) name).trim();
                        }
                    }
                }
            }
        }
        return null;
    }
}
