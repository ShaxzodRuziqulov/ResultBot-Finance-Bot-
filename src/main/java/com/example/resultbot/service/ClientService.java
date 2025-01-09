package com.example.resultbot.service;

import com.example.resultbot.entity.Client;
import com.example.resultbot.entity.enumirated.Status;
import com.example.resultbot.repository.ClientRepository;
import com.example.resultbot.service.dto.ClientDto;
import com.example.resultbot.service.mapper.ClientMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper) {
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }

    public ClientDto create(ClientDto clientDto) {
        Client client = clientMapper.toEntity(clientDto);
        client = clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    public ClientDto update(ClientDto clientDto) {
        Client client = clientMapper.toEntity(clientDto);
        client = clientRepository.save(client);
        return clientMapper.toDto(client);
    }

    public List<ClientDto> findAllClients() {
        return clientRepository
                .findAll()
                .stream()
                .map(clientMapper::toDto)
                .collect(Collectors.toList());
    }

    public Client findById(Long id) {
        return clientRepository
                .findById(id)
                .orElseGet(Client::new);
    }

    public Client delete(Long id) {
        return clientRepository.updateStatus(id, Status.DELETE);
    }
}

