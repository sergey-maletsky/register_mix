package com.firstlinesoftware.rmrs.server.services;

/**
 * Created by rburnashev on 04.02.15.
 */
public interface RESTOrstructSyncService {
    void startDepartmentsSync();

    void cleanupPositionsOutOfDepartment();
    //void startPositionsSync();
}
