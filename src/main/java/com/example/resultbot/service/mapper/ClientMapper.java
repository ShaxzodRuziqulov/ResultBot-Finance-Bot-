package com.example.resultbot.service.mapper;

import com.example.resultbot.entity.Client;
import com.example.resultbot.entity.ServiceType;
import com.example.resultbot.service.dto.ClientDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ServiceType.class)
public interface ClientMapper extends EntityMapper<ClientDto, Client> {
    @Mapping(source = "serviceType.id", target = "serviceTypeId")
    ClientDto toDto(Client client);

    @Mapping(source = "serviceTypeId", target = "serviceType.id")
    Client toEntity(ClientDto clientDto);
}
