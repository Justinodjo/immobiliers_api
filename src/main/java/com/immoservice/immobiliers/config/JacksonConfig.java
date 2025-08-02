// package com.web.application.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import com.fasterxml.jackson.databind.ObjectMapper;

// import com.fasterxml.jackson.core.StreamWriteConstraints;

// @Configuration
// public class JacksonConfig {

//     @Bean
//     public ObjectMapper objectMapper() {
//         ObjectMapper mapper = new ObjectMapper();
//         mapper.getFactory().configure(StreamWriteConstraints.FAIL_ON_EMPTY_BEANS, 2000);
//         return mapper;
//     }
// }

