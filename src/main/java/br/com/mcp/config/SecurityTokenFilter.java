package br.com.mcp.config;

import br.com.mcp.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.var;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

public class SecurityTokenFilter extends OncePerRequestFilter {

	private final TokenService tokenService;

	public SecurityTokenFilter(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		var token = recoverToken(request);
		System.out.println("Token recebido: " + token);
		if (token != null) {
			try {
				var prestadorId = tokenService.validarEObterPrestador(token);
				System.out.println("PrestadorId extraído: " + prestadorId);
				var authentication = new UsernamePasswordAuthenticationToken(prestadorId, null,
						Collections.emptyList());
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception e) {
				System.out.println("Validação do token falhou: " + e.getMessage());
				SecurityContextHolder.clearContext();
			}
		}

		filterChain.doFilter(request, response);
	}

	private String recoverToken(HttpServletRequest request) {
		var authHeader = request.getHeader("Authorization");
		if (authHeader == null || !authHeader.startsWith("Bearer "))
			return null;
		return authHeader.substring(7);
	}
}
