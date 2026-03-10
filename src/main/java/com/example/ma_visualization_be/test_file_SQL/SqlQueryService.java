package com.example.ma_visualization_be.test_file_SQL;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class SqlQueryService {

    @PersistenceContext
    private EntityManager entityManager;
    public String readQueryFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
    }

    public String replaceParams(String query, String month) {
        return query.replace(":month", "'" + month + "'");
    }

    public List<RepairFeeDTO> executeQueryAsDTO(String sql) {
        Query query = entityManager.createNativeQuery(sql);
        List<Object[]> resultList = query.getResultList();

        return resultList.stream()
                .map(row -> new RepairFeeDTO(
                        row[0] != null ? row[0].toString() : null,
                        row[1] != null ? ((Number) row[1]).doubleValue() : null,
                        row[2] != null ? ((Number) row[2]).doubleValue() : null
                ))
                .toList();
    }
}
