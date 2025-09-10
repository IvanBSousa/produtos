package caixa.caixaverso.dto;

import java.math.BigDecimal;

import caixa.caixaverso.anotations.IsNumeric;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

public record ProdutosDTO(
    
    @NotBlank
    String nome, 
    
    String descricao, 
    
    @NotNull
    @Positive
    @IsNumeric
    BigDecimal preco) {

}
