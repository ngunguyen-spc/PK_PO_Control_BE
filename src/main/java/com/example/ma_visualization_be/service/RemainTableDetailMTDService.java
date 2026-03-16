package com.example.ma_visualization_be.service;

import com.example.ma_visualization_be.dto.IRemainTableDetailDTO;
import com.example.ma_visualization_be.dto.IRemainTableDetailMTDDTO;
import com.example.ma_visualization_be.repository.IRemainTableDetailMTDRepo;
import com.example.ma_visualization_be.repository.IRemainTableDetailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RemainTableDetailMTDService {
    @Autowired
    IRemainTableDetailMTDRepo repository;

    public List<IRemainTableDetailMTDDTO> getRemainTableDetailMTD(String div, String date, String cusID, String shipBy) {
        List<IRemainTableDetailMTDDTO> result = new ArrayList<>();

        result =  repository.getRemainTableDetailMTD(div, date, cusID, shipBy);

        return result;

    }

}

