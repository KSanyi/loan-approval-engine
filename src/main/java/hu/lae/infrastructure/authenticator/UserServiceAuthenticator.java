package hu.lae.infrastructure.authenticator;

import java.lang.invoke.MethodHandles;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.body.RequestBodyEntity;

import hu.lae.usermanagement.Authenticator;
import hu.lae.usermanagement.UserInfo;
import hu.lae.usermanagement.UserRole;

public class UserServiceAuthenticator implements Authenticator {

    private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    private final static String DOMAIN = "lae";
    
    private final String userServiceUrl;
    
    public UserServiceAuthenticator(String userServiceBaseUrl) {
        userServiceUrl = userServiceBaseUrl + "/user/" + DOMAIN + "/{userId}/authenticate";
    }

	@Override
	public UserInfo authenticateUser(String userId, String password) throws AuthenticationException {
		try {
			RequestBodyEntity request = Unirest.post(userServiceUrl)
					.header("Content-Type", "application/json")
					.routeParam("userId", userId)
					.body(new JSONObject().put("password", password));
			
			logger.info("Calling userService: {}", request.getHttpRequest().getUrl());
			HttpResponse<JsonNode> response = request.asJson();
			
			logger.info("Response status: " + response.getStatusText());
			
			if(response.getStatus() == 401) {
	            throw new WrongPasswordException();
			}
			
			JSONObject responseJsonObject = response.getBody().getObject();
			
			return new UserInfo(userId,
					responseJsonObject.getString("name"),
					UserRole.valueOf(responseJsonObject.getString("role")));
		} catch (UnirestException ex) {
			logger.error("Error calling kits-user-service: " + ex.getMessage());
			throw new AuthenticationException(ex.getMessage());
		}
	}
    
}
