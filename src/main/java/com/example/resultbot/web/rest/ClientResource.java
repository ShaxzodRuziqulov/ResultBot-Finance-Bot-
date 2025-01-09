package com.example.resultbot.web.rest;

import com.example.resultbot.entity.Client;
import com.example.resultbot.service.ClientService;
import com.example.resultbot.service.dto.ClientDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/client")
public class ClientResource {
    private final ClientService clientService;

    public ClientResource(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ClientDto clientDto) throws URISyntaxException {
        ClientDto result = clientService.create(clientDto);
        return ResponseEntity.created(new URI("/api/client/create/" + result.getId())).body(result);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody ClientDto clientDto, @PathVariable Long id) throws URISyntaxException {
        if (clientDto.getId() != 0 && !clientDto.getId().equals(id)) {
            return ResponseEntity.badRequest().body("Invalid id");
        }
        ClientDto result = clientService.update(clientDto);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<ClientDto> clients = clientService.findAllClients();
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok(client);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Client client = clientService.delete(id);
        return ResponseEntity.ok(client);
    }
}

