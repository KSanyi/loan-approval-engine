package hu.lae.infrastructure.demo;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import hu.lae.usermanagement.Authenticator;
import hu.lae.usermanagement.UserInfo;
import hu.lae.usermanagement.UserRole;

public class DemoAuthenticator implements Authenticator {

    @Override
    public UserInfo authenticateUser(String loginName, String password) throws AuthenticationException {
        
        UserInfo userInfo = users.keySet().stream()
                .filter(u -> u.loginName.equals(loginName))
                .findAny().orElseThrow(() -> new WrongPasswordException());
        
        if(password.equals(users.get(userInfo))) {
            return userInfo;
        }
        
        throw new WrongPasswordException();
    }
    
    public static final Map<UserInfo, String> users;
    
    static {
        Map<UserInfo, String> map = new HashMap<>();
        map.put(new UserInfo("tgyor", "Gy�r Tam�s", UserRole.RiskManager), "xxx");
        map.put(new UserInfo("egyor", "Gy�r Erika", UserRole.Analyst), "xxx");
        map.put(new UserInfo("bkiss", "Kiss B�la", UserRole.Sales), "xxx");
        map.put(new UserInfo("skocso", "K�cs� S�ndor", UserRole.Admin), "xxx");
        users = Collections.unmodifiableMap(map);
    }

}
