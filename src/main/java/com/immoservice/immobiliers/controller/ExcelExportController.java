// package com.project.projetOne.controller;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;

// import com.project.projetOne.service.ExcelExportService;

// import jakarta.servlet.http.HttpServletResponse;

// import java.io.IOException;

// @RestController
// @RequestMapping("api/export")
// public class ExcelExportController {

// private final ExcelExportService excelExportService;

// public ExcelExportController(ExcelExportService excelExportService) {
// this.excelExportService = excelExportService;
// }

// @GetMapping("excel")
// public void exportToExcel(HttpServletResponse response) throws IOException {
// excelExportService.exportToExcel(response);
// }
// }
