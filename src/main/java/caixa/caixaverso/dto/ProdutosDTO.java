package caixa.caixaverso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ProdutosDTO(
    
    @NotBlank
    String nome, 
    
    String descricao, 
    
    @NotBlank
    @Positive
    Double preco) {

}
