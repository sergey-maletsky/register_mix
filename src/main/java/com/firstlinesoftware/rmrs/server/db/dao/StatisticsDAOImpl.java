package com.firstlinesoftware.rmrs.server.db.dao;

import com.firstlinesoftware.rmrs.server.db.entities.RequirementsStatistics;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class StatisticsDAOImpl implements StatisticsDAO {
    private static final long SCHEDULED_TIME = 1000*60*10;
    private Map<String, Long> countingDocuments = new ConcurrentHashMap<>();
    private Map<Long, RequirementsStatistics> countingDocumentsByUser = new ConcurrentHashMap<>();

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private PlatformTransactionManager transactionManager;

    @Override
    public void updateRequirementCountingById() {
        List<Object[]> batch = getBatchList(true, false, null);
        int[] state = jdbcTemplate.batchUpdate(SQL_UPDATE, batch);
        batch = getBatchList(true, true, state);
        if (batch.size() > 0) {
            jdbcTemplate.batchUpdate(SQL_INSERT, batch);
        }
    }

    @Override
    public void updateUserCountingById() {
        List<Object[]> batch = getBatchList(false, false, null);
        int[] state = jdbcTemplate.batchUpdate(SQL_UPDATE_VIS, batch);
        batch = getBatchList(false, true, state);
        if (batch.size() > 0) {
            jdbcTemplate.batchUpdate(SQL_INSERT_VIS, batch);
        }
    }

    @Override
    public long getRequirementCountingById(String requirementId) {
        long counting;
        try {
            counting = jdbcTemplate.queryForObject(SQL_FIND_COUNT_BY_REQ_ID,
                    Long.class, requirementId);
        } catch (DataAccessException ex) {
            return 0;
        }

        return counting;
    }

    @Override
    public long getUserCountingById(String requirementId, String userId) {
        long counting;
        try {
            counting = jdbcTemplate.queryForObject(SQL_FIND_COUNT_BY_REQ_ID_AND_USER_ID_VIS,
                    Long.class, requirementId, userId);
        } catch (DataAccessException ex) {
            return 0;
        }

        return counting;
    }

    @Override
    public Timestamp getUserVisitedById(String requirementId, String userId) {
        Timestamp timestamp;
        try {
            timestamp = jdbcTemplate.queryForObject(SQL_FIND_VISITED_BY_REQ_ID_AND_USER_ID_VIS,
                    Timestamp.class, requirementId, userId);
        } catch (DataAccessException ex) {
            return null;
        }

        return timestamp;
    }

    @Override
    public Map<String, Long> findAllStatistics() {
        List<RequirementsStatistics> requirementsStatisticsList = new ArrayList<>();
        Map<String, Long> dbCountingDocuments = new HashMap<>();
        try {
            requirementsStatisticsList = jdbcTemplate.queryForList(SQL_FIND_ALL,
                    RequirementsStatistics.class);
        } catch (DataAccessException ex) {
            Logger.getLogger(getClass()).error("While finding all items from the requirements_statistics table", ex);
        }

        for (RequirementsStatistics requirementsStatistics : requirementsStatisticsList) {
            dbCountingDocuments.put(requirementsStatistics.getRequirementId(), requirementsStatistics.getCount());
        }

        return dbCountingDocuments;
    }

    @Scheduled(fixedDelay = SCHEDULED_TIME)
    public void updateDocumentsCounting() {
        TransactionDefinition txDefinition = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);
        try {
            updateRequirementCountingById();
            updateUserCountingById();

            transactionManager.commit(txStatus);
        } catch (DataAccessException ex) {
            transactionManager.rollback(txStatus);
            Logger.getLogger(getClass()).error("While updating/inserting the statistics tables", ex);
        }
    }

    private List<Object[]> getBatchList(boolean isReqTable, boolean isInsert, int[] state) {
        List<Object[]> batch = new ArrayList<>();
        if (isReqTable) {
            for (String key : countingDocuments.keySet()) {
                Object[] values;
                if (isInsert) {
                    values = new Object[]{key, countingDocuments.get(key)};
                } else {
                    values = new Object[]{countingDocuments.get(key), key};
                }

                batch.add(values);
            }

            if (state != null) {
                batch = getInsertingBatchList(batch, state);
            }
        } else {
            for (Long key : countingDocumentsByUser.keySet()) {
                Object[] values;
                if (isInsert) {
                    values = new Object[]{
                            countingDocumentsByUser.get(key).getRequirementId(),
                            countingDocumentsByUser.get(key).getUserId(),
                            countingDocumentsByUser.get(key).getVisited(),
                            countingDocumentsByUser.get(key).getCount()
                    };
                } else {
                    values = new Object[]{
                            countingDocumentsByUser.get(key).getVisited(),
                            countingDocumentsByUser.get(key).getCount(),
                            countingDocumentsByUser.get(key).getRequirementId(),
                            countingDocumentsByUser.get(key).getUserId()
                    };
                }

                batch.add(values);
            }

            if (state != null) {
                batch = getInsertingBatchList(batch, state);
            }
        }

        return batch;
    }

    private static List<Object[]> getInsertingBatchList(List<Object[]> batch, int[] state) {
        List<Object[]> newBatch = new ArrayList<>();
        for(int i = 0; i < state.length ; i++) {
            if (state[i] == 0) {
                newBatch.add(batch.get(i));
            }
        }

        return newBatch;
    }

    @Override
    public Map<String, Long> getCountingDocuments() {
        return countingDocuments;
    }

    @Override
    public Map<Long, RequirementsStatistics> getCountingDocumentsByUser() {
        return countingDocumentsByUser;
    }
}
