package com.firstlinesoftware.rmrs.server.db.dao;

import com.firstlinesoftware.rmrs.server.db.entities.RequirementsStatistics;

import java.sql.Timestamp;
import java.util.Map;

public interface StatisticsDAO {
    String SQL_FIND_ALL = "SELECT * FROM " + RequirementsStatistics.REQ_STAT_TABLE_NAME;
    String SQL_FIND_ALL_BY_REQ_ID = "SELECT * FROM " + RequirementsStatistics.REQ_STAT_TABLE_NAME + " WHERE " + RequirementsStatistics.REQUIREMENT_ID_COLUMN + " = ?";
    String SQL_FIND_COUNT_BY_REQ_ID = "SELECT " + RequirementsStatistics.COUNT_COLUMN + " FROM " + RequirementsStatistics.REQ_STAT_TABLE_NAME + " WHERE " + RequirementsStatistics.REQUIREMENT_ID_COLUMN + " = ?";
    String SQL_UPDATE = "UPDATE " + RequirementsStatistics.REQ_STAT_TABLE_NAME + " SET " + RequirementsStatistics.COUNT_COLUMN + " = ? WHERE " + RequirementsStatistics.REQUIREMENT_ID_COLUMN + " = ?";
    String SQL_INSERT = "INSERT INTO " + RequirementsStatistics.REQ_STAT_TABLE_NAME + "(" + RequirementsStatistics.REQUIREMENT_ID_COLUMN + ", " + RequirementsStatistics.COUNT_COLUMN + ") VALUES (?, ?)";

    String SQL_UPDATE_VIS = "UPDATE " + RequirementsStatistics.VISIT_STAT_TABLE_NAME + " SET " + RequirementsStatistics.VISITED_COLUMN + " = ?, " + RequirementsStatistics.COUNT_COLUMN + " = ? WHERE " + RequirementsStatistics.REQUIREMENT_ID_COLUMN + " = ? AND "  + RequirementsStatistics.USER_ID_COLUMN + " = ?";
    String SQL_INSERT_VIS = "INSERT INTO " + RequirementsStatistics.VISIT_STAT_TABLE_NAME + "(" + RequirementsStatistics.REQUIREMENT_ID_COLUMN + ", " + RequirementsStatistics.USER_ID_COLUMN + ", " + RequirementsStatistics.VISITED_COLUMN + ", " + RequirementsStatistics.COUNT_COLUMN + ") VALUES (?, ?, ?, ?)";
    String SQL_FIND_COUNT_BY_REQ_ID_AND_USER_ID_VIS = "SELECT " + RequirementsStatistics.COUNT_COLUMN + " FROM " + RequirementsStatistics.VISIT_STAT_TABLE_NAME + " WHERE " + RequirementsStatistics.REQUIREMENT_ID_COLUMN + " = ? AND " + RequirementsStatistics.USER_ID_COLUMN + " = ?";
    String SQL_FIND_VISITED_BY_REQ_ID_AND_USER_ID_VIS = "SELECT " + RequirementsStatistics.VISITED_COLUMN + " FROM " + RequirementsStatistics.VISIT_STAT_TABLE_NAME + " WHERE " + RequirementsStatistics.REQUIREMENT_ID_COLUMN + " = ? AND " + RequirementsStatistics.USER_ID_COLUMN + " = ?";

    /**
     * Returns whole statistics items.
     * @return whole statistics items.
     */
    Map<String, Long> findAllStatistics();

    /**
     * Returns the number of visits to a specific requirement.
     * @param requirementId
     * @return The number of visits to a specific requirement.
     */
    long getRequirementCountingById(String requirementId);

    /**
     * Returns the number of visits to a specific requirement with a specific user.
     * @param requirementId
     * @param userId
     * @return The number of visits to a specific requirement with a specific user.
     */
    long getUserCountingById(String requirementId, String userId);

    /**
     * Returns the number of visits to a specific requirement with a specific user.
     * @param requirementId
     * @param userId
     * @return Timestamp of visit to a specific requirement with a specific user.
     */
    Timestamp getUserVisitedById(String requirementId, String userId);

    /**
     * Updates the number of visits to a specific requirement.
     */
    void updateRequirementCountingById();

    /**
     * Updates the number of visits to a specific requirement with a specific user.
     */
    void updateUserCountingById();

    Map<String, Long> getCountingDocuments();

    Map<Long, RequirementsStatistics> getCountingDocumentsByUser();
}
