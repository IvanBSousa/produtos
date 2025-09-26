package dto;

import java.math.BigDecimal;

import anotations.Numeric;
import io.smallrye.common.constraint.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RequestDTO(
    
    @NotBlank
    String nome, 
    
    String descricao, 
    
    @NotNull
    @Positive
    @Numeric
    BigDecimal preco
    ) {

}
