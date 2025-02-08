package br.com.mcp.service;

import br.com.mcp.dto.NfseRequest;
import br.com.mcp.dto.NfseResponse;

import java.nio.channels.NonWritableChannelException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//Lógica de negócio principal
//Age como uma ponte entre a API e o serviço backend
//Processa requisições antes de enviá-las ao backend

@Service
public class NfseGatewayService {
	private final RestTemplate restTemplate;
	private final TokenService tokenService;
	
	@Value("${nfse.backend.url}")
	private String backendUrl;
	
	public NfseGatewayService(RestTemplate restTemplate, TokenService tokenService) {
		this.restTemplate = restTemplate;
		this.tokenService = tokenService;
	}
	
	public NfseResponse gerarNotaFiscal(String token, NfseRequest request) {
	    try {
	        // Valida token e obtém prestador
	        String prestadorId = tokenService.validarEObterPrestador(token);

	        //Mock response
	        return NfseResponse.builder()
	        		.id(123L)
	        		.numeroNfse("NFSe-2024-001")
	                .codVerificacao("ABC123XYZ")
	                .xml("<nfse>...</nfse>")
	                .status("OK")
	                .build();

	    } catch (Exception e) {
	    	return NfseResponse.builder()
	                .status("ERRO")
	                .erros(new String[]{"Erro ao processar a requisição: " + e.getMessage()})
	                .build();
	    }
	}
	
	public NfseResponse consultarRPS(String token, Long numeroRPS) {
		try {
            String prestadorId = tokenService.validarEObterPrestador(token);
            
            // Mock response
            return NfseResponse.builder()
                .id(numeroRPS)
                .numeroNfse("NFSe-2024-001")
                .codVerificacao("ABC123XYZ")
                .xml("<nfse>...</nfse>")
                .status("OK")
                .build();

        } catch (Exception e) {
            return NfseResponse.builder()
                .status("ERRO")
                .erros(new String[]{"Erro ao consultar RPS: " + e.getMessage()})
                .build();
        }
    }
	
	public NfseResponse cancelarNfse(String token, Long numeroNfse) {
        try {
            String prestadorId = tokenService.validarEObterPrestador(token);
            
            // Mock response
            return NfseResponse.builder()
        		.id(numeroNfse)
                .numeroNfse(numeroNfse.toString())
                .codVerificacao("ABC123XYZ")
                .xml("<cancelamento><nfse>" + numeroNfse + "</nfse></cancelamento>")
                .status("OK")
                .build();

        } catch (Exception e) {
            return NfseResponse.builder()
                .status("ERRO")
                .erros(new String[]{"Erro ao cancelar NFSe: " + e.getMessage()})
                .build();
        }
    }
}
