package com.nickperov.patients_portal.service;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.nickperov.patients_portal.api.PatientStats;
import java.util.Objects;

public class PatientStatsImpl implements PatientStats {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PatientStatsImpl(@JsonProperty("count") long count, @JsonProperty("p_year") int year) {
        this.count = count;
        this.year = year;
    }

    private final int year;
    private final long count;

    @Override
    public int getYear() {
        return year;
    }

    @Override
    public long getCount() {
        return count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PatientStatsImpl that = (PatientStatsImpl) o;
        return year == that.year && count == that.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, count);
    }

    @Override
    public String toString() {
        return "PatientStatsImpl{" +
                "year=" + year +
                ", count=" + count +
                '}';
    }
}
