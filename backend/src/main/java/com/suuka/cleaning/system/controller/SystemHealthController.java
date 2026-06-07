package com.suuka.cleaning.system.controller;

import com.suuka.cleaning.common.response.ApiResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.util.Map;

@RestController
@RequestMapping("/api/system")
public class SystemHealthController {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public SystemHealthController(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/database-health")
    public ApiResponse<Map<String, Object>> databaseHealth() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            String productName = metaData.getDatabaseProductName();
            boolean tablesDetected = Boolean.TRUE.equals(jdbcTemplate.queryForObject(
                    "select exists (select 1 from information_schema.tables where table_schema = 'public' and table_name = 'app_users')",
                    Boolean.class
            ));

            return ApiResponse.success("Database health loaded", Map.of(
                    "database", productName,
                    "status", connection.isValid(2) ? "CONNECTED" : "UNAVAILABLE",
                    "tablesDetected", tablesDetected
            ));
        }
    }
}
