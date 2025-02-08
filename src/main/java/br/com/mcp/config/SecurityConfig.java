package br.com.mcp.config;

import br.com.mcp.service.TokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final TokenService tokenService;

	public SecurityConfig(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session
						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST, "/api/v1/tokens/generate").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/nfse").authenticated()
						.anyRequest()
						.authenticated())
				.addFilterBefore(new SecurityTokenFilter(tokenService), 
								UsernamePasswordAuthenticationFilter.class)
				.build();
	}
}
