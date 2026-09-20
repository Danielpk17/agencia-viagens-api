package com.senai.agenciaviagens.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record UsuarioRequest(

        @NotBlank(message = "O username e obrigatorio")
        @Size(min = 3, max = 60, message = "O username deve ter entre 3 e 60 caracteres")
        String username,

        @NotBlank(message = "A senha e obrigatoria")
        @Size(min = 6, max = 60, message = "A senha deve ter entre 6 e 60 caracteres")
        String senha,

        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres")
        String nome,

        @Email(message = "O email informado e invalido")
        @Size(max = 120, message = "O email deve ter no maximo 120 caracteres")
        String email,

        @NotEmpty(message = "Informe ao menos um perfil, como ADMIN ou USER")
        Set<String> perfis) {
}
