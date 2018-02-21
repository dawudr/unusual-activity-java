# StockApp

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 1.6.7.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|guard|interface|enum|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `-prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).

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
