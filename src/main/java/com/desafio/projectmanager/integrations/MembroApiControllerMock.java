package com.desafio.projectmanager.integrations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.desafio.projectmanager.dto.response.MembroExternoDTO;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/mock-api/membros")
public class MembroApiControllerMock {

    private static final Map<UUID, MembroExternoDTO> membrosMockados = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(MembroApiControllerMock.class);

    @PostConstruct
    public void init() {

        criarMembro(new MembroExternoDTO(UUID.randomUUID(), "João Silva", "CONVIDADO"));
        criarMembro(new MembroExternoDTO(UUID.randomUUID(), "Maria Santos", "FUNCIONARIO"));
        criarMembro(new MembroExternoDTO(UUID.randomUUID(), "Carlos Pereira", "FUNCIONARIO"));
        criarMembro(new MembroExternoDTO(UUID.randomUUID(), "Ana Souza", "FUNCIONARIO"));
        criarMembro(new MembroExternoDTO(UUID.randomUUID(), "Ashley Saint", "FUNCIONARIO"));
        criarMembro(new MembroExternoDTO(UUID.randomUUID(), "Ricardo Gonzalez", "FUNCIONARIO"));
        criarMembro(new MembroExternoDTO(UUID.randomUUID(), "Juan Pereira", "STAKEHOLDER"));
        criarMembro(new MembroExternoDTO(UUID.randomUUID(), "Maria Rodrigues", "CONVIDADO"));
        logger.info("API Mock de Membros populada com {} usuários.", membrosMockados.size());
    }

    @PostMapping
    public ResponseEntity<MembroExternoDTO> criarMembro(@RequestBody MembroExternoDTO novoMembro) {
        UUID id = UUID.randomUUID();
        novoMembro.setId(id);
        membrosMockados.put(id, novoMembro);
        logger.info("MOCK API: Membro criado -> ID: {}, Nome: {}", id, novoMembro.getNome());
        return ResponseEntity.status(HttpStatus.CREATED).body(novoMembro);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembroExternoDTO> getMembroPorId(@PathVariable UUID id) {
        MembroExternoDTO membro = membrosMockados.get(id);
        if (membro != null) {
            logger.info("MOCK API: Buscando membro com ID {}... Encontrado: {}", id, membro.getNome());
            return ResponseEntity.ok(membro);
        } else {
            logger.warn("MOCK API: Membro com ID {} não encontrado.", id);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<MembroExternoDTO>> getAllMembros() {
        return ResponseEntity.ok(new ArrayList<>(membrosMockados.values()));
    }
}
