package com.immoservice.immobiliers.controller;

import com.immoservice.immobiliers.entities.AccessLog;
import com.immoservice.immobiliers.repository.AccessLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logs")
public class AccessLogController {

    @Autowired
    private AccessLogRepository accessLogRepository;

    @GetMapping("/all")
    public List<AccessLog> getAllLogs() {
        return accessLogRepository.findAll();
    }
}
