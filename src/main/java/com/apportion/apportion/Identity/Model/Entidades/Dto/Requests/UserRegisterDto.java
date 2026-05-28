package com.apportion.apportion.Identity.Model.Entidades.Dto.Requests;

import com.apportion.apportion.Identity.Model.Enums.Roles;

import java.time.LocalDate;

public record UserRegisterDto(String nome, String email, String senha, Roles role, LocalDate dataDeNascimento) {
}
