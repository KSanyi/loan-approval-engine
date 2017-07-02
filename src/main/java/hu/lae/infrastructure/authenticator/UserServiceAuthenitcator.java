package hu.lae.infrastructure.authenticator;

import java.lang.invoke.MethodHandles;

import javax.json.JsonObject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.usermanagement.Authenticator;
import hu.lae.usermanagement.UserInfo;
import hu.lae.usermanagement.UserRole;

public class UserServiceAuthenitcator implements Authenticator {

    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final static String DOMAIN = "lae";
    
    private final String userServiceUrl;
    
    public UserServiceAuthenitcator(String userServiceUrl) {
        this.userServiceUrl = userServiceUrl;
    }
    
    @Override
    public UserInfo authenticateUser(String userId, String password) throws AuthenticationException {
        
        Client client = ClientBuilder.newClient();
        
        WebTarget webtarget = client.target(userServiceUrl)
                .path("user/" + DOMAIN)
                .queryParam("userId", userId)
                .queryParam("password", password); 

        logger.info("Calling userService: {}", webtarget.getUri());
        
        try {
            JsonObject jsonObject = webtarget.request().get(JsonObject.class);
            logger.info("Result: {}", jsonObject);
            return new UserInfo(userId, jsonObject.getString("name"), UserRole.valueOf(jsonObject.getString("role")));
        } catch(NotAuthorizedException ex) {
            logger.warn("NotAuthorizedException: " + ex.getMessage());
            throw new WrongPasswordException();
        } catch(Exception ex) {
            logger.error("Error calling kits-user-service: " + ex.getMessage());
            throw new AuthenticationException(ex.getMessage());
        }
    }
    
}
