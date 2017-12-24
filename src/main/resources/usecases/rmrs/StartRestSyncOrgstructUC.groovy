package usecases.rmrs

import com.firstlinesoftware.base.server.services.AuthService
import com.firstlinesoftware.orgstruct.shared.dto.PositionRoles
import com.firstlinesoftware.rmrs.server.importers.TezisRestOrgstructImporter
import org.jboss.resteasy.annotations.cache.NoCache
import org.springframework.beans.factory.annotation.Autowired
import usecases.orgstruct.CheckedUC

import javax.ws.rs.POST

/**
 * Created by rburnashev on 04.02.15.
 */
class StartRestSyncOrgstructUC extends CheckedUC<Void> {

    @Autowired
    private TezisRestOrgstructImporter importer;

    @POST
    @NoCache
    public Void run() {
        runChecked(PositionRoles.ROLE_ADMINISTRATOR, new AuthService.RunAs<Void>() {
            @Override
            Void run() {
                importer.doImport();
                return null;
            }
        });
    }
}