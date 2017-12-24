package com.firstlinesoftware.rmrs.client.proxies;

import com.firstlinesoftware.base.client.proxies.BaseProxy;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.ecm.client.callbacks.ReportActionCallback;
import com.firstlinesoftware.ecm.shared.dto.SearchReportCriteria;
import com.firstlinesoftware.rmrs.shared.dto.CircularLetter;

import java.util.List;

public class RequirementsReportProxy extends BaseProxy {
    public <T> void printSearchResults(String format, SearchReportCriteria reportCriteria, List<String> fields, ActionCallback<T> callback) {
        post(reportCriteria, "printSearchResults", callback, "format", format, "fields", fields);
    }

    public void printDocument(String reportName, SearchReportCriteria criteria, String id, ReportActionCallback callback) {
        post(criteria, "printDocument", callback, "reportName", reportName, "docId", id);
    }

    public void printCircular(CircularLetter letter, ReportActionCallback callback, String reportName, String lang) {
        post(letter, "printCircularLetter", callback, "reportName", reportName, "lang", lang);
    }
}
