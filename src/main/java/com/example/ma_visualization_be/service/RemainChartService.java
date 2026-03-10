package com.example.ma_visualization_be.service;

import com.example.ma_visualization_be.dto.IRemainChartDTO;
import com.example.ma_visualization_be.repository.IRemainChartRepo;
import com.example.ma_visualization_be.repository.IRemainTableRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RemainChartService {
    @Autowired
    IRemainChartRepo repository;

    public List<IRemainChartDTO> getRemainChart(String div, String date) {
        List<IRemainChartDTO> result = new ArrayList<>();

        result =  repository.getRemainChart(div, date);
//        List<String> customOrder = List.of("press", "mold", "guide");
//        result.sort(Comparator.comparingInt(item -> {
//            String deptLower = item.getDept() != null ? item.getDept().toLowerCase() : "";
//            int index = customOrder.indexOf(deptLower);
//            return index >= 0 ? index : Integer.MAX_VALUE; // các dept khác sẽ ở cuối
//        }));

        return result;

    }

}

