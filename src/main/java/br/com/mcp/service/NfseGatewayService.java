package br.com.mcp.service;

import br.com.mcp.dto.NfseRequest;
import br.com.mcp.dto.NfseResponse;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mcptecnologia.integrador_nfse.Empresa;
import com.mcptecnologia.integrador_nfse.artviagens.IntegradorMilhasFacil;
import com.mcptecnologia.integrador_nfse.certificacao.Assinador;

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
	
	//GERAR
	public NfseResponse gerarNotaFiscal(String token, NfseRequest request) {
	    try {
	        
	        if (request == null) {
				return NfseResponse.builder()
                    .status("ERRO")
                    .erros(new String[]{"Requisição não pode ser nula"})
                    .build();
			}
	        
	        //Param. Numéricos
	        Integer id = parseIntegerSafely(request.getIdRPS(), "ID do RPS");
	        Integer cep = parseIntegerSafely(request.getCep(), "CEP");
	        Integer cidadeIBGE = parseIntegerSafely(request.getCidadeIBGE(), "Código IBGE da cidade");
	        Double valor = parseDoubleSafely(request.getValor(), "Valor");
	        
	        //Não-numéricos (com null safety)
	        String documento = nullToEmpty(request.getDocumento());
	        String im = nullToEmpty(request.getIm());
	        String nome = nullToEmpty(request.getNome());
	        String endereco = nullToEmpty(request.getEndereco());
	        String numeroImovel = nullToEmpty(request.getNumeroImovel());
	        String complemento = nullToEmpty(request.getComplemento());
	        String bairro = nullToEmpty(request.getBairro());
	        String uf = nullToEmpty(request.getUf());
	        String email = nullToEmpty(request.getEmail());
	        String telefone = nullToEmpty(request.getTelefone());
			    
            validarCamposObrigatorios(documento, im, nome, valor);
            
            String prestadorId = tokenService.validarEObterPrestador(token);
	    	IntegradorMilhasFacil nfse = new IntegradorMilhasFacil(Empresa.getPorCnpj(prestadorId));
            
            // Gerar NFSe usando o integrador
            String resposta = nfse.gerarNFSe(
                    id, documento, im, nome, endereco, 
                    numeroImovel, complemento, bairro, 
                    cep, cidadeIBGE, uf, email, 
                    telefone, valor
             );
            
            // Processar resposta
            if ("OK".equals(resposta)) {
                return NfseResponse.builder()
                    .id((long) id)
                    .numeroNfse(nfse.numero_nfse)
                    .codVerificacao(nfse.codVerificacao)
                    .xml(nfse.xml)
                    .status("OK")
                    .build();
            } else {
                return NfseResponse.builder()
                    .status("ERRO")
                    .erros(nfse.errosWS.split(nfse.separadorErros))
                    .build();
            }
            
        } catch (NumberFormatException e) {
        	e.printStackTrace();
            return NfseResponse.builder()
                .status("ERRO")
                .erros(new String[]{"Erro de formato nos dados numéricos: " + e.getMessage()})
                .build();
        } catch (Exception e) {
        	e.printStackTrace();
            return NfseResponse.builder()
                .status("ERRO")
                .erros(new String[]{"Erro ao processar a requisição: " + e.getMessage()})
                .build();
        }
    }
    
    private void validarCamposObrigatorios(String documento, String im, String nome, double valor) {
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("Documento é obrigatório");
        }
        if (im == null || im.trim().isEmpty()) {
            throw new IllegalArgumentException("Inscrição Municipal é obrigatória");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor deve ser maior que zero");
        }
    }
    
    //Helper methods
    private Integer parseIntegerSafely(Integer integer, String fieldName) {
	    if (integer == null || integer.toString().trim().isEmpty()) {
	        throw new IllegalArgumentException(fieldName + " é obrigatório");
	    }
	    try {
	        return Integer.valueOf(integer.toString().trim());
	    } catch (NumberFormatException e) {
	        throw new IllegalArgumentException(fieldName + " deve ser um número válido");
	    }
	}

    private Double parseDoubleSafely(Double double1, String fieldName) {
        if (double1 == null) {
            throw new IllegalArgumentException(fieldName + " é obrigatório");
        }
        return double1;
    }

	private String nullToEmpty(String value) {
	    return value == null ? "" : value.trim();
	}
	
	//CONSULTAR
    public NfseResponse consultarRPS(String token, Long numeroRPS) {
        try {
            String prestadorId = tokenService.validarEObterPrestador(token);
            
            IntegradorMilhasFacil nfse = new IntegradorMilhasFacil();
            
            // Consultar RPS usando o integrador
            String resposta = nfse.consultarRPS(numeroRPS.longValue());
            
            if ("OK".equals(resposta)) {
                return NfseResponse.builder()
                    .id(numeroRPS)
                    .numeroNfse(nfse.numero_nfse)
                    .codVerificacao(nfse.codVerificacao)
                    .xml(nfse.xml)
                    .status("OK")
                    .build();
            } else {
                return NfseResponse.builder()
                    .status("ERRO")
                    .erros(nfse.errosWS.split(nfse.separadorErros))
                    .build();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return NfseResponse.builder()
                .status("ERRO")
                .erros(new String[]{"Erro de formato no número do RPS: " + e.getMessage()})
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return NfseResponse.builder()
                .status("ERRO")
                .erros(new String[]{"Erro ao consultar RPS: " + e.getMessage()})
                .build();
        }
    }
    
    //CANCELAR
    public NfseResponse cancelarNfse(String token, Long numeroNfse) {
        try {
            String prestadorId = tokenService.validarEObterPrestador(token);
            
            IntegradorMilhasFacil nfse = new IntegradorMilhasFacil();
            
            // Cancelar NFSe usando o integrador
            String resposta = nfse.cancelar(numeroNfse.longValue());
            
            if ("OK".equals(resposta)) {
                return NfseResponse.builder()
                    .id(numeroNfse)
                    .numeroNfse(nfse.numero_nfse)
                    .codVerificacao(nfse.codVerificacao)
                    .xml(nfse.xml)
                    .status("OK")
                    .build();
            } else {
                return NfseResponse.builder()
                    .status("ERRO")
                    .erros(nfse.errosWS.split(nfse.separadorErros))
                    .build();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return NfseResponse.builder()
                .status("ERRO")
                .erros(new String[]{"Erro de formato no número da NFSe: " + e.getMessage()})
                .build();
        } catch (Exception e) {
            e.printStackTrace();
            return NfseResponse.builder()
                .status("ERRO")
                .erros(new String[]{"Erro ao cancelar NFSe: " + e.getMessage()})
                .build();
        }
    }
}
