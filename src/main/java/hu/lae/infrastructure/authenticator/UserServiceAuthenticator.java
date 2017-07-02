package hu.lae.infrastructure.authenticator;

import java.lang.invoke.MethodHandles;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.usermanagement.Authenticator;
import hu.lae.usermanagement.UserInfo;
import hu.lae.usermanagement.UserRole;

public class UserServiceAuthenticator implements Authenticator {

    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final static String DOMAIN = "lae";
    
    private final String userServiceUrl;
    
    public UserServiceAuthenticator(String userServiceUrl) {
        this.userServiceUrl = userServiceUrl;
    }
    
    @Override
    public UserInfo authenticateUser(String userId, String password) throws AuthenticationException {
        
        Client client = ClientBuilder.newClient();
        
        WebTarget webtarget = client.target(userServiceUrl)
                .path("user/" + DOMAIN + "/" + userId + "/authenticate");

        logger.info("Calling userService: {}", webtarget.getUri());
        
        try {
            JsonObject requestJsonObject = Json.createObjectBuilder()
                    .add("password", password)
                    .build();
            Entity<JsonObject> entity = Entity.entity(requestJsonObject, MediaType.APPLICATION_JSON);
            JsonObject responseJsonObject = webtarget.request(MediaType.APPLICATION_JSON).post(entity, JsonObject.class);
            logger.info("Result: {}", responseJsonObject);
            return new UserInfo(userId, responseJsonObject.getString("name"), UserRole.valueOf(responseJsonObject.getString("role")));
        } catch(NotAuthorizedException ex) {
            logger.warn("NotAuthorizedException: " + ex.getMessage());
            throw new WrongPasswordException();
        } catch(Exception ex) {
            logger.error("Error calling kits-user-service: " + ex.getMessage());
            throw new AuthenticationException(ex.getMessage());
        }
    }
    
}
