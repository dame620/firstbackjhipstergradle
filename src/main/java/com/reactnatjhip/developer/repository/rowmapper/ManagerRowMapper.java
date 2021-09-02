package com.reactnatjhip.developer.repository.rowmapper;

import com.reactnatjhip.developer.domain.Manager;
import com.reactnatjhip.developer.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Manager}, with proper type conversions.
 */
@Service
public class ManagerRowMapper implements BiFunction<Row, String, Manager> {

    private final ColumnConverter converter;

    public ManagerRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Manager} stored in the database.
     */
    @Override
    public Manager apply(Row row, String prefix) {
        Manager entity = new Manager();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRegistrationNumber(converter.fromRow(row, prefix + "_registration_number", String.class));
        entity.setDepartment(converter.fromRow(row, prefix + "_department", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setCompanyId(converter.fromRow(row, prefix + "_company_id", Long.class));
        return entity;
    }
}
