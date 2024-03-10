### Prerequisite
* Java 21
* Maven 3.6 +
* MySQL 8.0

### How to build
* In mysql create a database name call sampleDB
* in the application.yml spring.jpa.hibernate.ddl-auto: update
* mvn clean install

### How to host
* In tomcat 10 with java21
* Move the target/tap-sdk-sample-app.war to webapps folder of tomcat
* Start the tomcat server
* Add the following reverse proxy in the apache server
```
     #Tap-SDK-Sample-APP
     ProxyPass /tap-sdk-sample-app/ http://127.0.0.1:8080/tap-sdk-sample-app/
     ProxyPassReverse /tap-sdk-sample-app/ http://127.0.0.1:8080/tap-sdk-sample-app/

     ProxyPass /tap-sdk-sample-app http://127.0.0.1:8080/tap-sdk-sample-app
     ProxyPassReverse /tap-sdk-sample-app http://127.0.0.1:8080/tap-sdk-sample-app
```

### How to configure the new sdp app
```
* In the application.yml file update the following
- sdk:
      api:
        key: sdk app key
        secret: sdk app secret
        url: https://<your domain>/sdk/subscription/authorize
      redirect:
        url: https://<your domain>/tap-sdk-sample-app/sdk/
      app:
        url: https://<your domain>
        id: your your app id
        password: your app password
```