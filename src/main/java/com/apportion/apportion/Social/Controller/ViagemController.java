package com.apportion.apportion.Social.Controller;

import com.apportion.apportion.Social.Model.Entidades.Dto.Requests.ViagemRequestDto;
import com.apportion.apportion.Social.Model.Entidades.Dto.Responses.ViagemResponseDto;
import com.apportion.apportion.Social.Service.ViagemService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/viagens")
@AllArgsConstructor
public class ViagemController {

    private final ViagemService viagemService;

    @GetMapping
    public ResponseEntity<Page<ViagemResponseDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(viagemService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ViagemResponseDto> findById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(viagemService.findById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<ViagemResponseDto> create(@RequestBody ViagemRequestDto request) {
        try {
            ViagemResponseDto novaViagem = viagemService.save(request);
            URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(novaViagem.getId())
                    .toUri();
            return ResponseEntity.created(uri).body(novaViagem);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!viagemService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        viagemService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}