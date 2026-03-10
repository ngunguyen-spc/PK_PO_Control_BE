package com.example.ma_visualization_be.controller;

import com.example.ma_visualization_be.dto.IRepairFeeDTO;
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
@RequestMapping("/api/repair_fee")
public class RepairFeeController {
    @Autowired
    private RepairFeeService service;

    @GetMapping
    public ResponseEntity<List<IRepairFeeDTO>> getRepairFee(
            @RequestParam String month) throws IOException {
        List<IRepairFeeDTO> data = service.getRepairFee(month);
        return ResponseEntity.ok(data);
    }
}
