package pl.tul.discountmanagement.model.dto.security;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Custom implementation of {@link AbstractAuthenticationToken}. The object will allow to pass user details and JWT object
 * to the request context.
 */
public class AuthenticationTokenDTO extends AbstractAuthenticationToken {

    private final Jwt jwt;
    private final UserDetailsDTO userDetailsDTO;

    /**
     * Creates a custom token with the supplied array of authorities, JWT details and user details.
     *
     * @param authorities    the collection of <tt>GrantedAuthority</tt>s for the principal represented by this
     *                       authentication object.
     * @param jwt            JWT representation
     * @param userDetailsDTO custom principal containing user details
     */
    public AuthenticationTokenDTO(Collection<? extends GrantedAuthority> authorities, Jwt jwt, UserDetailsDTO userDetailsDTO) {
        super(authorities);
        this.userDetailsDTO = userDetailsDTO;
        this.jwt = jwt;
    }

    /**
     * Overridden credential getter, which allow to return stored JWT contents.
     *
     * @return JWT object
     */
    @Override
    public Jwt getCredentials() {
        return jwt;
    }

    /**
     * Overridden principal getter, which allow to return stored user details.
     *
     * @return user details
     */
    @Override
    public UserDetailsDTO getPrincipal() {
        return userDetailsDTO;
    }
}
