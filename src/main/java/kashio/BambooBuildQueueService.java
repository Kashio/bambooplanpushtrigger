package kashio;

import com.atlassian.stash.repository.RefChange;
import com.atlassian.stash.ssh.api.SshCloneUrlResolver;
import com.atlassian.stash.ssh.api.SshConfiguration;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

/**
 * Created by Roy on 7/20/2016.
 */
public class BambooBuildQueueService extends AtlassianRestConnector {
    private final String QUEUE_RESOURCE = "/queue/";

    private final BranchPlanKeyProvider branchPlanKeyProvider;
    private final RefChange refChange;
    private final String bambooApiUrl;
    private final String repositorySshCloneUrl;

    public BambooBuildQueueService(AuthorizationStore authorizationStore,
                                   BranchPlanKeyProvider branchPlanKeyProvider,
                                   RefChange refChange,
                                   String bambooApiUrl,String repositorySshCloneUrl) {
        super(authorizationStore);
        this.branchPlanKeyProvider = branchPlanKeyProvider;
        this.refChange = refChange;
        this.bambooApiUrl = bambooApiUrl;
        this.repositorySshCloneUrl = repositorySshCloneUrl;
    }

    public AuthorizationStore getAuthorizationStore() {
        return authorizationStore;
    }

    public BranchPlanKeyProvider getBranchPlanKeyProvider() {
        return branchPlanKeyProvider;
    }

    public RepositoryCloneUrlProvider getRepositoryCloneUrlBuilder() {
        return repositoryCloneUrlProvider;
    }

    public RefChange getRefChange() {
        return refChange;
    }

    public String getBambooApiUrl() {
        return bambooApiUrl;
    }

    public int build() throws IOException {
        final String finalUrl = bambooApiUrl + QUEUE_RESOURCE + branchPlanKeyProvider.getBranchPlayKey()
                + "?ExecuteAllStages"
                + "&bamboo.variable.repositoryUrl=" + repositorySshCloneUrl
                + "&bamboo.variable.branchName=" + refChange.getRefId();
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(finalUrl);
        httpPost.addHeader("Authorization", "Basic " + createAuthorizationEncoding());
        HttpResponse response = httpClient.execute(httpPost);
        return response.getStatusLine().getStatusCode();
    }
}
