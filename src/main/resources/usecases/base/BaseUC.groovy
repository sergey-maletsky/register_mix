package usecases.base

import com.firstlinesoftware.base.server.services.AuthService
import com.firstlinesoftware.base.server.services.RepositoryService
import com.firstlinesoftware.base.server.utils.Messages
import org.springframework.beans.factory.annotation.Autowired

/**
 * User: AAbushkevich
 * Date: 08.07.13
 * Time: 14:22
 */

public class BaseUC<T> {
    @Autowired
    protected RepositoryService repositoryService;
    @Autowired
    protected Messages messages;
    @Autowired
    protected AuthService authService;

}