package com.example.ma_visualization_be.service;

import com.example.ma_visualization_be.dto.IRemainChartDTO;
import com.example.ma_visualization_be.repository.IRemainChartRepo;
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

        return result;

    }

}

