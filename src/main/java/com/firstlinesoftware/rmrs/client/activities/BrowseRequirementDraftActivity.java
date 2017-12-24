package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.SuccessCallback;
import com.firstlinesoftware.ecm.client.activities.BrowseDocumentFolderActivity;
import com.firstlinesoftware.ecm.shared.dto.TaskFolder;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.places.BrowseRequirementDraftsPlace;
import com.firstlinesoftware.rmrs.shared.dto.Proposal;

public class BrowseRequirementDraftActivity extends BrowseDocumentFolderActivity<Proposal> {
    public BrowseRequirementDraftActivity(BrowseRequirementDraftsPlace place) {
        super(place.id, place.documentId);
        positionId = place.positionId;
    }

    @Override
    protected void getFolder(String folderId, String positionId, SuccessCallback callback) {
        callback.onSuccess(new TaskFolder(folderId, positionId, Rmrs.getInjector().getMessages().drafts(), true));
    }
}
