package hu.lae;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.lae.domain.riskparameters.InMemoryRiskParametersRepository;
import hu.lae.infrastructure.authenticator.UserServiceAuthenitcator;
import hu.lae.infrastructure.server.ApplicationService;
import hu.lae.infrastructure.server.LaeServer;
import hu.lae.util.Clock;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    
    public static void main(String[] args) throws Exception {

        int port = getPort();
        
        String userServiceUrl = getUserServiceUrl();

        ApplicationService applicationService = new ApplicationService(new UserServiceAuthenitcator(userServiceUrl), new InMemoryRiskParametersRepository());

        Clock.setStaticDate(LocalDate.of(2017, 4, 1));
        
        new LaeServer(port, applicationService).startServer();
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        if (port == null) {
            throw new IllegalArgumentException("System environment variable PORT is missing");
        }

        try {
            int portNumber = Integer.parseInt(port);
            logger.info("PORT: " + port);
            return portNumber;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Illegal system environment variable PORT: " + port);
        }
    }
    
    private static String getUserServiceUrl() {
        String userServiceUrl = System.getenv("USER_SERVICE_URL");
        if (userServiceUrl == null) {
            throw new IllegalArgumentException("System environment variable USER_SERVICE_URL is missing");
        }
        logger.info("USER_SERVICE_URL: " + userServiceUrl);
        return userServiceUrl;
    }

}
