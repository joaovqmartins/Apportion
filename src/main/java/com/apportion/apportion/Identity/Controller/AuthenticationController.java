package com.apportion.apportion.Identity.Controller;
import com.apportion.apportion.Identity.Model.Entidades.Dto.AuthenticationDto;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Requests.UserRegisterDto;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Requests.UserRequestDto;
import com.apportion.apportion.Identity.Model.Entidades.UsuarioEntity;
import com.apportion.apportion.Identity.Repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDto data)
    {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(),data.senha());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        return ResponseEntity.ok().build();
    };

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid UserRegisterDto data){
        if(this.repository.findByEmail(data.email()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.senha());

        UsuarioEntity novoUsuario = new UsuarioEntity(data.nome(), data.email(), encryptedPassword, data.dataDeNascimento(),data.role());

        this.repository.save(novoUsuario);

        return ResponseEntity.ok().build();
    }

}
