package com.example.ma_visualization_be.controller;

import com.example.ma_visualization_be.dto.IRemainChartDTO;
import com.example.ma_visualization_be.service.RemainChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/remain_chart")
public class RemainChartController {
    @Autowired
    private RemainChartService service;

    @GetMapping
    public ResponseEntity<List<IRemainChartDTO>> getRemainChart(
            @RequestParam String div,
            @RequestParam String date) throws IOException {
        List<IRemainChartDTO> data = service.getRemainChart(div, date);
        return ResponseEntity.ok(data);
    }
}
