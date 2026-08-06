package com.immoservice.immobiliers.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.immoservice.immobiliers.entities.AccessLog;
import com.immoservice.immobiliers.repository.AccessLogRepository;

@RestController
@RequestMapping("/logs")
public class AccessLogController {

    @Autowired
    private AccessLogRepository accessLogRepository;

    @GetMapping("/all")
    public List<AccessLog> getAllLogs() {
        return accessLogRepository.findAll(Sort.by(Sort.Direction.DESC, "accessTime"));
    }


    @GetMapping("/user/{username}")
    public List<AccessLog> getLogsByUser(@PathVariable String username) {
        return accessLogRepository.findByUtilisateur_Name(username);
    }

    @GetMapping("/periode")
    public List<AccessLog> getLogsByPeriod(@RequestParam String start, @RequestParam String end) {
        return accessLogRepository.findByAccessTimeBetween(
                LocalDateTime.parse(start),
                LocalDateTime.parse(end)
        );
    }
}


