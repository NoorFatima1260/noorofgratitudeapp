package noorofgratitute.com;
public class LoginRequest {
    private String username;
    private String password;
    private String email;
    public LoginRequest(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email; }
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
