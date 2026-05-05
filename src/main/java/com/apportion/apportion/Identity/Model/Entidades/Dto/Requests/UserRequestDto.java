package com.apportion.apportion.Identity.Model.Entidades.Dto.Requests;

import jakarta.persistence.Entity;
import lombok.*;

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
}
