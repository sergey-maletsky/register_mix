package com.firstlinesoftware.rmrs.server.services.impl;

import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.server.services.SystemVariables;
import com.firstlinesoftware.base.server.utils.Batcher;
import com.firstlinesoftware.base.shared.dto.Pair;
import com.firstlinesoftware.base.shared.dto.SearchCriteria;
import com.firstlinesoftware.orgstruct.server.services.OrgstructService;
import com.firstlinesoftware.orgstruct.server.services.OrgstructSyncService;
import com.firstlinesoftware.orgstruct.shared.dto.Department;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.server.services.RESTOrstructSyncService;
import com.firstlinesoftware.rmrs.server.services.sync.tezis.TezisDept;
import com.firstlinesoftware.rmrs.server.services.sync.tezis.TezisEmployee;
import com.firstlinesoftware.rmrs.server.services.sync.tezis.TezisRestClient;
import com.firstlinesoftware.rmrs.server.services.sync.tezis.TezisRestSynchronizer;
import com.google.common.base.Function;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by rburnashev on 04.02.15.
 */
@Service
public class RESTOrstructSyncServiceImpl implements RESTOrstructSyncService {

    private final Logger logger = Logger.getLogger(getClass());

    private static final String RMRS = "РМРС";
    private static final String SYSTEM_VARIABLE_DEPARTMENTS_VERSIONS = "departmentsVersions";
    private static final String SYSTEM_VARIABLE_EMPLOYEES_VERSIONS = "employeesVersions";
    private static final int THREAD_POOLS = 100;
    private static final long TERMINATION_TIMEOUT = 5;

    @Value("${tezis.sync.rest.url}")
    private String REST_URL;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private OrgstructService orgstructService;
    @Autowired
    private OrgstructSyncService orgstructSyncService;
    @Autowired
    private SystemVariables systemVariables;
    @Autowired
    private TezisRestSynchronizer tezisRestSynchronizer;
    @Autowired
    private Batcher batcher;

    @Override
    public void startDepartmentsSync() {
        final TezisRestClient client = new TezisRestClient(REST_URL);
//        logger.trace("Start getting departments tree");
        final TezisDept root = client.getDepartments();
//        logger.trace("End getting departments tree");

        if (root != null) {
            HashMap<String, TezisRestSynchronizer.SyncEntry> departmentCheckSums = systemVariables.get(SYSTEM_VARIABLE_DEPARTMENTS_VERSIONS);
            if (departmentCheckSums == null) {
                departmentCheckSums = new HashMap<String, TezisRestSynchronizer.SyncEntry>();
            }
//            HashMap<String, TezisRestSynchronizer.SyncEntry> employeeVersions = systemVariables.get(SYSTEM_VARIABLE_EMPLOYEES_VERSIONS);
//            if (employeeVersions == null) {
//                employeeVersions = new HashMap<String, TezisRestSynchronizer.SyncEntry>();
//            }

            final List<Department> rootDepartments = orgstructService.getDepartments((String) null);
            final String rmrsDepartmentId = getRootRMRSId(rootDepartments);

            if (rmrsDepartmentId != null) {
                final HashMap<String, TezisRestSynchronizer.SyncEntry> finalDepartmentCheckSums = departmentCheckSums;
//                final HashMap<String, TezisRestSynchronizer.SyncEntry> finalEmployeeVersions = employeeVersions;

                repositoryService.runSystemTask(null, new Runnable() {
                    @Override
                    public void run() {
                        logger.trace("Start synchronising departments");
                        tezisRestSynchronizer.dfsAndSyncDepartments(root, rmrsDepartmentId);
//                        systemVariables.put(SYSTEM_VARIABLE_DEPARTMENTS_VERSIONS, finalDepartmentCheckSums);
//                        systemVariables.put(SYSTEM_VARIABLE_EMPLOYEES_VERSIONS, finalEmployeeVersions);
                        logger.trace("End synchronising departments");
                    }
                });
                startPositionsSync(root);
            }


        }
    }


    public void startPositionsSync(TezisDept root) {
        HashMap<String, TezisRestSynchronizer.SyncEntry> employeeVersions = systemVariables.get(SYSTEM_VARIABLE_EMPLOYEES_VERSIONS);
        if (employeeVersions == null) {
            employeeVersions = new HashMap<String, TezisRestSynchronizer.SyncEntry>();
        }
        final HashMap<String, TezisRestSynchronizer.SyncEntry> finalEmployeeVersions = employeeVersions;


        if (root != null) {
            logger.trace("Start asynchronous merging employees");
            final Batcher.Task<TezisEmployee> task = batcher.startSingleThreaded(null, 100, new Function<TezisEmployee, Void>() {
                @Nullable
                @Override
                public Void apply(TezisEmployee input) {
                    tezisRestSynchronizer.syncPosition(input, finalEmployeeVersions);
                    return null;
                }
            });
            syncEmployeesInDep(root, task);
            task.finish();

        }


    }

    public void syncEmployeesInDep(final TezisDept tezisDept, final Batcher.Task<TezisEmployee> batcher) {
        if(tezisDept!=null){
            if(tezisDept.employees!=null) {
                for(TezisEmployee tezisEmployee:tezisDept.employees) {
                    batcher.addToBatch(tezisEmployee);
                }
            }
            if(tezisDept.subDepts!=null){
                for(TezisDept dept:tezisDept.subDepts){
                    syncEmployeesInDep(dept,batcher);
                }
            }
        }
    }

    @Override
    public void cleanupPositionsOutOfDepartment(){
        final List<Pair<String, String>> mustHave = Collections.singletonList(new Pair<String, String>("base:authorityName", "GROUP_position_*"));
        final List<Pair<String, String>> mustNotHave = Collections.singletonList(new Pair<String, String>("orgstruct:status", "blocked"));
        SearchCriteria criteria = new SearchCriteria();
        criteria.setMustHave(mustHave);
        criteria.setMustNotHave(mustNotHave);
        criteria.maxPermissionChecks = 100000;
        criteria.limit = 0;
        List<Position> positions = orgstructService.getPositions(repositoryService.search(criteria));
        final AtomicInteger removed = new AtomicInteger(0);
        final int total = positions.size();
        final Batcher.Task<Position> task = batcher.startSingleThreaded(null, 20, new Function<Position, Void>(){
            @Nullable
            @Override
            public Void apply(@Nullable Position input) {
                if (input != null){
                    if (logger.isTraceEnabled()){
                        logger.trace("In progress: " + input.getDisplayName());
                    }
                        if (input.department == null){ //unassigned position
                            if (logger.isTraceEnabled()) {
                                logger.trace("Removing position: " + input.getTitle());
                            }
                            orgstructService.deletePosition(input.id);
                            removed.getAndIncrement();
                    }
                }
                return null;
            }
        });
        logger.info("Start positions cleanup, total: " + total);
        for(Position position : positions){
            task.addToBatch(position);
        }
        task.finish();
        logger.info("Position cleanup finished, total: " + total + ", removed: " + removed.get());
    }


//    @Override
//    public void startPositionsSync() {
//        final TezisRestClient client = new TezisRestClient(REST_URL);
//        logger.trace("Start getting departments with employees");
//        final List<TezisDept> depts = client.getDepartmentEmployees();
//        logger.trace("End getting departments with employees");
//
//        HashMap<String, TezisRestSynchronizer.SyncEntry> employeeVersions = systemVariables.get(SYSTEM_VARIABLE_EMPLOYEES_VERSIONS);
//        if (employeeVersions == null) {
//            employeeVersions = new HashMap<String, TezisRestSynchronizer.SyncEntry>();
//        }
//        final HashMap<String, TezisRestSynchronizer.SyncEntry> finalEmployeeVersions = employeeVersions;
//        final ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOLS);
//
//        if (depts != null && !depts.isEmpty()) {
//            logger.trace("Start asynchronous merging employees");
//            for (final TezisDept dept : depts) {
//                if (dept.employees != null && !dept.employees.isEmpty()) {
//                    final Runnable task = new Runnable() {
//                        @Override
//                        public void run() {
//                            final String departmentId = orgstructSyncService.getDepartmentIdByExternalId(dept.id);
//
//                            if (departmentId != null) {
//                                for (TezisEmployee employee : dept.employees) {
//                                    tezisRestSynchronizer.syncPosition(employee, departmentId, finalEmployeeVersions);
//                                }
//                            }
//                        }
//                    };
//                    executor.execute(new Runnable() {
//                        @Override
//                        public void run() {
//                            repositoryService.runSystemTask(null, task);
//                        }
//                    });
//                }
//            }
//        }
//
//        executor.shutdown();
//
//        try {
//            executor.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.MINUTES);
//            logger.trace("End asynchronous merging employees");
//
//            repositoryService.runSystemTask(null, new Runnable() {
//                @Override
//                public void run() {
//                    systemVariables.put(SYSTEM_VARIABLE_EMPLOYEES_VERSIONS, finalEmployeeVersions);
//                }
//            });
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }

    private String getRootRMRSId(final List<Department> roots) {
        String result = null;
        for (Department root : roots) {
            if (RMRS.equals(root.getName())) {
                result = root.id;
            }
        }
        return result;
    }
}


