#!/usr/bin/env bash
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/unusualactivity
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=password
export SPRING_DATASOURCE_DRIVER=com.mysql.jdbc.Driver
#SPRING_DATASOURCE_USERNAME=root;SPRING_DATASOURCE_PASSWORD=password;SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/unusualactivity;SPRING_DATASOURCE_URL_NEW=jdbc:sqlserver://fjdb.centralus.cloudapp.azure.com:1433/UA
#export SPRING_DATASOURCE_URL=jdbc:sqlserver://fjdb.centralus.cloudapp.azure.com:1433;database=UA
#export SPRING_DATASOURCE_USERNAME=devuser
#export SPRING_DATASOURCE_PASSWORD=devuser
mvn spring-boot:run
