package com.firstlinesoftware.rmrs.client.proxies;

import com.firstlinesoftware.base.client.proxies.BaseProxy;
import com.firstlinesoftware.base.client.services.ActionCallback;
import com.firstlinesoftware.base.shared.actions.RestResult;
import com.firstlinesoftware.rmrs.shared.dto.Requirement;
import com.firstlinesoftware.route.shared.dto.Rounds;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class RequirementProxy extends BaseProxy {

    public void create(Requirement dto, String folder, String errandId, String templateId, boolean sendToRoute, ActionCallback<Void> callback) {
        post(dto, "createRequirement", callback, "folderId", folder, "errand", errandId, "template", templateId, "sendToRoute", sendToRoute);
    }

    public void update(Requirement dto, String errandId, boolean sendToRoute, ActionCallback<Void> callback) {
        post(dto, "updateRequirement", callback, "errand", errandId, "sendToRoute", sendToRoute);
    }

    public void falsificate(Requirement dto, ActionCallback<Void> callback) {
        post(dto, "falsificateRequirement", callback);
    }

    public void getResponsible(String id, ActionCallback<String> callback) {
        get("getResponsible", callback, "id", id);
    }

    public void getDocumentCounting(String id, ActionCallback<String> callback) {
        get("getDocumentCounting", callback, "id", id);
    }

    public void getDocumentExt(String id, ActionCallback<String> callback) {
        get("getDocumentExt", callback, "id", id);
    }

    public void startImport(String filename, ActionCallback<Void> callback) {
        post(EMPTY_DTO, "importRequirements", callback, "filename", filename);
    }

    public void sendRequirementsToApproval(Collection<String> ids, Rounds rounds, ActionCallback<Void> callback) {
        post(rounds, "sendRequirementsToApproval", callback, "ids", ids);
    }

    public void setLifecycle(String lifecycle, List<String> ids, String comment, ActionCallback<Void> callback) {
        final RestResult object = new RestResult();
        object.strings = ids;
        post(object, "setLifecycle", callback, "lifecycle", lifecycle, "comment", comment);
    }

    public void removeResponsibilityForRequirements(List<Requirement> requirements, ActionCallback<Void> callback) {
        post(requirements, "removeResposibilityForRequirements", callback);
    }

    public void getByFolderWithAttachVersions(String folder, boolean showRecursive, boolean onlyHeaders, ActionCallback<List<Requirement>> callback) {
        get("getByFolderWithAttachVersions", callback, "folder", folder, "showRecursive", showRecursive, "onlyHeaders", onlyHeaders);
    }

    public void export(String parent, Date date, ActionCallback<String> callback) {
        get("exportRequirements", callback, "parent", parent, "date", date.getTime());
    }
}
