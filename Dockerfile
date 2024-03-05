FROM tomcat:10.1.19-jre21
LABEL authors="sripiranavan"
LABEL maintainer="sripiranavan.y@hsenidmobile.com"

VOLUME /tmp

EXPOSE 8080

ARG WAR_FOLDER=target/tap-sdk-sample-app
COPY ${WAR_FOLDER} /usr/local/tomcat/webapps/

CMD ["catalina.sh", "run"]