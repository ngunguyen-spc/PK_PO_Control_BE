package com.example.ma_visualization_be.service;

import com.example.ma_visualization_be.dto.IRemainTableDetailDTO;
import com.example.ma_visualization_be.repository.IRemainTableDetailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RemainTableDetailService {
    @Autowired
    IRemainTableDetailRepo repository;

    public List<IRemainTableDetailDTO> getRemainTableDetail(String div, String date, String cusID, String shipBy) {
        List<IRemainTableDetailDTO> result = new ArrayList<>();

        result =  repository.getRemainTableDetail(div, date, cusID, shipBy);

        return result;

    }

}

