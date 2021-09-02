package com.reactnatjhip.developer.repository.rowmapper;

import com.reactnatjhip.developer.domain.Adviser;
import com.reactnatjhip.developer.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Adviser}, with proper type conversions.
 */
@Service
public class AdviserRowMapper implements BiFunction<Row, String, Adviser> {

    private final ColumnConverter converter;

    public AdviserRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Adviser} stored in the database.
     */
    @Override
    public Adviser apply(Row row, String prefix) {
        Adviser entity = new Adviser();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setRegistrationNumber(converter.fromRow(row, prefix + "_registration_number", String.class));
        entity.setCompany(converter.fromRow(row, prefix + "_company", String.class));
        entity.setDepartment(converter.fromRow(row, prefix + "_department", String.class));
        entity.setUserId(converter.fromRow(row, prefix + "_user_id", Long.class));
        entity.setBankId(converter.fromRow(row, prefix + "_bank_id", Long.class));
        return entity;
    }
}
