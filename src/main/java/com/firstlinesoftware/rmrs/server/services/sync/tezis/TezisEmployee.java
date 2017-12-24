package com.firstlinesoftware.rmrs.server.services.sync.tezis;

/**
 * Created by rburnashev on 16.02.15.
 */
public class TezisEmployee {
    private  static volatile int instanceCount=0;
    public TezisEmployee() {
        instanceCount++;
    }

    public String id;
    public String name;
    public String email;
    public String firstName;
    public String middleName;
    public String lastName;
    public String login;
    public Integer version;

    public String departmentId;
    public String positionName;
}
