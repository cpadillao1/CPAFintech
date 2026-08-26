package com.fintech.management.control_system.controller;

import com.fintech.management.control_system.dto.SystemStatusDTO;
import com.fintech.management.control_system.service.SystemControlService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class SystemControlController {

    private final SystemControlService systemService;

    @GetMapping("/config")
    public ResponseEntity<SystemStatusDTO> getSystemConfig() {
        return ResponseEntity.ok(systemService.getFullConfigDTO());
    }

}
