package com.nickperov.patients_portal.service;

import com.nickperov.patients_portal.api.Patient;
import com.nickperov.patients_portal.api.PatientStats;
import java.math.BigInteger;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PatientsServiceImpl implements PatientsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientsService.class);

    private final PatientRepository patientRepository;
    private final EntityManager entityManager;

    @Autowired
    public PatientsServiceImpl(final PatientRepository patientRepository, final EntityManager entityManager) {
        this.patientRepository = patientRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public List<? extends Patient> findPatients(final String name) {
        if (name == null || name.isBlank()) {
            return this.patientRepository.findAll();
        } else {
            final String searchNameStr = name.trim();
            final int spaceIndex = searchNameStr.indexOf(' ');
            if (spaceIndex != -1) {
                // Contains first name and last name prefix
                final String firstName = searchNameStr.substring(0, spaceIndex);
                final String lastNamePrefix = searchNameStr.substring(spaceIndex).trim();
                return this.patientRepository.findByFirstNameAndLastNameStartingWith(firstName, lastNamePrefix);
            } else {
                // Contains first name prefix only
                return this.patientRepository.findByFirstNameStartingWith(searchNameStr);
            }
        }
    }

    @Override
    public List<? extends PatientStats> getPatientsStatistics(final int yearFrom, final int yearTo) {
        final Query statsQuery = entityManager.createNativeQuery(
                "select count(id) as count, p_year "
                        + "from (select id,  extract(year from date_of_birth) AS p_year from patient "
                        + "where extract(year from date_of_birth) >= ?1 and  extract(year from date_of_birth) < ?2) "
                        + "p_years group by p_year order by count DESC");
        statsQuery.setParameter(1, yearFrom);
        statsQuery.setParameter(2, yearTo);

        return (List<PatientStatsImpl>) statsQuery.getResultStream()
                .map(row -> {
                    if (row != null && row instanceof Object[] && ((Object[]) row).length == 2) {
                        final Object[] statsRow = (Object[]) row;
                        return new PatientStatsImpl(((BigInteger) statsRow[0]).longValue(), (Integer) statsRow[1]);
                    } else {
                        return null;
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean createNewPatient(final Patient patient) {
        final PatientImpl existingPatient = this.patientRepository.find(patient.getFirstName(), patient.getLastName(), patient.getDateOfBirth());
        if (existingPatient != null) {
            LOGGER.warn("Such user already exists: " + existingPatient);
            return false;
        } else {
            final PatientImpl newPatient = this.patientRepository.save(new PatientImpl(patient));
            LOGGER.info("New patient created: " + newPatient);
            return true;
        }
    }
}
