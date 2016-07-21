package kashio;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.stash.repository.Repository;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Roy on 7/21/2016.
 */
public class RepositoryCloneUrlProvider extends AtlassianRestConnector {
    private final String REST_API = "rest/api/latest/";
    private final String PROJECTS_RESOURCE = "/projects/";
    private final String REPOS_RESOURCE = "/repos/";

    private final Repository repository;
    private final ApplicationProperties applicationProperties;

    public RepositoryCloneUrlProvider(AuthorizationStore authorizationStore, Repository repository,
                                         ApplicationProperties applicationProperties) {
        super(authorizationStore);
        this.repository = repository;
        this.applicationProperties = applicationProperties;
    }

    public Repository getRepository() {
        return repository;
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }

    private String getCurrentAppBaseUrl()
    {
        Pattern baseUrlPattern = Pattern.compile("[^/:]+:\\d+/");
        Matcher matcher = baseUrlPattern.matcher(applicationProperties.getBaseUrl(UrlMode.ABSOLUTE));
        return matcher.group(0);
    }

    private Pair<String, String> getRepositoryProjectAndName()
    {
        Pattern repositoryProjectAndNamePattern = Pattern.compile("(\\w+)/(\\w+)");
        Matcher matcher = repositoryProjectAndNamePattern.matcher(repository.toString());
        return new ImmutablePair<String, String>(matcher.group(1), matcher.group(2));
    }

    public String getCloneUrl(String protocol) throws IOException {
        final Pair<String, String> repoProjectAndName = getRepositoryProjectAndName();
        final String finalUrl = getCurrentAppBaseUrl() + REST_API + PROJECTS_RESOURCE +
                repoProjectAndName.getKey() + REPOS_RESOURCE + repoProjectAndName.getValue();
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(finalUrl);
        httpGet.addHeader("Authorization", "Basic " + createAuthorizationEncoding());
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String body = IOUtils.toString(entity.getContent(), "UTF-8");
            Matcher matcher;
            Pattern cloneUrlPattern = Pattern.compile("\"clone\":\\s\\[\\r?\\n\\s+\\{\\r?\\n\\s+\"href\":\\s\"(.+?)\",\\r?\\n\\s+\"name\":\\s\"" + protocol + "\"\\r?\\n\\s+\\},",
                    Pattern.DOTALL);
            matcher = cloneUrlPattern.matcher(body);
            return matcher.group(1);
        }
        return "";
    }
}
