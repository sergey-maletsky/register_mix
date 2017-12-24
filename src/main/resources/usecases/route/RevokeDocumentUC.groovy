package usecases.route

import com.firstlinesoftware.base.server.events.ServerEventBus
import com.firstlinesoftware.base.server.services.AuthService
import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.base.server.utils.Messages
import com.firstlinesoftware.base.shared.dto.DTO
import com.firstlinesoftware.ecm.server.services.DocumentService
import com.firstlinesoftware.ecm.server.services.NotificationEmailService
import com.firstlinesoftware.ecm.server.services.PositionFoldersService
import com.firstlinesoftware.ecm.shared.dto.Document
import com.firstlinesoftware.orgstruct.server.services.OrgstructService
import com.firstlinesoftware.orgstruct.shared.dto.Position
import com.firstlinesoftware.rmrs.server.services.RequirementsService
import com.firstlinesoftware.rmrs.shared.dto.Requirement
import com.firstlinesoftware.route.server.services.CommonRouteService
import com.firstlinesoftware.route.server.services.DocumentRouteService
import com.firstlinesoftware.route.server.services.impl.RouteEventSender
import com.firstlinesoftware.route.shared.dto.AbstractRoute
import com.firstlinesoftware.route.shared.events.RouteMemberTaskCreatedServerEvent
import com.firstlinesoftware.route.shared.events.RouteServerEvent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import usecases.orgstruct.CheckedUC

import javax.ws.rs.POST
import javax.ws.rs.QueryParam

/**
 * User: VAntonov
 * Date: 13.01.11
 * Time: 10:50
 */
@Component
@SuppressWarnings("unused")
public class RevokeDocumentUC extends CheckedUC {
    @Autowired
    DocumentRouteService documentRouteService;
    @Autowired
    DocumentService documentService;
    @Autowired
    private CommonRouteService commonRouteService
    @Autowired
    private PositionFoldersService positionFoldersService
    @Autowired
    private RouteEventSender routeEventSender
    @Autowired
    private NotificationEmailService notificationEmailService
    @Autowired
    private RequirementsService requirementsService
    @Autowired
    private OrgstructService orgstructService
    @Autowired
    private Messages messages

    private Object revoke(final String id) throws Exception {
        final AbstractRoute route = documentService.get(id);

        if (route != null) {
            Position position = route.author
            if (route instanceof  Requirement) {
                final String responsibleId = requirementsService.getResponsible(route.id)
                if (responsibleId != null) {
                    position = orgstructService.getPosition(responsibleId)
                }
            }

            return runCheckedPosition(position.id, new AuthService.RunAs<String>() {
                @Override
                public String run() throws Exception {
                    documentRouteService.revokeDocument(id)
                    returnToAuthor(route, position)
                    return null
                }
            });
        }
        return null;

    }

    private void returnToAuthor(AbstractRoute route, Position position) {
        positionFoldersService.link(route.id, position.id, AbstractRoute.DOCUMENT_LIFECYCLE_DRAFT)

        final Document updated = commonRouteService.createUpdated(route)
        updated.id = route.id
        commonRouteService.setLifecycle(updated, AbstractRoute.DOCUMENT_LIFECYCLE_DRAFT, null)
        documentService.setReadWrite(route.id, position.id)

        final Document afterUpdate = documentService.get(route.id)
        ServerEventBus.addPendingEvent(position.id, createRouteServerEvent(afterUpdate, position))
        notificationEmailService.sendTask(route.id, position.id, AbstractRoute.LIFECYCLE_REJECTED)
    }

    private RouteServerEvent createRouteServerEvent(Document document, Position position) {
        final RouteServerEvent event = new RouteMemberTaskCreatedServerEvent()
        event.id = document.id
        event.position = position
        event.lifecycle = AbstractRoute.DOCUMENT_LIFECYCLE_DRAFT
        event.document = document
        event.message = this.messages.getMessage("task.created." + AbstractRoute.LIFECYCLE_REJECTED)
        return event
    }

    @POST
    public Object run(final DTO object, @QueryParam("id") final String id) {
        return repositoryService.doInTransaction(false, new RepositoryService.RetryingTransactionCallback<Object>() {
            @Override
            public Object execute() throws Exception {
                return revoke(id);
            }
        });
    }
}
