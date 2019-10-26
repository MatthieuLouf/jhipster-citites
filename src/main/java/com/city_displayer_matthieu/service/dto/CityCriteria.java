package com.city_displayer_matthieu.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.LocalDateFilter;

/**
 * Criteria class for the {@link com.city_displayer_matthieu.domain.City} entity. This class is used
 * in {@link com.city_displayer_matthieu.web.rest.CityResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /cities?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CityCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private LongFilter inhabitants;

    private IntegerFilter postal_code;

    private LocalDateFilter created_date;

    private LongFilter userId;

    public CityCriteria(){
    }

    public CityCriteria(CityCriteria other){
        this.id = other.id == null ? null : other.id.copy();
        this.name = other.name == null ? null : other.name.copy();
        this.inhabitants = other.inhabitants == null ? null : other.inhabitants.copy();
        this.postal_code = other.postal_code == null ? null : other.postal_code.copy();
        this.created_date = other.created_date == null ? null : other.created_date.copy();
        this.userId = other.userId == null ? null : other.userId.copy();
    }

    @Override
    public CityCriteria copy() {
        return new CityCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getName() {
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
    }

    public LongFilter getInhabitants() {
        return inhabitants;
    }

    public void setInhabitants(LongFilter inhabitants) {
        this.inhabitants = inhabitants;
    }

    public IntegerFilter getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(IntegerFilter postal_code) {
        this.postal_code = postal_code;
    }

    public LocalDateFilter getCreated_date() {
        return created_date;
    }

    public void setCreated_date(LocalDateFilter created_date) {
        this.created_date = created_date;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CityCriteria that = (CityCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(inhabitants, that.inhabitants) &&
            Objects.equals(postal_code, that.postal_code) &&
            Objects.equals(created_date, that.created_date) &&
            Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        name,
        inhabitants,
        postal_code,
        created_date,
        userId
        );
    }

    @Override
    public String toString() {
        return "CityCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (name != null ? "name=" + name + ", " : "") +
                (inhabitants != null ? "inhabitants=" + inhabitants + ", " : "") +
                (postal_code != null ? "postal_code=" + postal_code + ", " : "") +
                (created_date != null ? "created_date=" + created_date + ", " : "") +
                (userId != null ? "userId=" + userId + ", " : "") +
            "}";
    }

}
