package com.senai.agenciaviagens.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record DestinoRequest(

        @NotBlank(message = "O nome do destino e obrigatorio")
        @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres")
        String nome,

        @NotBlank(message = "O pais e obrigatorio")
        @Size(max = 80, message = "O pais deve ter no maximo 80 caracteres")
        String pais,

        @NotBlank(message = "A cidade e obrigatoria")
        @Size(max = 80, message = "A cidade deve ter no maximo 80 caracteres")
        String cidade,

        @Size(max = 500, message = "A descricao deve ter no maximo 500 caracteres")
        String descricao,

        @NotNull(message = "O preco medio e obrigatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "O preco medio deve ser maior que zero")
        BigDecimal precoMedio,

        @NotNull(message = "A duracao em dias e obrigatoria")
        @Min(value = 1, message = "A duracao minima e de 1 dia")
        @Max(value = 90, message = "A duracao maxima e de 90 dias")
        Integer duracaoDias,

        Boolean ativo) {
}
