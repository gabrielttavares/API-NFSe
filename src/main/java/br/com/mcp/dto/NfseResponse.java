package br.com.mcp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NfseResponse {
	private Long id;
    private String numeroNfse;
    private String codVerificacao;
    private String xml;
    private String[] erros;
    private String status; // "OK" ou "ERRO"
}
