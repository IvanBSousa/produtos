package dto;

import java.math.BigDecimal;
import java.time.Instant;
import com.fasterxml.jackson.annotation.JsonFormat;

public record ResponseDTO(

    Long id,

    String nome,

    String descricao,

    BigDecimal preco,

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "America/Sao_Paulo")
    Instant dataCriacao
) { }
