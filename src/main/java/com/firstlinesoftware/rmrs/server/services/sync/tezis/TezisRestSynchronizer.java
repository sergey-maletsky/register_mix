package com.firstlinesoftware.rmrs.server.services.sync.tezis;

import com.firstlinesoftware.base.server.importers.AbstractExternalAspectStrategy;
import com.firstlinesoftware.orgstruct.server.services.OrgstructService;
import com.firstlinesoftware.orgstruct.server.services.OrgstructSyncService;
import com.firstlinesoftware.orgstruct.shared.dto.Department;
import com.firstlinesoftware.orgstruct.shared.dto.Person;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/**
 * Created by rburnashev on 20.02.15.
 */
@Component
public class TezisRestSynchronizer {

    private final Logger logger = Logger.getLogger(getClass());

    @Value("${tezis.sync.mail.template}")
    private String TEZIS_MAIL_TEMPLATE;

    @Autowired
    private OrgstructService orgstructService;
    @Autowired
    private OrgstructSyncService orgstructSyncService;

    public void dfsAndSyncDepartments(final TezisDept node, final String parentDepartmentId) {
        final String departmentId = syncDepartment(node, parentDepartmentId);
//        if (node.employees != null) {
//            for (TezisEmployee tezisEmployee : node.employees) {
//                syncPosition(tezisEmployee, departmentId, employeesCheckSums);
//            }
//        }
        final List<TezisDept> subDepts = node.subDepts;
        if (subDepts != null && !subDepts.isEmpty()) {
            for (TezisDept subDept : subDepts) {
                dfsAndSyncDepartments(subDept, departmentId);
            }
        }
    }

    public String syncDepartment(final TezisDept source, final String parentDepartmentId) {
        String id = orgstructSyncService.getDepartmentIdByExternalId(source.id);

        final Department updated = new Department();

        updated.setExternalId(source.id);
        updated.setExternalModifiedDate(new Date());
        updated.setExternalVersionId(String.valueOf(source.version));
        updated.setName(source.name);
        updated.setDepartmentCode(source.code);
        updated.setParentId(parentDepartmentId);

        if (id == null) {
            id = orgstructService.createDepartment(updated, parentDepartmentId);
        } else {
            final Department existing = orgstructService.getDepartment(id);
            assert existing != null;
            if (AbstractExternalAspectStrategy.isNewer(updated, existing)) {
                existing.id = id;
                orgstructService.updateDepartment(updated);
            }
        }

        return id;
    }

    public String syncPosition(final TezisEmployee sourceEmployee, final Map<String, SyncEntry> employeeVersions) {
        Person person = null;
        String id = null;

        if (sourceEmployee.login != null) {
            person = orgstructService.getPersonByUsername(sourceEmployee.login);
            if (person != null) {
                if (!person.getFirstName().equals(sourceEmployee.firstName) ||
                        !person.getLastName().equals(sourceEmployee.lastName) ||
                        !person.getMiddleName().equals(sourceEmployee.middleName)) {
                    person.setFirstName(sourceEmployee.firstName);
                    person.setLastName(sourceEmployee.lastName);
                    person.setMiddleName(sourceEmployee.middleName);
                    orgstructService.updatePerson(person);
                }
            } else {
                person = new Person();
                person.setFirstName(sourceEmployee.firstName);
                person.setLastName(sourceEmployee.lastName);
                person.setMiddleName(sourceEmployee.middleName);
                person.setUserName(sourceEmployee.login);
                person.setEmail(sourceEmployee.email);
                person = orgstructService.getPerson(orgstructService.createPerson(person));
            }


        }

        if (person != null) {
            String departmentId = orgstructSyncService.getDepartmentIdByExternalId(sourceEmployee.departmentId);
            id = orgstructSyncService.getPositionIdByExternalId(sourceEmployee.id);

            if (id == null) {
                final Position position = new Position();
                List<String> roles = new ArrayList<>();
                roles.add("registrator");
                roles.add("top_errand_issuer");

                position.setExternalId(sourceEmployee.id);
                position.setExternalModifiedDate(new Date());
                position.setEmail(sourceEmployee.email);
                position.setName(sourceEmployee.positionName);
                position.setRoles(roles);

                id = orgstructService.createPosition(position, departmentId);
                employeeVersions.put(id, new SyncEntry("", sourceEmployee.version));

                orgstructService.assign(person.id, id);

                logger.trace("Successful creating position of employee " + sourceEmployee.login);
            } else {
                final SyncEntry syncEntry = employeeVersions.get(id);

                if (syncEntry == null || !Objects.equals(sourceEmployee.version, syncEntry.version) || sourceEmployee.version == null) {
                    final Position position = orgstructService.getPosition(id);

                    position.setEmail(sourceEmployee.email);
                    position.setParentId(departmentId);

                    orgstructService.updatePosition(position);
                    employeeVersions.put(id, new SyncEntry("", sourceEmployee.version));

                    if (!person.id.equals(position.person.id)) {
                        orgstructService.assign(person.id, id);
                    }
//                    logger.trace("Successful updating position of employee " + sourceEmployee.name);
                }
            }
        }
//        logger.trace("Processing position of employee " + sourceEmployee.name);

        return id;
    }

    private String getLoginFromEmail(String email) {
        String result = null;
        final String[] parts = email.split("@");

        if (parts.length >= 2 && parts[1].equals(TEZIS_MAIL_TEMPLATE)) {
            result = parts[0];
        }

        return result;
    }

    public static class SyncEntry implements Serializable {
        public final String checkSum;
        public final Integer version;


        private SyncEntry(final String checkSum, final Integer version) {
            this.checkSum = checkSum;
            this.version = version;
        }
    }
}
