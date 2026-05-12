package com.apportion.apportion.Social.Model.Controller;


import com.apportion.apportion.Social.Model.Entidades.Dto.Requests.GrupoRequestDto;
import com.apportion.apportion.Social.Model.Entidades.Dto.Responses.GrupoResponseDto;
import com.apportion.apportion.Social.Service.GrupoService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/grupos")
@AllArgsConstructor
public class GrupoController {

    private final GrupoService grupoService;

    @GetMapping
    public ResponseEntity<Page<GrupoResponseDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(grupoService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoResponseDto> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(grupoService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<GrupoResponseDto> create(@RequestBody GrupoRequestDto request) {
        GrupoResponseDto novoGrupo = grupoService.save(request);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(novoGrupo.getId())
                .toUri();
        return ResponseEntity.created(uri).body(novoGrupo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!grupoService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        grupoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

