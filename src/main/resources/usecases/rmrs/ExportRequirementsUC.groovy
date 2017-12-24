package usecases.rmrs

import com.firstlinesoftware.rmrs.server.exporters.RequirementsContentExporter
import org.jboss.resteasy.annotations.cache.NoCache
import org.springframework.beans.factory.annotation.Autowired
import usecases.base.BaseUC

import javax.ws.rs.GET
import javax.ws.rs.QueryParam

class ExportRequirementsUC extends BaseUC<Void> {
    @Autowired
    private RequirementsContentExporter exporter;

    @NoCache
    @GET
    String run(@QueryParam("parent") final String parent, @QueryParam("date") Long date) {
        return exporter.export(parent, new Date(date));
    }
}