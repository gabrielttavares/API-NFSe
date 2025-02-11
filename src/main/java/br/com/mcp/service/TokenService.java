package br.com.mcp.service;

import java.security.Key;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {
	@Value("${api.security.token.secret}")
	private String secret;
	
	public String validarEObterPrestador(String token) {
		
		System.out.println("TESTE");
		if (token == null || token.isEmpty()) {
			throw new SecurityException("Token não fornecido");
		}
		
		try {
			//validação do token e extração do prestadorId
			//usar biblioteca JWT para validar token
			
			if (token.startsWith("Bearer ")) {
				token = token.substring(7);
			}
			
			Claims claims = Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
			
			if (claims.getExpiration().before(new Date())) {
				throw new SecurityException("Token expirado.");
			}
			return claims.getSubject();
		} catch (Exception e) {
			e.printStackTrace();
			throw new SecurityException("Token inválido.");
		}
	}
	
	public String gerarToken(String prestadorId) {
		try {
			Calendar expiration = Calendar.getInstance();
			expiration.set(Calendar.YEAR, 2050);
			
			return Jwts.builder()
					.setIssuer("NFSe API")        // quem emitiu o token
	                .setSubject(prestadorId)      // para qual prestador
	                .setIssuedAt(Calendar.getInstance().getTime())             // quando foi emitido
	                .setExpiration(expiration.getTime())     // quando expira
//	                .setId(UUID.randomUUID().toString())
	                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
	                .compact();
		} catch (Exception e) {
			throw new RuntimeException("Erro ao gerar token", e);
		}
	}
	
	private Key getSigningKey() {
		byte[] keyBytes = Base64.getDecoder().decode(secret);
		
		if (keyBytes.length < 32) {
	        throw new IllegalArgumentException("Chave secreta deve ter pelo menos 256 bits.");
	    }
		
        return Keys.hmacShaKeyFor(keyBytes);
	}
}
