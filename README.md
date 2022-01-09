# image-store

This example service stores and serves images in `jpg` and `jpeg` format.

### Database
Service can store images in memory (persistence during runtime) or in GridFS from MongoDB (persistence in external storage).

You can switch target storage using `mongo.enabled` property in `application.properties` file:
 - `mongo.enabled=true` uses MongoDB
 - `mongo.enabled=false` uses service memory
 - by default service uses own memory

### How to use

 1. To start application make sure you are using Java 15 and run `./gradlew bootRun` command.
 2. To save image send POST request to `/images` endpoint for example:
     ```
    curl --request POST --url http://localhost:8080/images/ --header 'content-type: multipart/form-data' --form file=@/Users/amadeusz.zaradny/example/panda.jpeg
    ```
    response contains id associated with saved image for example `{"id":"61ba5705dc774443a1500251"}`.
 3. To read saved image with id `61ba5705dc774443a1500251` you can send GET request via browser (ex: google chrome):
    ```
    http://localhost:8080/images/61ba5705dc774443a1500251
    ```  
 4. Have fun :)
 
### How to run test

To run all tests use: `./gradlew check`

To run only integration tests use: `./gradlew integration`

To run only unit tests use: `./gradlew test`
 
 
   