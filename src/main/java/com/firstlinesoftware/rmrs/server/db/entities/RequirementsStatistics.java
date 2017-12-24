package com.firstlinesoftware.rmrs.server.db.entities;

import com.google.common.base.Objects;
import java.sql.Timestamp;

public class RequirementsStatistics {
    public static final String REQ_STAT_TABLE_NAME = "requirements_statistics";
    public static final String VISIT_STAT_TABLE_NAME = "visit_statistics";
    public static final String REQUIREMENT_ID_COLUMN = "requirement_id";
    public static final String USER_ID_COLUMN = "user_id";
    public static final String VISITED_COLUMN = "visited";
    public static final String COUNT_COLUMN = "count";
    public static final String TIMESTAMP_FORMAT = "dd.MM.yyyy HH:mm:ss"; //21.07.2017 10:05:01

    private String requirementId;
    private String userId;
    private Timestamp visited;
    private Long count;

    public RequirementsStatistics() {
    }

    public RequirementsStatistics(String requirementId, String userId, Timestamp visited, Long count) {
        this.requirementId = requirementId;
        this.userId = userId;
        this.visited = visited;
        this.count = count;
    }

    public String getRequirementId() {
        return requirementId;
    }

    public void setRequirementId(String requirementId) {
        this.requirementId = requirementId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getVisited() {
        return visited;
    }

    public void setVisited(Timestamp visited) {
        this.visited = visited;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequirementsStatistics that = (RequirementsStatistics) o;
        return Objects.equal(getRequirementId(), that.getRequirementId()) &&
                Objects.equal(getUserId(), that.getUserId()) &&
                Objects.equal(getCount(), that.getCount());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getRequirementId(), getUserId(), getCount());
    }
}
