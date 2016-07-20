package kashio;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.stash.repository.Repository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Roy on 7/20/2016.
 */
public abstract class RepositoryCloneUrlBuilder {
    protected final String SSH_PREFIX = "ssh://";
    protected final String HTTP_PREFIX = "http://";

    protected final Repository repository;
    private final ApplicationProperties applicationProperties;

    protected RepositoryCloneUrlBuilder(Repository repository, final ApplicationProperties applicationProperties) {
        this.repository = repository;
        this.applicationProperties = applicationProperties;
    }

    protected String getCurrentAppBaseUrl()
    {
        Pattern baseUrlPattern = Pattern.compile("[^/:]+:\\d+/");
        Matcher matcher = baseUrlPattern.matcher(applicationProperties.getBaseUrl(UrlMode.ABSOLUTE));
        return matcher.group(0);
    }

    protected String getRepositoryProjectAndName()
    {
        Pattern repositoryProjectAndNamePattern = Pattern.compile("\\w+/\\w+");
        Matcher matcher = repositoryProjectAndNamePattern.matcher(repository.toString());
        return matcher.group(0);
    }

    public abstract String buildSsh();

    public abstract String buildHttp();
}
