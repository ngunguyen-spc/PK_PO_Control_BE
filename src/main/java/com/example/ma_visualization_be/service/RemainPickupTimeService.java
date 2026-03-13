package com.example.ma_visualization_be.service;

import com.example.ma_visualization_be.dto.IRemainPickupTimeDTO;
import com.example.ma_visualization_be.repository.IRemainPickupTimeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RemainPickupTimeService {
    @Autowired
    IRemainPickupTimeRepo repository;

    public List<IRemainPickupTimeDTO> getRemainPickupTime(String div, String date) {
        List<IRemainPickupTimeDTO> result = new ArrayList<>();

        result =  repository.getRemainPickupTime(div, date);

        return result;

    }

}

