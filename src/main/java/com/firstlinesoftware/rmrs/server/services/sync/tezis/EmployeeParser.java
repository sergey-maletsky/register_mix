package com.firstlinesoftware.rmrs.server.services.sync.tezis;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rburnashev on 16.02.15.
 */
public class EmployeeParser extends DefaultHandler {

    public static final String EXT_EMPLOYEE = "ext$Employee";
    public static final String EXT_DEPARTMENT_EXT = "ext$DepartmentExt";
    private List<TezisEmployee> employees;
    private TezisEmployee currentInstance;
    private String currentElement;

    public EmployeeParser() {
        this.employees = new ArrayList<TezisEmployee>();
    }

    public List<TezisEmployee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<TezisEmployee> employees) {
        this.employees = employees;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equals("instance")) {
            final String id = attributes.getValue("id");
            if (id.startsWith(EXT_EMPLOYEE)) {
                currentInstance = new TezisEmployee();
                currentInstance.id = id.substring(13);
            } else if (id.startsWith(EXT_DEPARTMENT_EXT) && currentInstance != null) {
                currentInstance.departmentId=attributes.getValue("id").substring(18);
            }
        } else if (currentInstance != null && qName.equals("basic")) {
            currentElement = attributes.getValue("name");
        } else if (currentInstance != null && qName.equals("version")) {
            currentElement = "version";
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (currentInstance != null && currentElement != null) {
            final String value = new String(ch, start, length).trim();

            switch (currentElement) {
                case "email":
                    currentInstance.email = value;
                    break;
                case "name":
                    currentInstance.name = value;
                    break;
                case "firstName":
                    currentInstance.firstName = value;
                    break;
                case "lastName":
                    currentInstance.lastName = value;
                    break;
                case "middleName":
                    currentInstance.middleName = value;
                    break;
                case "version":
                    currentInstance.version = Integer.parseInt(value);
                    break;
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (currentInstance != null && qName.equals("instance")) {
            employees.add(currentInstance);
            currentInstance = null;
        } else {
            currentElement = null;
        }
    }
}
