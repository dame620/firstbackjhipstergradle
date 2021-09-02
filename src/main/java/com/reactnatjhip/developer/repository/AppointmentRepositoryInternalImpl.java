package com.reactnatjhip.developer.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.reactnatjhip.developer.domain.Appointment;
import com.reactnatjhip.developer.repository.rowmapper.AdviserRowMapper;
import com.reactnatjhip.developer.repository.rowmapper.AppointmentRowMapper;
import com.reactnatjhip.developer.repository.rowmapper.ManagerRowMapper;
import com.reactnatjhip.developer.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Appointment entity.
 */
@SuppressWarnings("unused")
class AppointmentRepositoryInternalImpl implements AppointmentRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AdviserRowMapper adviserMapper;
    private final ManagerRowMapper managerMapper;
    private final AppointmentRowMapper appointmentMapper;

    private static final Table entityTable = Table.aliased("appointment", EntityManager.ENTITY_ALIAS);
    private static final Table adviserTable = Table.aliased("adviser", "adviser");
    private static final Table managerTable = Table.aliased("manager", "manager");

    public AppointmentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AdviserRowMapper adviserMapper,
        ManagerRowMapper managerMapper,
        AppointmentRowMapper appointmentMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.adviserMapper = adviserMapper;
        this.managerMapper = managerMapper;
        this.appointmentMapper = appointmentMapper;
    }

    @Override
    public Flux<Appointment> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Appointment> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Appointment> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = AppointmentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AdviserSqlHelper.getColumns(adviserTable, "adviser"));
        columns.addAll(ManagerSqlHelper.getColumns(managerTable, "manager"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(adviserTable)
            .on(Column.create("adviser_id", entityTable))
            .equals(Column.create("id", adviserTable))
            .leftOuterJoin(managerTable)
            .on(Column.create("manager_id", entityTable))
            .equals(Column.create("id", managerTable));

        String select = entityManager.createSelect(selectFrom, Appointment.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(
                crit ->
                    new StringBuilder(select)
                        .append(" ")
                        .append("WHERE")
                        .append(" ")
                        .append(alias)
                        .append(".")
                        .append(crit.toString())
                        .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<Appointment> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Appointment> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Appointment process(Row row, RowMetadata metadata) {
        Appointment entity = appointmentMapper.apply(row, "e");
        entity.setAdviser(adviserMapper.apply(row, "adviser"));
        entity.setManager(managerMapper.apply(row, "manager"));
        return entity;
    }

    @Override
    public <S extends Appointment> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Appointment> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(
                    numberOfUpdates -> {
                        if (numberOfUpdates.intValue() <= 0) {
                            throw new IllegalStateException("Unable to update Appointment with id = " + entity.getId());
                        }
                        return entity;
                    }
                );
        }
    }

    @Override
    public Mono<Integer> update(Appointment entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}

class AppointmentSqlHelper {

    static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("reason", table, columnPrefix + "_reason"));
        columns.add(Column.aliased("date", table, columnPrefix + "_date"));
        columns.add(Column.aliased("state", table, columnPrefix + "_state"));
        columns.add(Column.aliased("reportreason", table, columnPrefix + "_reportreason"));

        columns.add(Column.aliased("adviser_id", table, columnPrefix + "_adviser_id"));
        columns.add(Column.aliased("manager_id", table, columnPrefix + "_manager_id"));
        return columns;
    }
}
