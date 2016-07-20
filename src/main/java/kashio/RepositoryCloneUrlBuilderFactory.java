package kashio;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.stash.repository.Repository;

/**
 * Created by Roy on 7/20/2016.
 */
public class RepositoryCloneUrlBuilderFactory {
    private final Repository repository;
    private final ApplicationProperties applicationProperties;

    public RepositoryCloneUrlBuilderFactory(final Repository repository, final ApplicationProperties applicationProperties) {
        this.repository = repository;
        this.applicationProperties = applicationProperties;
    }

    public RepositoryCloneUrlBuilder build(String scmId) {
        if ("git".equals(scmId)) {
            return new GitRepositoryCloneUrlBuilder(repository, applicationProperties);
        }
        return null;
    }
}
