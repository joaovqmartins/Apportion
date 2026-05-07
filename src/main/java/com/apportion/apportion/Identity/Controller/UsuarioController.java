package com.apportion.apportion.Identity.Controller;

import com.apportion.apportion.Identity.Model.Entidades.Dto.Requests.UserRequestDto;
import com.apportion.apportion.Identity.Model.Entidades.Dto.Responses.UserResponseDto;
import com.apportion.apportion.Identity.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/AllUsers")
    public ResponseEntity<Page<UserResponseDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    )
    {
        Pageable pageable = PageRequest.of(page,size);

        return ResponseEntity.ok(userService.findAll(pageable));
    };
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