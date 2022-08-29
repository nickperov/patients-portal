package com.nickperov.patients_portal.service;

import com.nickperov.patients_portal.api.Patient;
import com.nickperov.patients_portal.api.PatientStats;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;

public interface PatientsService {
    
    List<? extends Patient> findPatients(@RequestBody String name);

    List<? extends PatientStats> getPatientsStatistics(int yearFrom, int yearTo);

    boolean createNewPatient(@RequestBody Patient patient);
}
