package com.apportion.apportion.Controller;

import com.apportion.apportion.Dto.Requests.UserRequestDto;
import com.apportion.apportion.Dto.Responses.UserResponseDto;
import com.apportion.apportion.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;



@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UsuarioController {

    private final UserService userService;

    @GetMapping
    public List<UserResponseDto> GetAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> findById(@PathVariable Long id) {
        try {
            UserResponseDto dto = userService.findById(id);

            return ResponseEntity.ok(dto);

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping
    public ResponseEntity<UserResponseDto> create(@RequestBody UserRequestDto user) {
        UserResponseDto novoUsuario = userService.save(user);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoUsuario.getId())
                .toUri();
        return ResponseEntity.created(uri).body(novoUsuario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable long id) {
        if (!userService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        userService.deletebyId(id);
        return ResponseEntity.noContent().build();
    }
}