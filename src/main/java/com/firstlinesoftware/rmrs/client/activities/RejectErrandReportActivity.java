package com.firstlinesoftware.rmrs.client.activities;

import com.firstlinesoftware.base.client.services.UserActionCallback;
import com.firstlinesoftware.exec.client.Exec;
import com.firstlinesoftware.exec.client.places.RejectErrandReportPlace;
import com.firstlinesoftware.rmrs.client.Rmrs;
import com.firstlinesoftware.rmrs.client.messages.RmrsMessages;
import com.firstlinesoftware.exec.client.activities.AbstractCreateVisaActivity;
import com.firstlinesoftware.exec.shared.dto.Visa;

public class RejectErrandReportActivity extends AbstractCreateVisaActivity {

    private final RmrsMessages messages = Rmrs.getInjector().getMessages();

    public RejectErrandReportActivity(RejectErrandReportPlace place) {
        super(false, place.errandId, place.positionId);
    }

    @Override
    protected void storeVisa() {
        dto.kind = Visa.KIND;
        Exec.getInjector().getErrandProxy().rejectReport(documentId, dto, new UserActionCallback<Void>(
                messages.reportRejected(),
                messages.errorWhileRejectingReport()) {
            @Override
            public void onActionSuccess(Void result) {
                onResultSuccess(null);
            }
        });
    }

    @Override
    public String getTitle() {
        return messages.createRejectionReportVisa();
    }

    @Override
    protected void setupView() {
        super.setupView();
        view.save.setText(messages.send());
    }
}
