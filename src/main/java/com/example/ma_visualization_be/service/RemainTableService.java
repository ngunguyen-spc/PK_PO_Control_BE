package com.example.ma_visualization_be.service;

import com.example.ma_visualization_be.dto.IRemainTableDTO;
import com.example.ma_visualization_be.dto.IRepairFeeDTO;
import com.example.ma_visualization_be.repository.IRemainTableRepo;
import com.example.ma_visualization_be.repository.IRepairFeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class RemainTableService {
    @Autowired
    IRemainTableRepo repository;

    public List<IRemainTableDTO> getRemainTable(String div, String date) {
        List<IRemainTableDTO> result = new ArrayList<>();

        result =  repository.getRemainTable(div, date);
//        List<String> customOrder = List.of("press", "mold", "guide");
//        result.sort(Comparator.comparingInt(item -> {
//            String deptLower = item.getDept() != null ? item.getDept().toLowerCase() : "";
//            int index = customOrder.indexOf(deptLower);
//            return index >= 0 ? index : Integer.MAX_VALUE; // các dept khác sẽ ở cuối
//        }));

        return result;

    }

}

