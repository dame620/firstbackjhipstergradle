package com.reactnatjhip.developer.repository.rowmapper;

import com.reactnatjhip.developer.domain.Appointment;
import com.reactnatjhip.developer.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Appointment}, with proper type conversions.
 */
@Service
public class AppointmentRowMapper implements BiFunction<Row, String, Appointment> {

    private final ColumnConverter converter;

    public AppointmentRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Appointment} stored in the database.
     */
    @Override
    public Appointment apply(Row row, String prefix) {
        Appointment entity = new Appointment();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setReason(converter.fromRow(row, prefix + "_reason", String.class));
        entity.setDate(converter.fromRow(row, prefix + "_date", Instant.class));
        entity.setState(converter.fromRow(row, prefix + "_state", Boolean.class));
        entity.setReportreason(converter.fromRow(row, prefix + "_reportreason", String.class));
        entity.setAdviserId(converter.fromRow(row, prefix + "_adviser_id", Long.class));
        entity.setManagerId(converter.fromRow(row, prefix + "_manager_id", Long.class));
        return entity;
    }
}
