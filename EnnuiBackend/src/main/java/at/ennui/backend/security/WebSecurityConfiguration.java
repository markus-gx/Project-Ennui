package at.ennui.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.Filter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String TOKEN_TYPE = "Bearer";
    @Autowired
    private TokenAuthenticationProvider tokenAuthenticationProvider;

    @Bean("authenticationManagerBean")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //Add Paths which should be ignored by authentication
        web.ignoring().antMatchers("/games/activated").and()
                .ignoring().antMatchers(HttpMethod.GET,"/events").and()
                .ignoring().antMatchers("/information/statistics").and()
                .ignoring().antMatchers(HttpMethod.GET,"/offers").and()
                .ignoring().antMatchers(HttpMethod.GET,"/offers/*").and()
                .ignoring().antMatchers(HttpMethod.GET,"/events/place");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS).and()
                .addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    private Filter tokenAuthenticationFilter() throws Exception{
        return new TokenAuthenticationFilter(authenticationManager(),TOKEN_TYPE);
    }
}
