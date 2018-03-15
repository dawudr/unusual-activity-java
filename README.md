# FINANCIAL JUICE - unusual activity

## VOLUME BREAKOUTS

This app will perform statistical methods on financial data to detect unusual spikes in volume near-realtime and present in a dashboard.

## Design

![Alt text](doc/FJ-UA-Volume_breakouts_Design.002.jpeg?raw=true "Features")

![Alt text](doc/FJ-UA-Volume_breakouts_Design.003.jpeg?raw=true "Algorithm")

![Alt text](doc/FJ-UA-Volume_breakouts_Design.004.jpeg?raw=true "Data Model")

## Running the Java program Backend

1. Set your MySql environment variables in run.sh script.
```
export SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/unusualactivity
export SPRING_DATASOURCE_USERNAME=root
export SPRING_DATASOURCE_PASSWORD=password
```

To run the application use script.
```
./run.sh
```

And browse to:
![http://localhost:8080/api/stock](http://localhost:8080/api/stock)
This will give a JSON API Response of [] or stock data json if there are records in the Mysql database.



## Setting up the database
mysql -h localhost -u root -p xxx

ALTER USER user IDENTIFIED BY 'xxx';

create database unusualactivity;


# StockApp - Node and Angular Frontend

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 1.6.7.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

And browse to:
![http://localhost:4200/](http://localhost:4200/)
This will load up the application page.


## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `-prod` flag for a production build.


## Further help

First of all, we need to setup Angular CLI for code generating.

npm install -g @angular/cli

go to the project directory and using below command to generate the angular application.

ng new stock-app --routing

then go the stock-app directory and run the following command to test your app locally.

cd stock-app

ng serve

finally, go to the http://localhost:4200/

Use this command to generate use the stock module.

ng generate module stock --routing

Then we need to a service to call the REST services. We called it a stock service.

ng generate service stock/stock

Now we have to generate sub-components.

ng generate component /stock/stock-list

ng generate component /stock/stock-create

First of all, we will add the bootstrap 4 into the project. Go to the user-appdirectory and run following command.

npm install --save bootstrap font-awesome


Proxy To Backend
Integrate SpringBoot server and Angular 4 client
Up to now, Angular4-Client and SpringBoot server work independently on ports 8080 and 4200.
Goal of below integration: the client at 4200 will proxy any /api requests to the server.

Step to do:
– Create a file proxy.conf.json under project angular4-client folder with content:


{
	"/api": {
		"target": "http://localhost:8080",
		"secure": false
	}
}


"scripts": {
    "ng": "ng",
    "start": "ng serve --proxy-config proxy.conf.json",
    "build": "ng build",
    "test": "ng test",
    "lint": "ng lint",
    "e2e": "ng e2e"
},

– Build and run the RestfulService project with SpringBoot App mode at port 8080. And run angular4-client at port 4200.
https://github.com/angular/angular-cli/blob/master/docs/documentation/stories/proxy.md

We can then edit the package.json file's start script to be

"start": "ng serve --proxy-config proxy.conf.json",
