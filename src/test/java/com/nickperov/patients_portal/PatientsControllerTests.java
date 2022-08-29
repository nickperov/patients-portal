package com.nickperov.patients_portal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nickperov.patients_portal.api.Patient;
import com.nickperov.patients_portal.api.PatientDTO;
import com.nickperov.patients_portal.api.PatientStatsDTO;
import com.nickperov.patients_portal.service.PatientRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class PatientsControllerTests {

    private final ObjectMapper mapper = new ObjectMapper();

    {
        mapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    private MockMvc mvc;

    @Autowired
    private PatientRepository patientRepository;

    @AfterEach
    public void cleanDB() {
        patientRepository.deleteAll();
    }

    @Test
    void testCreateOld() throws Exception {
        final LocalDate dateOfBirth = LocalDate.of(1912, 3, 1);
        final PatientDTO patientOne = createNewPatient("John", "Old", dateOfBirth);
        executeAddPatient(patientOne, true);
    }

    @Test
    void testCreateDuplicate() throws Exception {
        final LocalDate dateOfBirth = LocalDate.of(1986, 7, 4);
        final PatientDTO patientOne = createNewPatient("Alice", "French", dateOfBirth);
        final PatientDTO patientTwo = createNewPatient("Alice", "French", dateOfBirth);

        executeAddPatient(patientOne, true);
        executeAddPatient(patientTwo, false);
    }

    @Test
    void testCreateSameName() throws Exception {
        final PatientDTO patientOne = createNewPatient("Lina", "Gomez", LocalDate.of(1986, 7, 4));
        final PatientDTO patientTwo = createNewPatient("Lina", "Gomez", LocalDate.of(1986, 7, 5));

        executeAddPatient(patientOne, true);
        executeAddPatient(patientTwo, true);
    }

    @Test
    void testCreateSameDate() throws Exception {
        final PatientDTO patientOne = createNewPatient("Ella", "Winsent", LocalDate.of(1986, 7, 4));
        final PatientDTO patientTwo = createNewPatient("Fred", "Killo", LocalDate.of(1986, 7, 4));

        executeAddPatient(patientOne, true);
        executeAddPatient(patientTwo, true);
    }

    @Test
    void testCreateAndGet() throws Exception {
        final PatientDTO patientOne = createNewPatient("Bob", "Brown", LocalDate.now());
        final PatientDTO patientTwo = createNewPatient("Bob", "Wheeler", LocalDate.now());
        final PatientDTO patientThree = createNewPatient("Tom", "Hampton", LocalDate.now());

        executeAddPatient(patientOne, true);
        executeAddPatient(patientTwo, true);
        executeAddPatient(patientThree, true);

        // Find all
        final MvcResult result = mvc.perform(get("/api/patients/find"))
                .andExpect(status().isOk())
                .andReturn();

        final List<PatientDTO> patientList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNotNull(patientList);
        assertEquals(3, patientList.size());
        assertTrue(patientList.contains(patientOne));
        assertTrue(patientList.contains(patientTwo));
        assertTrue(patientList.contains(patientThree));
    }

    @Test
    void testFindByFirstName() throws Exception {
        final PatientDTO patientOne = createNewPatient("Tom", "Kent", LocalDate.now());
        final PatientDTO patientTwo = createNewPatient("Tom", "Ford", LocalDate.now());
        final PatientDTO patientThree = createNewPatient("Tom", "Campos", LocalDate.now());
        final PatientDTO patientFour = createNewPatient("Bill", "Howell", LocalDate.now());

        executeAddPatient(patientOne, true);
        executeAddPatient(patientTwo, true);
        executeAddPatient(patientThree, true);
        executeAddPatient(patientFour, true);

        // Find all
        final MvcResult result = mvc.perform(get("/api/patients/find?name=Tom"))
                .andExpect(status().isOk())
                .andReturn();

        final List<PatientDTO> patientList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNotNull(patientList);
        assertEquals(3, patientList.size());
        assertTrue(patientList.contains(patientOne));
        assertTrue(patientList.contains(patientTwo));
        assertTrue(patientList.contains(patientThree));
    }

    @Test
    void testFindByFirstAndLastName() throws Exception {
        final PatientDTO patientOne = createNewPatient("Alan", "Rowe", LocalDate.now());
        final PatientDTO patientTwo = createNewPatient("Alan", "Roman", LocalDate.now());
        final PatientDTO patientThree = createNewPatient("Alan", "Santos", LocalDate.now());

        executeAddPatient(patientOne, true);
        executeAddPatient(patientTwo, true);
        executeAddPatient(patientThree, true);

        // Find all
        final MvcResult result = mvc.perform(get("/api/patients/find?name=Alan Ro"))
                .andExpect(status().isOk())
                .andReturn();

        final List<PatientDTO> patientList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNotNull(patientList);
        assertEquals(2, patientList.size());
        assertTrue(patientList.contains(patientOne));
        assertTrue(patientList.contains(patientTwo));
    }

    @Test
    void testFindByFirstAndLastNameMultipleSpaces() throws Exception {
        final PatientDTO patientOne = createNewPatient("Alan", "Rowe", LocalDate.now());
        final PatientDTO patientTwo = createNewPatient("Alan", "Roman", LocalDate.now());
        final PatientDTO patientThree = createNewPatient("Alan", "Santos", LocalDate.now());

        executeAddPatient(patientOne, true);
        executeAddPatient(patientTwo, true);
        executeAddPatient(patientThree, true);

        // Add spaces  
        final MvcResult result = mvc.perform(get("/api/patients/find?name=  Alan   Ro  "))
                .andExpect(status().isOk())
                .andReturn();

        final List<PatientDTO> patientList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNotNull(patientList);
        assertEquals(2, patientList.size());
        assertTrue(patientList.contains(patientOne));
        assertTrue(patientList.contains(patientTwo));
    }

    @Test
    void testFindByFirstNamePrefix() throws Exception {
        final PatientDTO patientOne = createNewPatient("Damian", "Hoover", LocalDate.now());
        final PatientDTO patientTwo = createNewPatient("Daniel", "Baxter", LocalDate.now());
        final PatientDTO patientThree = createNewPatient("Danna", "Wolfe", LocalDate.now());
        final PatientDTO patientFour = createNewPatient("Debby", "Park", LocalDate.now());
        final PatientDTO patientFive = createNewPatient("Duppy", "Schultz", LocalDate.now());

        executeAddPatient(patientOne, true);
        executeAddPatient(patientTwo, true);
        executeAddPatient(patientThree, true);
        executeAddPatient(patientFour, true);
        executeAddPatient(patientFive, true);

        // Find all
        final MvcResult result = mvc.perform(get("/api/patients/find?name=Da"))
                .andExpect(status().isOk())
                .andReturn();

        final List<PatientDTO> patientList = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNotNull(patientList);
        assertEquals(3, patientList.size());
        assertTrue(patientList.contains(patientOne));
        assertTrue(patientList.contains(patientTwo));
        assertTrue(patientList.contains(patientThree));
    }

    @Test
    void testPatientStats() throws Exception {
        createNumberOfPatients(1980, "Test80th", 30);
        createNumberOfPatients(1981, "Test81th", 12);
        createNumberOfPatients(1982, "Test82th", 42);
        createNumberOfPatients(1985, "Test85th", 10);
        createNumberOfPatients(1987, "Test87th", 33);
        createNumberOfPatients(1990, "Test90th", 5);
        createNumberOfPatients(1995, "Test90th", 25);

        final MvcResult resultAll = mvc.perform(get("/api/patients/stats?yearFrom=1900&yearTo=2022"))
                .andExpect(status().isOk())
                .andReturn();

        final List<PatientStatsDTO> patientAllStats = mapper.readValue(resultAll.getResponse().getContentAsString(), new TypeReference<>() {
        });
        assertNotNull(patientAllStats);
        assertEquals(7, patientAllStats.size());
        assertEquals(42, patientAllStats.get(0).getCount());
        assertEquals(33, patientAllStats.get(1).getCount());
        assertEquals(30, patientAllStats.get(2).getCount());
        assertEquals(25, patientAllStats.get(3).getCount());
        assertEquals(12, patientAllStats.get(4).getCount());
        assertEquals(10, patientAllStats.get(5).getCount());
        assertEquals(5, patientAllStats.get(6).getCount());

        final Map<Integer, Long> patientAllStatsMap = patientAllStats.stream().collect(Collectors.toMap(PatientStatsDTO::getYear, PatientStatsDTO::getCount));

        assertEquals(30, patientAllStatsMap.get(1980));
        assertEquals(12, patientAllStatsMap.get(1981));
        assertEquals(42, patientAllStatsMap.get(1982));
        assertEquals(10, patientAllStatsMap.get(1985));
        assertEquals(33, patientAllStatsMap.get(1987));
        assertEquals(5, patientAllStatsMap.get(1990));
        assertEquals(25, patientAllStatsMap.get(1995));

        // Test interval 1981 inclusive and 1990 exclusive
        final MvcResult resultInterval81_90 = mvc.perform(get("/api/patients/stats?yearFrom=1981&yearTo=1990"))
                .andExpect(status().isOk())
                .andReturn();

        final List<PatientStatsDTO> patientInterval81_90Stats = mapper.readValue(resultInterval81_90.getResponse().getContentAsString(), new TypeReference<>() {
        });

        assertNotNull(patientInterval81_90Stats);
        assertEquals(4, patientInterval81_90Stats.size());

        final Map<Integer, Long> patientInterval81_90StatsMap = patientInterval81_90Stats.stream().collect(Collectors.toMap(PatientStatsDTO::getYear, PatientStatsDTO::getCount));

        assertEquals(12, patientInterval81_90StatsMap.get(1981));
        assertEquals(42, patientInterval81_90StatsMap.get(1982));
        assertEquals(10, patientInterval81_90StatsMap.get(1985));
        assertEquals(33, patientInterval81_90StatsMap.get(1987));
    }

    private void createNumberOfPatients(final int year, final String firstNamePrefix, final int number) throws Exception {
        final Random rnd = new Random();
        for (int i = 0; i < number; i++) {
            final int dayOfYear = 1 + rnd.nextInt(360);
            final LocalDate dateOfBirth = LocalDate.ofYearDay(year, dayOfYear);
            executeAddPatient(createNewPatient(firstNamePrefix + i, "TestSurname", dateOfBirth), true);
        }
    }

    private void executeAddPatient(final Patient patient, final boolean expectedResult) throws Exception {
        mvc.perform(post("/api/patients/").contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(patient)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedResult).toLowerCase()));
    }


    private static PatientDTO createNewPatient(final String firstName, final String lastName, final LocalDate dateOfBirth) {
        return new PatientDTO(new Patient() {
            @Override
            public String getFirstName() {
                return firstName;
            }

            @Override
            public String getLastName() {
                return lastName;
            }

            @Override
            public LocalDate getDateOfBirth() {
                return dateOfBirth;
            }
        });
    }
}
