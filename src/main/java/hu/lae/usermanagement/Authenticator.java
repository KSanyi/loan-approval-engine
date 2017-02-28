package hu.lae.usermanagement;

public interface Authenticator {

    UserInfo authenticateUser(String userName, String password) throws AuthenticationException;
    
    @SuppressWarnings("serial")
    static class AuthenticationException extends Exception {
        public AuthenticationException(String errorMessage) {
            super(errorMessage);
        }
    }
    
    @SuppressWarnings("serial")
    static class WrongPasswordException extends AuthenticationException {
        public WrongPasswordException() {
            super("Wrong user name or password");
        }
    }
    
}
