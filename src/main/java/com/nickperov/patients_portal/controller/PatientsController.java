package com.nickperov.patients_portal.controller;

import com.nickperov.patients_portal.api.PatientDTO;
import com.nickperov.patients_portal.api.PatientStatsDTO;
import com.nickperov.patients_portal.service.PatientsService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/patients")
public class PatientsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientsController.class);

    @Autowired
    private PatientsService patientsService;

    @GetMapping("/find")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get patient information"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public List<PatientDTO> findPatients(@RequestParam(required = false) final String name) {
        LOGGER.info("Request patients with name starts with: " + (name != null ? name : ""));
        return patientsService.findPatients(name).stream().map(PatientDTO::new).collect(Collectors.toList());
    }

    @GetMapping("/stats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get statistical information"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public List<PatientStatsDTO> countPatients(@RequestParam final int yearFrom, @RequestParam final int yearTo) {
        LOGGER.info("Request patients stats within year range: " + yearFrom + " - " + yearTo);
        if (yearFrom < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Negative year value: " + yearFrom);
        } else if (yearTo < 0 ) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Negative year value: " + yearFrom);
        } else if (yearFrom >= yearTo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Year_from must be before year_to parameter");
        }
        return this.patientsService.getPatientsStatistics(yearFrom, yearTo).stream().map(PatientStatsDTO::new).collect(Collectors.toList());
    }

    @PostMapping
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New patient request processed"),
            @ApiResponse(responseCode = "400", description = "Incorrect request"),
            @ApiResponse(responseCode = "500", description = "Internal error")
    })
    public boolean createPatient(@RequestBody final PatientDTO patient) {
        LOGGER.info("Request to create new patient: " + patient);
        validateRequestBody(patient);
        return patientsService.createNewPatient(patient);
    }

    private void validateRequestBody(final PatientDTO patient) {
        String errorMsg = null;
        if (patient.getFirstName() == null || patient.getFirstName().isBlank()) {
            errorMsg = "Invalid patient parameters, first name is missing";
        } else if (patient.getLastName() == null || patient.getLastName().isBlank()) {
            errorMsg = "Invalid patient parameters, last name is missing";
        } else if (patient.getDateOfBirth() == null) {
            errorMsg = "Invalid patient parameters, date of birth is missing";
        }
        if (errorMsg != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorMsg);
        }
    }
}
