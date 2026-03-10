package com.example.ma_visualization_be.test_file_SQL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class SqlQueryController {

    @Autowired
    private  SqlQueryService sqlQueryService;

    @GetMapping("/repair-fee")
    public ResponseEntity<?> getRepairFee(@RequestParam String month) {
        try {
            String sqlPath = "\\\\192.168.122.15\\1.pc\\Visualization_MA\\repair_fee_query.sql";
            String rawSql = sqlQueryService.readQueryFromFile(sqlPath);
            String finalSql = sqlQueryService.replaceParams(rawSql, month);
            List<RepairFeeDTO> result = sqlQueryService.executeQueryAsDTO(finalSql);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
