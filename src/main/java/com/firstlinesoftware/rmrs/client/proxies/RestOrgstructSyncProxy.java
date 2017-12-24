package com.firstlinesoftware.rmrs.client.proxies;

import com.firstlinesoftware.base.client.services.UserActionCallback;
import com.firstlinesoftware.ecm.client.proxies.DocumentProxy;

/**
 * Created by rburnashev on 04.02.15.
 */
public class RestOrgstructSyncProxy extends DocumentProxy {
    public void startSyncOrgstruct(UserActionCallback<String> callback) {
        post(EMPTY_DTO, "startRestSyncOrgstruct", callback);
    }
}
