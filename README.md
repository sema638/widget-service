# widget-service

### Implemented
* basic requirements
* paging support
* filtering by area (using R-Tree implementation https://github.com/davidmoten/rtree)
* SQL database support (H2 in-memory database)

### Running

#### With default memory repository implementation
mvn spring-boot:run

#### With database repository implementation
mvn spring-boot:run -Dspring.profiles.active=database

### Swagger
Open http://localhost:8080/swagger-ui/ in a browser to see API.

### Filtering details
Using R-Tree allows searching widgets in particular area in average O(logN) (in the worst case O(n)).
Tree is maintained at service level, widget creation/deletion/update are sync to the tree.