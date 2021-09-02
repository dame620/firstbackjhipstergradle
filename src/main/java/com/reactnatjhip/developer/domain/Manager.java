package com.reactnatjhip.developer.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Manager.
 */
@Table("manager")
public class Manager implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("registration_number")
    private String registrationNumber;

    @Column("department")
    private String department;

    private Long userId;

    @Transient
    private User user;

    @Transient
    private Company company;

    @Column("company_id")
    private Long companyId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Manager id(Long id) {
        this.id = id;
        return this;
    }

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    public Manager registrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
        return this;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getDepartment() {
        return this.department;
    }

    public Manager department(String department) {
        this.department = department;
        return this;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public User getUser() {
        return this.user;
    }

    public Manager user(User user) {
        this.setUser(user);
        this.userId = user != null ? user.getId() : null;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
        this.userId = user != null ? user.getId() : null;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long user) {
        this.userId = user;
    }

    public Company getCompany() {
        return this.company;
    }

    public Manager company(Company company) {
        this.setCompany(company);
        this.companyId = company != null ? company.getId() : null;
        return this;
    }

    public void setCompany(Company company) {
        this.company = company;
        this.companyId = company != null ? company.getId() : null;
    }

    public Long getCompanyId() {
        return this.companyId;
    }

    public void setCompanyId(Long company) {
        this.companyId = company;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Manager)) {
            return false;
        }
        return id != null && id.equals(((Manager) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Manager{" +
            "id=" + getId() +
            ", registrationNumber='" + getRegistrationNumber() + "'" +
            ", department='" + getDepartment() + "'" +
            "}";
    }
}
