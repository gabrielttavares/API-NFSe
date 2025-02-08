package br.com.mcp.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.mcp.service.TokenService;

@RestController
@RequestMapping("/api/v1/tokens")
public class TokenController {
	private final TokenService tokenService;
	
	public TokenController(TokenService tokenService) {
		this.tokenService = tokenService;
	}
	
	@PostMapping("/generate")
	public ResponseEntity<TokenResponse> generateToken(@RequestParam String prestadorId) {
		String token = tokenService.gerarToken(prestadorId);
		System.out.println("TOKEN GERADO: " + token);
		return ResponseEntity.ok(new TokenResponse(token));
	}
}

record TokenResponse(String token) {}
