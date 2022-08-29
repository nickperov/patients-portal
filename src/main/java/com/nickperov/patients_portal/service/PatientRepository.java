package com.nickperov.patients_portal.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PatientRepository extends JpaRepository<PatientImpl, Integer> {


    @Query("select p from PatientImpl p where p.firstName = :firstName and p.lastName = :lastName and p.dateOfBirth = :dateOfBirth")
    PatientImpl find(final String firstName, final String lastName, final LocalDate dateOfBirth);

    @Query("select p from PatientImpl p where p.firstName like :firstNamePrefix% order by p.firstName, p.lastName, p.dateOfBirth")
    List<PatientImpl> findByFirstNameStartingWith(final String firstNamePrefix);

    @Query("select p from PatientImpl p where p.firstName = :firstName and p.lastName like :lastNameNamePrefix% order by p.firstName, p.lastName, p.dateOfBirth")
    List<PatientImpl> findByFirstNameAndLastNameStartingWith(final String firstName, final String lastNameNamePrefix);
}
