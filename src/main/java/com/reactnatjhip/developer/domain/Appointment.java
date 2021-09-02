package com.reactnatjhip.developer.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Appointment.
 */
@Table("appointment")
public class Appointment implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("reason")
    private String reason;

    @Column("date")
    private Instant date;

    @Column("state")
    private Boolean state;

    @Column("reportreason")
    private String reportreason;

    @JsonIgnoreProperties(value = { "user", "bank" }, allowSetters = true)
    @Transient
    private Adviser adviser;

    @Column("adviser_id")
    private Long adviserId;

    @JsonIgnoreProperties(value = { "user", "company" }, allowSetters = true)
    @Transient
    private Manager manager;

    @Column("manager_id")
    private Long managerId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Appointment id(Long id) {
        this.id = id;
        return this;
    }

    public String getReason() {
        return this.reason;
    }

    public Appointment reason(String reason) {
        this.reason = reason;
        return this;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Instant getDate() {
        return this.date;
    }

    public Appointment date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Boolean getState() {
        return this.state;
    }

    public Appointment state(Boolean state) {
        this.state = state;
        return this;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getReportreason() {
        return this.reportreason;
    }

    public Appointment reportreason(String reportreason) {
        this.reportreason = reportreason;
        return this;
    }

    public void setReportreason(String reportreason) {
        this.reportreason = reportreason;
    }

    public Adviser getAdviser() {
        return this.adviser;
    }

    public Appointment adviser(Adviser adviser) {
        this.setAdviser(adviser);
        this.adviserId = adviser != null ? adviser.getId() : null;
        return this;
    }

    public void setAdviser(Adviser adviser) {
        this.adviser = adviser;
        this.adviserId = adviser != null ? adviser.getId() : null;
    }

    public Long getAdviserId() {
        return this.adviserId;
    }

    public void setAdviserId(Long adviser) {
        this.adviserId = adviser;
    }

    public Manager getManager() {
        return this.manager;
    }

    public Appointment manager(Manager manager) {
        this.setManager(manager);
        this.managerId = manager != null ? manager.getId() : null;
        return this;
    }

    public void setManager(Manager manager) {
        this.manager = manager;
        this.managerId = manager != null ? manager.getId() : null;
    }

    public Long getManagerId() {
        return this.managerId;
    }

    public void setManagerId(Long manager) {
        this.managerId = manager;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Appointment)) {
            return false;
        }
        return id != null && id.equals(((Appointment) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Appointment{" +
            "id=" + getId() +
            ", reason='" + getReason() + "'" +
            ", date='" + getDate() + "'" +
            ", state='" + getState() + "'" +
            ", reportreason='" + getReportreason() + "'" +
            "}";
    }
}
