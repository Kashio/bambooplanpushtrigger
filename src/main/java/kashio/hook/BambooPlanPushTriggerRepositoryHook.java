package kashio.hook;

import com.atlassian.stash.hook.repository.*;
import com.atlassian.stash.repository.*;
import com.atlassian.stash.setting.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.client.HttpClient;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

public class BambooPlanPushTriggerRepositoryHook implements AsyncPostReceiveRepositoryHook, RepositorySettingsValidator
{
    /**
     * Connects to a configured URL to notify of all changes.
     */
    public void postReceive(RepositoryHookContext context, Collection<RefChange> refChanges)
    {
        String url = context.getSettings().getString("url");
        if (url != null)
        {
            try
            {
                new URL(url).openConnection().getInputStream().close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void validate(Settings settings, SettingsValidationErrors errors, Repository repository)
    {
        if (settings.getString("url", "").isEmpty())
        {
            errors.addFieldError("url", "Url field is blank, please supply one");
        }
    }
}