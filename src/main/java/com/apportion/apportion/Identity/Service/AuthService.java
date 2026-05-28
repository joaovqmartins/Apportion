package com.apportion.apportion.Identity.Service;

import com.apportion.apportion.Identity.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        System.out.println("DEBUG - Email que chegou no Service: [" + username + "]");

        UserDetails usuario = userRepository.findByEmail(username);
        System.out.println("DEBUG - O banco encontrou o usuário? " + (usuario != null));

        // Se o banco não achar o usuário, lança a exceção exigida pelo Spring Security
        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com o email: " + username);
        }

        return usuario;
    }


}
