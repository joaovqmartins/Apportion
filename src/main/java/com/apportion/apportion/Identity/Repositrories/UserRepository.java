package com.apportion.apportion.Identity.Repositrories;


import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserRepository extends JpaRepository<UsuarioEntity, Long>
{
    UserDetails findByEmail(String email);
}
