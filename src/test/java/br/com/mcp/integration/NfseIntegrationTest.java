package br.com.mcp.integration;

import br.com.mcp.dto.NfseRequest;
import br.com.mcp.dto.NfseResponse;
import br.com.mcp.service.NfseGatewayService;
import br.com.mcp.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class NfseIntegrationTest {

	@Autowired
	private TokenService tokenService;

	@Autowired
	private NfseGatewayService nfseService;

	@Test
	void deveCompletarFluxoNfse() {

		//arrange
		NfseRequest request = createSampleNfseRequest();
		String prestadorId = "123";

		//act
		String token = tokenService.gerarToken(prestadorId);
		NfseResponse nfseResponse = nfseService.gerarNotaFiscal("Bearer " + token, request);
		NfseResponse cancelResponse = nfseService.cancelarNfse("Bearer " + token,
				nfseResponse.getId());
		NfseResponse consultaResponse = nfseService.consultarRPS("Bearer " + token, 
				nfseResponse.getId());
		
		//assert
		assertAll("Geração de Token",
				() -> assertNotNull(token, "Token não deve ser nulo.")
		);
		
		assertAll("Geração de NFSe",
				() -> assertNotNull(nfseResponse, "Resposta não deve ser nula."),
				() -> assertEquals("OK", nfseResponse.getStatus(), "Status deve ser OK."),
				() -> assertNotNull(nfseResponse.getNumeroNfse(), "Número da NFSe não deve ser nulo."),
				() -> assertNotNull(nfseResponse.getCodVerificacao(), "Código de verificação da NFSe não deve ser nulo.")
		);
		
		assertAll("Consulta RPS",
				() -> assertEquals("OK", consultaResponse.getStatus(), "Status da consulta deve ser OK.")
		);
		
		assertAll("Cancelamento da NFSe",
				() -> assertEquals("OK", cancelResponse.getStatus(), "Status da consulta deve ser OK.")
		);
	}
	
	private NfseRequest createSampleNfseRequest() {
        NfseRequest request = new NfseRequest();
        request.setDocumento("12345678900");
        request.setNome("Empresa Teste");
        request.setIm("123456");
        request.setEndereco("Rua Teste");
        request.setNumeroImovel("100");
        request.setBairro("Centro");
        request.setCep(12345678);
        request.setCidadeIBGE(3550308);
        request.setUf("SP");
        request.setEmail("teste@empresa.com");
        request.setTelefone("11999999999");
        request.setValor(100.00);
        return request;
    }
}
