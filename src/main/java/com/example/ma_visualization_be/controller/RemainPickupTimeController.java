package com.example.ma_visualization_be.controller;

import com.example.ma_visualization_be.dto.IRemainChartDTO;
import com.example.ma_visualization_be.dto.IRemainPickupTimeDTO;
import com.example.ma_visualization_be.service.RemainChartService;
import com.example.ma_visualization_be.service.RemainPickupTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/remain_pickup_time")
public class RemainPickupTimeController {
    @Autowired
    private RemainPickupTimeService service;

    @GetMapping
    public ResponseEntity<List<IRemainPickupTimeDTO>> getRemainPickupTime(
            @RequestParam String div,
            @RequestParam String date) throws IOException {
        List<IRemainPickupTimeDTO> data = service.getRemainPickupTime(div, date);
        return ResponseEntity.ok(data);
    }
}
