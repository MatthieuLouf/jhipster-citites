package com.city_displayer_matthieu.domain;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * A City.
 */
@Entity
@Table(name = "city")
public class City implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "inhabitants")
    private Long inhabitants;

    @Column(name = "postal_code")
    private Integer postal_code;

    @Column(name = "created_date")
    private LocalDate created_date;

    @ManyToOne
    @JsonIgnoreProperties("cities")
    private User user;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public City name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getInhabitants() {
        return inhabitants;
    }

    public City inhabitants(Long inhabitants) {
        this.inhabitants = inhabitants;
        return this;
    }

    public void setInhabitants(Long inhabitants) {
        this.inhabitants = inhabitants;
    }

    public Integer getPostal_code() {
        return postal_code;
    }

    public City postal_code(Integer postal_code) {
        this.postal_code = postal_code;
        return this;
    }

    public void setPostal_code(Integer postal_code) {
        this.postal_code = postal_code;
    }

    public LocalDate getCreated_date() {
        return created_date;
    }

    public City created_date(LocalDate created_date) {
        this.created_date = created_date;
        return this;
    }

    public void setCreated_date(LocalDate created_date) {
        this.created_date = created_date;
    }

    public User getUser() {
        return user;
    }

    public City user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof City)) {
            return false;
        }
        return id != null && id.equals(((City) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "City{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", inhabitants=" + getInhabitants() +
            ", postal_code=" + getPostal_code() +
            ", created_date='" + getCreated_date() + "'" +
            "}";
    }
}
