package kashio;

/**
 * Created by Roy on 7/20/2016.
 */
public class AuthorizationStore {
private String userName;
    private String password;

    public AuthorizationStore(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
