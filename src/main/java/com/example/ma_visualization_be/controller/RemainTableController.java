package com.example.ma_visualization_be.controller;

import com.example.ma_visualization_be.dto.IRemainTableDTO;
import com.example.ma_visualization_be.dto.IRepairFeeDTO;
import com.example.ma_visualization_be.service.RemainTableService;
import com.example.ma_visualization_be.service.RepairFeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/remain_table")
public class RemainTableController {
    @Autowired
    private RemainTableService service;

    @GetMapping
    public ResponseEntity<List<IRemainTableDTO>> getRemainTable(
            @RequestParam String div,
            @RequestParam String date) throws IOException {
        List<IRemainTableDTO> data = service.getRemainTable(div, date);
        return ResponseEntity.ok(data);
    }
}
