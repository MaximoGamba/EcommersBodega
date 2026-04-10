package com.uade.tpo.demo.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class MasterDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(MasterDataInitializer.class);

    private final JdbcTemplate jdbcTemplate;

    public MasterDataInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedTable("colors", List.of(
                new SeedRow(1L, "Tinto"),
                new SeedRow(2L, "Blanco"),
                new SeedRow(3L, "Rosado")));

        seedTable("azucares", List.of(
                new SeedRow(1L, "Seco"),
                new SeedRow(2L, "Semi-seco"),
                new SeedRow(3L, "Dulce")));

        seedTable("elaboraciones", List.of(
                new SeedRow(1L, "Tradicional"),
                new SeedRow(2L, "Industrial")));

        seedTable("crianzas", List.of(
                new SeedRow(1L, "Joven"),
                new SeedRow(2L, "Crianza"),
                new SeedRow(3L, "Reserva")));

        seedTable("cepas", List.of(
                new SeedRow(1L, "Malbec"),
                new SeedRow(2L, "Cabernet Sauvignon"),
                new SeedRow(3L, "Chardonnay")));

        seedTable("medidas", List.of(
                new SeedRow(1L, "750ml"),
                new SeedRow(2L, "1L")));
    }

    private void seedTable(String tableName, List<SeedRow> rows) {
        int inserted = 0;
        int skipped = 0;

        for (SeedRow row : rows) {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM " + tableName + " WHERE id = ?",
                    Integer.class,
                    row.id());

            if (count != null && count > 0) {
                skipped++;
                continue;
            }

            jdbcTemplate.update(
                    "INSERT INTO " + tableName + " (id, name) VALUES (?, ?)",
                    row.id(),
                    row.name());
            inserted++;
        }

        log.info("Seed tabla {} -> insertados: {}, existentes: {}", tableName, inserted, skipped);
    }

    private record SeedRow(Long id, String name) {
    }
}

