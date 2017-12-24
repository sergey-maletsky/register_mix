package com.firstlinesoftware.rmrs.server.services.sync.tezis;

import java.util.List;

/**
 * Created by rburnashev on 12.02.15.
 */
public class TezisDept {
    public String id;
    public String code;
    public String name;
    public Integer version;
    public String parentId;
    public List<TezisDept> subDepts;
    public List<TezisEmployee> employees;

    public TezisDept(String code, String name, String id, Integer version) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.version = version;
    }

    @Override
    public String toString() {
        return "[" + code + "] " + name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TezisDept tezisDept = (TezisDept) o;

        if (code != null ? !code.equals(tezisDept.code) : tezisDept.code != null) return false;
        if (!id.equals(tezisDept.id)) return false;
        if (name != null ? !name.equals(tezisDept.name) : tezisDept.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
