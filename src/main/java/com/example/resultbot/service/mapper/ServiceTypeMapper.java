package com.example.resultbot.service.mapper;

import com.example.resultbot.entity.ServiceType;
import com.example.resultbot.service.dto.ServiceTypeDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceTypeMapper extends EntityMapper<ServiceTypeDto, ServiceType> {
    ServiceType toEntity(ServiceTypeDto serviceTypeDto);

    ServiceTypeDto toDto(ServiceType serviceType);
}
