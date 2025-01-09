package com.example.resultbot.web.rest;

import com.example.resultbot.entity.ServiceType;
import com.example.resultbot.service.ServiceTypeService;
import com.example.resultbot.service.dto.ServiceTypeDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/service-type")
public class ServiceTypeResource {
    private final ServiceTypeService serviceTypeService;

    public ServiceTypeResource(ServiceTypeService serviceTypeService) {
        this.serviceTypeService = serviceTypeService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody ServiceTypeDto serviceTypeDto) throws URISyntaxException {
        ServiceTypeDto result = serviceTypeService.create(serviceTypeDto);
        return ResponseEntity.created(new URI("/api/service-type/create/" + result.getId())).body(result);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@RequestBody ServiceTypeDto serviceTypeDto, @PathVariable Long id) throws URISyntaxException {
        if (serviceTypeDto.getId() != 0 && !serviceTypeDto.getId().equals(id)) {
            return ResponseEntity.badRequest().body("Invalid id");
        }
        ServiceTypeDto result = serviceTypeService.update(serviceTypeDto);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        List<ServiceTypeDto> serviceTypes = serviceTypeService.findAllServiceTypes();
        return ResponseEntity.ok(serviceTypes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable Long id) {
        ServiceType serviceType = serviceTypeService.findById(id);
        return ResponseEntity.ok(serviceType);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ServiceType serviceType = serviceTypeService.delete(id);
        return ResponseEntity.ok(serviceType);
    }
}

