package com.reactnatjhip.developer.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Adviser.
 */
@Table("adviser")
public class Adviser implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column("registration_number")
    private String registrationNumber;

    @Column("company")
    private String company;

    @Column("department")
    private String department;

    private Long userId;

    @Transient
    private User user;

    @Transient
    private Bank bank;

    @Column("bank_id")
    private Long bankId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Adviser id(Long id) {
        this.id = id;
        return this;
    }

    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    public Adviser registrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
        return this;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getCompany() {
        return this.company;
    }

    public Adviser company(String company) {
        this.company = company;
        return this;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDepartment() {
        return this.department;
    }

    public Adviser department(String department) {
        this.department = department;
        return this;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public User getUser() {
        return this.user;
    }

    public Adviser user(User user) {
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

    public Bank getBank() {
        return this.bank;
    }

    public Adviser bank(Bank bank) {
        this.setBank(bank);
        this.bankId = bank != null ? bank.getId() : null;
        return this;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
        this.bankId = bank != null ? bank.getId() : null;
    }

    public Long getBankId() {
        return this.bankId;
    }

    public void setBankId(Long bank) {
        this.bankId = bank;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Adviser)) {
            return false;
        }
        return id != null && id.equals(((Adviser) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Adviser{" +
            "id=" + getId() +
            ", registrationNumber='" + getRegistrationNumber() + "'" +
            ", company='" + getCompany() + "'" +
            ", department='" + getDepartment() + "'" +
            "}";
    }
}
