## Technologies used

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring MVC](https://docs.spring.io/spring-framework/reference/web/webmvc.html)

## How to execute

- Build the Project
```
gradlew build
```
- Execute
```
java -jar build/libs/storage-api-1.0-SNAPSHOT.jar
```

## Testing Endpoints

- Upload File
```
curl -X POST -F "file=@path/to/your/file.txt" http://localhost:8080/api/files/upload
```
- Download File
```
curl -OJL http://localhost:8080/api/files/download/your-file-name.txt
```
- List uploaded files
```
curl http://localhost:8080/api/files/list
```