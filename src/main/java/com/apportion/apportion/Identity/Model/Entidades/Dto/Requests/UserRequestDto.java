package com.apportion.apportion.Identity.Model.Entidades.Dto.Requests;

import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto
{
        public String nome;
        public String email;
        public String senha;
        private LocalDate dataDeNascimento;
}
