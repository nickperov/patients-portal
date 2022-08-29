
## Test patients portal

### Build and run instructions
#### Prerequisites:
* Java version 17
* maven

#### To build
```
mvn clean package
```
#### To run locally
```
mvn spring-boot:run
```
#### API contract
http://localhost:8080/swagger-ui/index.html

#### Portal API interface:

* To create new patient: POST - /api/patients/
* To find patients: GET - /api/patients/find (optional parameter: name[String])
* To retrieve patients statistics: GET - /api/patients/stats (compulsory parameters: yearFrom[positive int],  yearTo[positive int])

[API Contract](api-docs.json)