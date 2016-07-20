package kashio;

import com.atlassian.stash.repository.RefChange;
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
    private final RepositoryCloneUrlBuilder repositoryCloneUrlBuilder;
    private final RefChange refChange;
    private final String bambooApiUrl;

    public BambooBuildQueueService(AuthorizationStore authorizationStore,
                                   BranchPlanKeyProvider branchPlanKeyProvider,
                                   RepositoryCloneUrlBuilder repositoryCloneUrlBuilder,
                                   RefChange refChange,
                                   String bambooApiUrl) {
        super(authorizationStore);
        this.branchPlanKeyProvider = branchPlanKeyProvider;
        this.repositoryCloneUrlBuilder = repositoryCloneUrlBuilder;
        this.refChange = refChange;
        this.bambooApiUrl = bambooApiUrl;
    }

    public AuthorizationStore getAuthorizationStore() {
        return authorizationStore;
    }

    public BranchPlanKeyProvider getBranchPlanKeyProvider() {
        return branchPlanKeyProvider;
    }

    public RepositoryCloneUrlBuilder getRepositoryCloneUrlBuilder() {
        return repositoryCloneUrlBuilder;
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
                + "&bamboo.variable.repositoryUrl=" + repositoryCloneUrlBuilder.buildSsh()
                + "&bamboo.variable.branchName=" + refChange.getRefId();
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(finalUrl);
        httpPost.addHeader("Authorization", "Basic " + createAuthorizationEncoding());
        HttpResponse response = httpClient.execute(httpPost);
        return response.getStatusLine().getStatusCode();
    }
}
