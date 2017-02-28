package hu.lae.usermanagement;

public class UserInfo {

    public final String loginName;
    
    public final String name;
    
    public final UserRole role;
    
    public UserInfo(String loginName, String name, UserRole role) {
        this.loginName = loginName;
        this.name = name;
        this.role = role;
    }

}
