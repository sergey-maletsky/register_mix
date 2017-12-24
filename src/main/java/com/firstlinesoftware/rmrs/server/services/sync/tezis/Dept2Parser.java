package com.firstlinesoftware.rmrs.server.services.sync.tezis;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by rburnashev on 12.02.15.
 */
public class Dept2Parser extends DefaultHandler {
    private List<TezisDept> depts;
    private String field;
    private String id;
    private boolean act = false;

    private StringBuilder buf = new StringBuilder();

    private Map<String, String> fields = new HashMap<>();

    public Dept2Parser() {
        this.depts = new ArrayList<TezisDept>();
    }

    public List<TezisDept> getDepts() {
        return depts;
    }

    public void setDepts(List<TezisDept> depts) {
        this.depts = depts;
    }


    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        buf.append(new String(ch, start, length));
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (act && "instance".equals(qName)) {
            final String code = fields.get("code");
            final String name = fields.get("name");
            final String version = fields.get("version");
            if (id == null || name == null || version == null) {
                Logger.getLogger(Dept2Parser.class.getName()).warning(" -- nullable values for Dept");
            } else {
                depts.add(new TezisDept(code, name, id, Integer.parseInt(version)));
            }
            id = null;
            field = null;
            fields.clear();
            act = false;
        } else {
            if (field != null) {
                fields.put(field, buf.toString().trim());
                field = null;
            }
            buf = new StringBuilder();
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if ("instance".equals(qName)) {
            id = attributes.getValue("id").substring(18);
            act = true;
        } else if ("field".equals(qName)) {
            field = attributes.getValue("name");
//        } else if (act & "basic".equals(attributes.getValue("name"))) {
//            String n = attributes.getValue("name");
//            i = ("code".equals(n)? 0: "name".equals(n)? 1: -1);
//        } else if (act & "version".equals(qName)) {
//            i = 3;
        }
    }

}
