package com.example.resultbot.service;

import com.example.resultbot.entity.ServiceType;
import com.example.resultbot.entity.enumirated.Status;
import com.example.resultbot.repository.ServiceTypeRepository;
import com.example.resultbot.service.dto.ServiceTypeDto;
import com.example.resultbot.service.mapper.ServiceTypeMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServiceTypeService {

    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceTypeMapper serviceTypeMapper;

    public ServiceTypeService(ServiceTypeRepository serviceTypeRepository, ServiceTypeMapper serviceTypeMapper) {
        this.serviceTypeRepository = serviceTypeRepository;
        this.serviceTypeMapper = serviceTypeMapper;
    }

    public ServiceTypeDto create(ServiceTypeDto serviceTypeDto) {
        ServiceType serviceType = serviceTypeMapper.toEntity(serviceTypeDto);
        if (serviceTypeDto.getStatus() == null) {
            serviceType.setStatus(Status.ACTIVE);
        }
        serviceType = serviceTypeRepository.save(serviceType);
        return serviceTypeMapper.toDto(serviceType);
    }

    public ServiceTypeDto update(ServiceTypeDto serviceTypeDto) {
        ServiceType serviceType = serviceTypeMapper.toEntity(serviceTypeDto);
        serviceType = serviceTypeRepository.save(serviceType);
        return serviceTypeMapper.toDto(serviceType);
    }

    public List<ServiceTypeDto> findAllServiceTypes() {
        return serviceTypeRepository
                .findAll()
                .stream()
                .map(serviceTypeMapper::toDto)
                .collect(Collectors.toList());
    }

    public ServiceType findById(Long id) {
        return serviceTypeRepository
                .findById(id)
                .orElseGet(ServiceType::new);
    }

    public ServiceType delete(Long id) {
        return serviceTypeRepository.updateStatus(id, Status.DELETE);
    }
}

