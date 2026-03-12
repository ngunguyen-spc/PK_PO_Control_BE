package com.example.ma_visualization_be.controller;

import com.example.ma_visualization_be.dto.IRemainTableDetailDTO;
import com.example.ma_visualization_be.service.RemainTableDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/api/remain_table_detail")
public class RemainTableDetailController {
    @Autowired
    private RemainTableDetailService service;

    @GetMapping
    public ResponseEntity<List<IRemainTableDetailDTO>> getRemainTableDetail(
            @RequestParam String div,
            @RequestParam String date,
            @RequestParam String cusID,
            @RequestParam String shipBy
    ) throws IOException {
        List<IRemainTableDetailDTO> data = service.getRemainTableDetail(div, date, cusID, shipBy);
        return ResponseEntity.ok(data);
    }
}
