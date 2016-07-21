package kashio.hook;

import com.atlassian.plugin.spring.scanner.annotation.component.Scanned;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.stash.hook.repository.AsyncPostReceiveRepositoryHook;
import com.atlassian.stash.hook.repository.RepositoryHookContext;
import com.atlassian.stash.repository.RefChange;
import com.atlassian.stash.repository.RefChangeType;
import com.atlassian.stash.repository.Repository;
import com.atlassian.stash.setting.RepositorySettingsValidator;
import com.atlassian.stash.setting.Settings;
import com.atlassian.stash.setting.SettingsValidationErrors;
import com.atlassian.stash.ssh.api.SshCloneUrlResolver;
import kashio.AuthorizationStore;
import kashio.BambooBuildQueueService;
import kashio.BranchPlanKeyProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;

@Scanned
public class BambooPlanPushTriggerRepositoryHook implements AsyncPostReceiveRepositoryHook, RepositorySettingsValidator
{
    private final SshCloneUrlResolver sshCloneUrlResolver;

    @Autowired
    public BambooPlanPushTriggerRepositoryHook(@ComponentImport final SshCloneUrlResolver sshCloneUrlResolver)
    {
        this.sshCloneUrlResolver = sshCloneUrlResolver;
    }

    /**
     * Connects to a configured URL to notify of all changes.
     */
    public void postReceive(RepositoryHookContext context, Collection<RefChange> refChanges)
    {
        final String bambooApiUrl = context.getSettings().getString("url");
        final String repositorySshCloneUrl = sshCloneUrlResolver.getCloneUrl(context.getRepository());
        if (bambooApiUrl != null && repositorySshCloneUrl != null)
        {
            final String planKey = context.getSettings().getString("planKey");
            final AuthorizationStore authorizationStore = new AuthorizationStore(context.getSettings().getString("user"),
                    context.getSettings().getString("password"));
            for (RefChange refChange : refChanges)
            {
                if (refChange.getType() == RefChangeType.ADD || refChange.getType() == RefChangeType.UPDATE)
                {
                    final BranchPlanKeyProvider branchPlanKeyProvider = new BranchPlanKeyProvider(authorizationStore,
                            bambooApiUrl, refChange.getRefId(), planKey);
                    final BambooBuildQueueService bambooBuildQueueService = new BambooBuildQueueService(authorizationStore,
                            branchPlanKeyProvider, refChange, bambooApiUrl, repositorySshCloneUrl);
                    try {
                        bambooBuildQueueService.build();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void validate(Settings settings, SettingsValidationErrors errors, Repository repository)
    {
        if (settings.getString("url", "").isEmpty()) {
            errors.addFieldError("url", "Url field is blank, please supply one");
        }
        if (settings.getString("user", "").isEmpty()) {
            errors.addFieldError("user", "User field is blank, please supply one");
        }
        if (settings.getString("password", "").isEmpty()) {
            errors.addFieldError("password", "Password field is blank, please supply one");
        }
        if (settings.getString("planKey", "").isEmpty()) {
            errors.addFieldError("planKey", "Plan key field is blank, please supply one");
        }
    }
}