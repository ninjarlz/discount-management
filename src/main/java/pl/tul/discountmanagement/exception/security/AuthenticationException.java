package pl.tul.discountmanagement.exception.security;

/**
 * The {@link Exception} for any authentication problems.
 */
public class AuthenticationException extends Exception {
    public AuthenticationException(String message) {
        super(message);
    }
}
