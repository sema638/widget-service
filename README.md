# widget-service

### Implemented
* basic requirements
* paging support
* SQL database support (H2 in-memory database)

### Running

#### With default memory repository implementation
mvn spring-boot:run

#### With database repository implementation
mvn spring-boot:run -Dspring.profiles.active=database

### Swagger
Open http://localhost:8080/swagger-ui/ in a browser to see API.