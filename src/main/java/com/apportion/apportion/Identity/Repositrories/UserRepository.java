package com.apportion.apportion.Identity.Repositrories;


import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UsuarioEntity, Long>
{
}
