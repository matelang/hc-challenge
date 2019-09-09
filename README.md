# HazelCast Cloud Developer Challenge Project

## How to run the project

To run this project you will need the following:
* Java
* Maven
* Node
* NPM
* A K8s cluster

For reference I have the following:
```shell
$ java -version
openjdk version "12.0.2" 2019-07-16
OpenJDK Runtime Environment (build 12.0.2+10)
OpenJDK 64-Bit Server VM (build 12.0.2+10, mixed mode)

$ mvn -v
Apache Maven 3.6.1 (NON-CANONICAL_2019-07-24T20:49:02Z_root; 2019-07-24T23:49:02+03:00)
Maven home: /opt/maven
Java version: 12.0.2, vendor: N/A, runtime: /usr/lib/jvm/java-12-openjdk
Default locale: en_US, platform encoding: UTF-8
OS name: "linux", version: "5.2.11-arch1-1-arch", arch: "amd64", family: "unix"

$ node -v
v12.10.0

$ npm -v
6.11.3

$ kubectl version 
Client Version: version.Info{Major:"1", Minor:"15", GitVersion:"v1.15.2", GitCommit:"f6278300bebbb750328ac16ee6dd3aa7d3549568", GitTreeState:"archive", BuildDate:"2019-08-29T18:43:18Z", GoVersion:"go1.12.9", Compiler:"gc", Platform:"linux/amd64"}
Server Version: version.Info{Major:"1", Minor:"14", GitVersion:"v1.14.6", GitCommit:"96fac5cd13a5dc064f7d9f4f23030a6aeface6cc", GitTreeState:"clean", BuildDate:"2019-08-19T11:05:16Z", GoVersion:"go1.12.9", Compiler:"gc", Platform:"linux/amd64"}

```

To run the backend, cd to the `api` directory in the repo root.
```shell
$ cd api 
$ mvn spring-boot:run
```
> Note: this will start an embedded Tomcat server on port 8080. Make sure it's available.

To run the frontend, cd to the `client` directory in the repo root.
```shell
$ cd client 
$ npm i # just execute once
$ npm start
```
> Note: this will start a node server on port 3000. Make sure it's available.

Open your browser (tested with Brave) Navigate to `http://localhost:3000`.


# Design considerations

## Backend

The backend application is a standard Spring Boot 2 application, which packages with an embedded Tomcat web container.

I used certain starters and their imported autoconfigurations to embrace convention over configuration for most of the aspects of the application.

The internal package and class layout is more Domain Driven than layered.

Exception handling can be further specialized by introducing new exception types and mapping them in a ControllerAdvice to specific error codes when the client needs to differentiate certain flows. For arguments sake I have implemented a Conflict which occurs when a Deployment by the given already exists, so I introduced the exception type DeploymentAlreadyExistsException.

For communicating with the k8s cluster I have used the official Java client, but tried to keep the service layer agnostic of the chosen k8s client, so a refactoring of the client layer would not impact either business logic, or contracts.

An enhancement would be to introduce DTOs making the REST API layer even more agnostic to the underlying service Value Objects, but for the sake of the challenge I considered it overkill, since I don't have heavy logic in the application.

Configuring the K8s connection & auth can be done using the application YAML, you can either pass in a kube config file `~/.kube/config` or use explicit configuration. A properly configured spring bean should be injected into the context by the Configuration class.

I have used the lombok library to generate accessors, getters, setters and builders for my DTOs and Value Objects(VO) avoiding boilerplate code.

Authentication is done by JWT tokens, issued by Google and verified on the backend. The implementation takes as parameter the discovery document and can automatically fetch JWKS and validate token based on `kid` header. In google's case there is a slight mismatch between the discovery documents host (with `https` scheme) and the actual value in the `iss` claim which gets verified, so I had to manually pass in the JWKS URL.

For demonstrations sake I have saved into the database the deployment name and image, but I am not sure this adds value, so I tried decoupling it from main application logic using Spring's BAU event publishing mechanism.
Currently it is sync, but could be refactored to be off the request thread for more independence, since it is not necessary for formulating the response.

## Frontend

I have implemented a very basic React based UI, probably it is not flawless, also error handling is absolutely missing.

I have used an infinite scrolling component to intelligently consume the RESTful pagination provided by the deployments call.


# Links

* [Original Assignment](./ASSIGNMENT.md "Assignment Description")

