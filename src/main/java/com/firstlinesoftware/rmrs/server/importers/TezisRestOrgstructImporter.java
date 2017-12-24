package com.firstlinesoftware.rmrs.server.importers;

import com.firstlinesoftware.base.server.importers.AbstractImporter;
import com.firstlinesoftware.base.server.importers.ImportStrategy;
import com.firstlinesoftware.base.server.importers.impl.DirectoriesUpdateStrategy;
import com.firstlinesoftware.base.server.services.RepositoryService;
import com.firstlinesoftware.base.shared.dto.Directory;
import com.firstlinesoftware.orgstruct.server.importers.impl.OrgstructUpdateByExternalAspectStrategy;
import com.firstlinesoftware.orgstruct.server.services.OrgstructService;
import com.firstlinesoftware.orgstruct.server.services.OrgstructSyncService;
import com.firstlinesoftware.orgstruct.shared.directories.OrgstructDirectories;
import com.firstlinesoftware.orgstruct.shared.dto.Orgstructure;
import com.firstlinesoftware.orgstruct.shared.dto.Person;
import com.firstlinesoftware.orgstruct.shared.dto.Position;
import com.firstlinesoftware.rmrs.server.adapters.TezisRestAdapter;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TezisRestOrgstructImporter extends AbstractImporter<Orgstructure, Orgstructure, String> {
    public static final Function<Person, String> GET_USER_NAMES = new Function<Person, String>() {
        @Nullable
        @Override
        public String apply(Person input) {
            return input != null ? input.userName : null;
        }
    };
    @Autowired
    private OrgstructUpdateByExternalAspectStrategy orgstructUpdateStrategy;

    @Autowired
    private DirectoriesUpdateStrategy directoriesUpdateStrategy;

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private OrgstructService orgstructService;
    @Autowired
    private OrgstructSyncService orgstructSyncService;

    @Autowired
    private TezisRestAdapter adapter;

    @Value("${tezis.sync.rest.url}")
    private String url;

    public void doImport() {
        doImport(adapter, new ImportStrategy<Orgstructure>() {
            @Override
            public void startImport() {

            }

            @Override
            public void importItem(final Orgstructure item) {
                orgstructUpdateStrategy.importItem(item);
                if (item.positions != null) {
                    final Set<String> names = new HashSet<String>();
                    for (Position position : item.positions) {
                        names.add(position.getName());
                    }
                    final Directory d = new Directory();
                    d.type = OrgstructDirectories.POSITIONS.getType();
                    d.items = new Directory.Items();
                    d.items.items = new ArrayList<>();
                    for (String name : names) {
                        final Directory.Item e = new Directory.Item(name, name);
                        e.type = d.type;
                        d.items.items.add(e);
                    }
                    directoriesUpdateStrategy.importItem(d);
                }
                if (item.persons != null) {
                    repositoryService.runSystemTask(null, new Runnable() {
                        @Override
                        public void run() {
                            final List<String> tezisUsers = Lists.transform(item.persons, GET_USER_NAMES);
                            final List<String> existing = Lists.transform(orgstructSyncService.getAllPersons(), GET_USER_NAMES);
                            for (String user : existing) {
                                if (!tezisUsers.contains(user)) {
                                    final Person person = orgstructService.getPersonByUsername(user);
                                    assert person != null;
                                    orgstructService.deletePerson(person.id);
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void stopImport() {

            }
        }, url);
    }
}
