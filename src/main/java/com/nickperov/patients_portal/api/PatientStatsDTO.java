package com.nickperov.patients_portal.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Objects;

public class PatientStatsDTO implements PatientStats {

    private final int year;
    private final long count;


    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    private PatientStatsDTO(@JsonProperty("year") final int year, @JsonProperty("count") final long count) {
        this.year = year;
        this.count = count;
    }

    public PatientStatsDTO(final PatientStats patientStats) {
        this(patientStats.getYear(), patientStats.getCount());
    }

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
        PatientStatsDTO that = (PatientStatsDTO) o;
        return year == that.year && count == that.count;
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, count);
    }

    @Override
    public String toString() {
        return "PatientStatsDTO{" +
                "year=" + year +
                ", count=" + count +
                '}';
    }
}
