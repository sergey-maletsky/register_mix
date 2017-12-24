package com.firstlinesoftware.rmrs.shared.dto.importer;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ADemkina on 12.01.2017.
 */
public class DateTimeAdapter extends XmlAdapter<String, Date> {
    public Date unmarshal(String v) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String substring = v.substring(0, v.indexOf("."));

        return formatter.parse(substring);
    }

    public String marshal(Date v) throws Exception {
        return v.toString();
    }
}
