package at.ennui.backend.security;

import org.bouncycastle.util.Strings;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManager authenticationManager;
    private final String tokenType;

    public TokenAuthenticationFilter(AuthenticationManager manager, String tokenType){
        this.authenticationManager = manager;
        this.tokenType = tokenType;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        final String authorization = httpServletRequest.getHeader("Authorization");
        try{
            if(authorization != null && !authorization.isEmpty() && authorization.toLowerCase().startsWith(tokenType.toLowerCase() + " ")){
                String[] tokenTypeAndToken = authorization.split(" ");
                final UserAuthentication tokenAuthentication = new UserAuthentication();
                tokenAuthentication.setCredentials(tokenTypeAndToken[1]);
                SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(tokenAuthentication));
            }
            else{
                throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED,"No token provided!");
            }
            filterChain.doFilter(httpServletRequest,httpServletResponse);
        }
        //add some more catch for specific exceptions
        catch(Exception e){
            SecurityContextHolder.clearContext();
            httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,e.getMessage());
        }
    }
}
