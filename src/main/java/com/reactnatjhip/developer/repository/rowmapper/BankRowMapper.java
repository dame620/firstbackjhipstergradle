package com.reactnatjhip.developer.repository.rowmapper;

import com.reactnatjhip.developer.domain.Bank;
import com.reactnatjhip.developer.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Bank}, with proper type conversions.
 */
@Service
public class BankRowMapper implements BiFunction<Row, String, Bank> {

    private final ColumnConverter converter;

    public BankRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Bank} stored in the database.
     */
    @Override
    public Bank apply(Row row, String prefix) {
        Bank entity = new Bank();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        return entity;
    }
}
