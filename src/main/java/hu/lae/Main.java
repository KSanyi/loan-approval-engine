package hu.lae;

import hu.lae.infrastructure.demo.DemoAuthenticator;
import hu.lae.infrastructure.server.ApplicationService;
import hu.lae.infrastructure.server.LaeServer;
import hu.lae.riskparameters.InMemoryRiskParametersRepository;

public class Main {

    public static void main(String[] args) throws Exception {

        int port = getPort();

        ApplicationService applicationService = new ApplicationService(new DemoAuthenticator(), new InMemoryRiskParametersRepository());

        new LaeServer(port, applicationService).startServer();
    }

    private static int getPort() {
        String port = System.getenv("PORT");
        if (port == null) {
            throw new IllegalArgumentException("System environment variable PORT is missing");
        }

        try {
            return Integer.parseInt(port);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Illegal system environment variable PORT: " + port);
        }
    }

}
