package com.example.ma_visualization_be.service;

import com.example.ma_visualization_be.dto.IRemainTableDetailMTDDTO;
import com.example.ma_visualization_be.dto.IRemainTableDetailMTDIDDTO;
import com.example.ma_visualization_be.repository.IRemainTableDetailMTDIDRepo;
import com.example.ma_visualization_be.repository.IRemainTableDetailMTDRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RemainTableDetailMTDIDService {
    @Autowired
    IRemainTableDetailMTDIDRepo repository;

    public List<IRemainTableDetailMTDIDDTO> getRemainTableDetailMTDID(String div, String date, String cusID, String shipBy) {
        List<IRemainTableDetailMTDIDDTO> result = new ArrayList<>();

        result =  repository.getRemainTableDetailMTDID(div, date, cusID, shipBy);

        return result;

    }

}

