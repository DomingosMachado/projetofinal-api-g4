package org.serratec.projetofinal_api_g4.dto;
import org.serratec.projetofinal_api_g4.enums.TipoFuncionario;

public class LoginResponseDTO {
    private String id;
    private String nome;
    private String email;
    private TipoFuncionario tipoFuncionario;
    private String token;
    private boolean sucesso;
    private String mensagem;
    
    // Construtores
    public LoginResponseDTO() {}
    
    public LoginResponseDTO(String id, String nome, String email, 
                           TipoFuncionario tipoFuncionario, String token, 
                           boolean sucesso, String mensagem) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.tipoFuncionario = tipoFuncionario;
        this.token = token;
        this.sucesso = sucesso;
        this.mensagem = mensagem;
    }
    
    // Getters e Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public TipoFuncionario getTipoFuncionario() { return tipoFuncionario; }
    public void setTipoFuncionario(TipoFuncionario tipoFuncionario) { this.tipoFuncionario = tipoFuncionario; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public boolean isSucesso() { return sucesso; }
    public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }
    
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }
}