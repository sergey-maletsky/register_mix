package usecases.rmrs

import com.firstlinesoftware.base.server.services.AuthService
import com.firstlinesoftware.orgstruct.shared.dto.PositionRoles
import com.firstlinesoftware.rmrs.server.importers.RequirementsXmlImporter
import org.jboss.resteasy.annotations.GZIP
import org.jboss.resteasy.annotations.cache.NoCache
import org.springframework.beans.factory.annotation.Autowired
import usecases.orgstruct.CheckedUC

import javax.ws.rs.POST
import javax.ws.rs.QueryParam

class ImportRequirementsUC extends CheckedUC<Void> {
    @Autowired
    protected RequirementsXmlImporter importer;

    @NoCache
    @POST
    Void run(@QueryParam("filename") final String filename) {
        runChecked(PositionRoles.ROLE_ADMINISTRATOR, new AuthService.RunAs<Void>() {
            @Override
            Void run() {
                def uploadedXml = new File(repositoryService.getTempDir(), filename)
                if (!uploadedXml.exists()) {
                    throw new IllegalStateException("Cannot find a file containing requirement to be imported: ${uploadedXml.getAbsolutePath()}")
                }
                importer.importZip(uploadedXml)
                return null;
            }
        });
    }
}