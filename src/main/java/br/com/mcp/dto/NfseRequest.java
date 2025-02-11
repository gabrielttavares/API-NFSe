package br.com.mcp.dto;

import lombok.Data;

@Data
public class NfseRequest {
	private Integer idRPS;
	private String documento;
	private String im;
	private String nome;
	private String endereco;
	private String numeroImovel;
	private String complemento;
	private String bairro;
	private Integer cep;
	private Integer cidadeIBGE;
	private String uf;
	private String email;
	private String telefone;
	private Double valor;
}