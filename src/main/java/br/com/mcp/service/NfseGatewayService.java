package br.com.mcp.service;

import br.com.mcp.dto.NfseRequest;
import br.com.mcp.dto.NfseResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mcptecnologia.integrador_nfse.artviagens.IntegradorMilhasFacil;

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
	        String prestadorId = tokenService.validarEObterPrestador(token);
	        
	    	IntegradorMilhasFacil nfse = new IntegradorMilhasFacil();
			
	    	// Extrair e validar parâmetros
            int id = Integer.valueOf(request.getIdRPS());
            String documento = request.getDocumento();
            String im = request.getIm();
            String nome = request.getNome();
            String endereco = request.getEndereco();
            String numeroImovel = request.getNumeroImovel();
            String complemento = request.getComplemento();
            String bairro = request.getBairro();
            int cep = Integer.valueOf(request.getCep());
            int cidadeIBGE = Integer.valueOf(request.getCidadeIBGE());
            String uf = request.getUf();
            String email = request.getEmail();
            String telefone = request.getTelefone();
            double valor = Double.valueOf(request.getValor());
			
            
            validarCamposObrigatorios(documento, im, nome, valor);
            
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
