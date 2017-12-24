package com.firstlinesoftware.rmrs.server.services.sync.tezis;


/**
 * Created by rburnashev on 12.02.15.
 */
public class HashUtils {

    public static Integer getHashCode(final TezisDept root) {
        int hashCode = root.hashCode();

        if (root.subDepts != null && !root.subDepts.isEmpty()) {
            for (TezisDept child : root.subDepts) {
                hashCode += hashCode * 31 + getHashCode(child);
            }
        }

        return hashCode;
    }
}
