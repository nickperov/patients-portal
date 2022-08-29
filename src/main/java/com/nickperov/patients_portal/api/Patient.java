package com.nickperov.patients_portal.api;

import java.time.LocalDate;

public interface Patient {

    String getFirstName();

    String getLastName();

    LocalDate getDateOfBirth();
}
