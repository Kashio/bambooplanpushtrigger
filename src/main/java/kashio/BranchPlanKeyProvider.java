package kashio;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Roy on 7/20/2016.
 */
public class BranchPlanKeyProvider extends AtlassianRestConnector{
    private final String PLAN_RESOURCE = "/plan/";
    private final String BRANCH_RESOURCE = "/branch";

    private final String bambooApiUrl;
    private final String branchName;
    private final String planKey;

    public BranchPlanKeyProvider(AuthorizationStore authorizationStore, String bambooApiUrl, String branchName, String planKey) {
        super(authorizationStore);
        this.bambooApiUrl = bambooApiUrl;
        Pattern branchShortNamePattern = Pattern.compile("refs/heads/(.+)");
        Matcher matcher = branchShortNamePattern.matcher(branchName);
        branchName = matcher.group(1);
        branchName = branchName.replace('/', '-');
        this.branchName = branchName;
        this.planKey = planKey;
    }

    public AuthorizationStore getAuthorizationStore() {
        return authorizationStore;
    }

    public String getBambooApiUrl() {
        return bambooApiUrl;
    }

    public String getBranchName() {
        return branchName;
    }

    public String getPlanKey() {
        return planKey;
    }

    public String getBranchPlayKey() throws IOException {
        final String finalUrl = bambooApiUrl + PLAN_RESOURCE + planKey + BRANCH_RESOURCE;
        HttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(finalUrl);
        httpGet.addHeader("Authorization", "Basic " + createAuthorizationEncoding());
        HttpResponse response = httpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            String body = IOUtils.toString(entity.getContent(), "UTF-8");
            Matcher matcher;
            Pattern branchesPattern = Pattern.compile("shortName=\"" + branchName + "\"\\sshortKey=\"(\\w+)\"", Pattern.DOTALL);
            matcher = branchesPattern.matcher(body);
            return matcher.group(1);
        }
        return planKey;
    }

}
