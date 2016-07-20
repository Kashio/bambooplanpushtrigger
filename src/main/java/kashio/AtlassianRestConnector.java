package kashio;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by Roy on 7/20/2016.
 */
public abstract class AtlassianRestConnector {
    protected final AuthorizationStore authorizationStore;

    protected AtlassianRestConnector(AuthorizationStore authorizationStore) {
        this.authorizationStore = authorizationStore;
    }

    public AuthorizationStore getAuthorizationStore() {
        return authorizationStore;
    }

    protected String createAuthorizationEncoding() {
        final Base64 base64 = new Base64();
        return base64
                .encodeAsString((authorizationStore.getUserName() + ":" + authorizationStore.getPassword())
                .getBytes());
    }
}
