package kashio;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.stash.repository.Repository;

/**
 * Created by Roy on 7/20/2016.
 */
public class GitRepositoryCloneUrlBuilder extends RepositoryCloneUrlBuilder {
    protected GitRepositoryCloneUrlBuilder(Repository repository, ApplicationProperties applicationProperties) {
        super(repository, applicationProperties);
    }

    public String buildSsh() {
        return SSH_PREFIX + repository.getScmId() + '@' + getCurrentAppBaseUrl() +
                getRepositoryProjectAndName() + '.' + repository.getScmId();
    }

    public String buildHttp() {
        return HTTP_PREFIX + getCurrentAppBaseUrl() + "scm/" +
                getRepositoryProjectAndName() + '.' + repository.getScmId();
    }
}
