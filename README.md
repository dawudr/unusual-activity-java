# FINANCIAL JUICE - unusual activity

## VOLUME BREAKOUTS

This app will perform statistical methods on financial data to detect unusual spikes in volume near-realtime and present in a dashboard.

## Design

![Alt text](doc/FJ-UA-Volume_breakouts_Design.002.jpeg?raw=true "Features")

![Alt text](doc/FJ-UA-Volume_breakouts_Design.003.jpeg?raw=true "Algorithm")

![Alt text](doc/FJ-UA-Volume_breakouts_Design.004.jpeg?raw=true "Data Model")

## Running the program
To run the application:
```
mvn spring-boot:run
```

```
cd web
npm install
npm start
```

And browse to:
![http://localhost:4200/](http://localhost:4200/)
This will load up the application page.



## Setting up the database
mysql -h localhost -u root -p xxx

ALTER USER user IDENTIFIED BY 'xxx';

create database unusualactivity;