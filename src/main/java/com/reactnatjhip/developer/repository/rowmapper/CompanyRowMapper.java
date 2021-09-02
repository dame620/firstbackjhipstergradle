package com.reactnatjhip.developer.repository.rowmapper;

import com.reactnatjhip.developer.domain.Company;
import com.reactnatjhip.developer.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Company}, with proper type conversions.
 */
@Service
public class CompanyRowMapper implements BiFunction<Row, String, Company> {

    private final ColumnConverter converter;

    public CompanyRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Company} stored in the database.
     */
    @Override
    public Company apply(Row row, String prefix) {
        Company entity = new Company();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setName(converter.fromRow(row, prefix + "_name", String.class));
        entity.setNinea(converter.fromRow(row, prefix + "_ninea", String.class));
        entity.setRc(converter.fromRow(row, prefix + "_rc", String.class));
        entity.setAddress(converter.fromRow(row, prefix + "_address", String.class));
        return entity;
    }
}
