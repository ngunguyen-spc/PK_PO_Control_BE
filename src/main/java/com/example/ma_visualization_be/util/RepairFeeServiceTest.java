//package com.example.ma_visualization_be.util;
//
//import com.example.ma_visualization_be.dto.IRepairFeeDTO;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.PersistenceContext;
//import jakarta.persistence.Query;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.util.List;
//
//@Service
//public class RepairFeeServiceTest {
//    @PersistenceContext
//    private EntityManager entityManager;
//
//    public List<IRepairFeeDTO> getRepairFeeData(String month) throws IOException {
//        String sql = SqlFileReaderUtil.readSqlFromFile("/sql/repair_fee_query.sql");
//        Query query = entityManager.createNativeQuery(sql);
//        query.setParameter("month", month);
//
//        List<Object[]> results = query.getResultList();
//
//        return results.stream().map(row -> new IRepairFeeDTO() {
//            @Override public String getDept()         { return (String) row[0]; }
//            @Override public Double getFcUsd()        { return (Double) row[1]; }
//            @Override public Integer getCountDayAll() { return ((Number) row[2]).intValue(); }
//            @Override public Integer getCountDayMTD() { return ((Number) row[3]).intValue(); }
//            @Override public Double getTgtMtdOrg()    { return (Double) row[4]; }
//            @Override public Double getAct()          { return (Double) row[5]; }
//        }).toList();
//    }
//
//
//}
