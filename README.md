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

## Project description (by huseinbabal)
In this challenge, you are requested to implement a REST API in Spring Boot and UI (will be a plus) in any technology for orchestrating a Kubernetes cluster.

Requirements
User Spring Boot to create a REST API that has necessary endpoints to create & list deployment on Kubernetes

Relay on REST Maturity Model to provide good interface to REST API consumers

There will be a simple UI (if possible) to create and list deployments

If there is no UI, provide details for calling REST endpoints to create and list deployments in README.md file

A couple of deployment details like name, image, etc... will be persisted in database. In memory db like h2 would be a good fit to run project locally with less dependencies

Good exception handling design

Relay on clean code practices and keep in mind that your codebase will be reviewed according to that patterns.

Good unit test coverage for REST API and provide detail in README.md to run test coverages

Assume that, kubernetes cluster is on an external infrastructure, and prepare your client according to that manner. Your REST API will use that client to create & list deployments on k8s cluster.

Use JWT token to access REST API from UI project

Prepare a README.md to show how to execute project and a quick design schema will be a plus

Once you finished your work, put all the necessary content within a git project that includes a README.md that will help us how to execute flow on our side. It can be Github, Gitlab, or Bitbucket. If it will be a private repository, please provide read access to user huseyinbabal

Prevent Gold Plating! Do what is said in requirements

Prove that you are the Rock'n Roll of Spring Boot and Kubernetes.