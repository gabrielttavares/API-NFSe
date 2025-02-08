package br.com.mcp.controller;

import br.com.mcp.dto.NfseRequest;
import br.com.mcp.dto.NfseResponse;
import br.com.mcp.service.NfseGatewayService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;

//Define os endpoints da API
//Gerencia as requisições HTTP
//Mapeia URLs para métodos específicos
//Converte JSON em objetos Java e vice versa

@RestController
@RequestMapping("/api/v1/nfse")
public class NfseController {
    private final NfseGatewayService nfseGatewayService;

    public NfseController(NfseGatewayService nfseGatewayService) {
        this.nfseGatewayService = nfseGatewayService;
    }

    @PostMapping
    public ResponseEntity<NfseResponse> gerarNfse(
            @RequestHeader("Authorization") String token,
            @RequestBody NfseRequest request) {
        try {
            NfseResponse resultado = nfseGatewayService.gerarNotaFiscal(token, request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                NfseResponse.builder()
                    .status("ERRO")
                    .erros(new String[]{e.getMessage()})
                    .build()
            );
        }
    }

    @DeleteMapping("/{numeroNfse}")
    public ResponseEntity<NfseResponse> cancelarNfse(
            @RequestHeader("Authorization") String token,
            @PathVariable Long numeroNfse) {
        try {
            NfseResponse resultado = nfseGatewayService.cancelarNfse(token, numeroNfse);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                NfseResponse.builder()
                    .status("ERRO")
                    .erros(new String[]{e.getMessage()})
                    .build()
            );
        }
    }

    @GetMapping("/rps/{numeroRps}")
    public ResponseEntity<NfseResponse> consultarRps(
            @RequestHeader("Authorization") String token,
            @PathVariable Long numeroRps) {
        try {
            NfseResponse resultado = nfseGatewayService.consultarRPS(token, numeroRps);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                NfseResponse.builder()
                    .status("ERRO")
                    .erros(new String[]{e.getMessage()})
                    .build()
            );
        }
    }
}