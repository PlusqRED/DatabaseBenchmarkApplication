# Database Benchmark Application

Service for testing different types of databases.

## Getting Started

This service helps to in parallel extract the results of execution registered services and their endpoints.

Out-of-box application supports *(but you can easily expand the current set of services according to the following rules: see section **Usage**)*:

* PostgreSQL
* MongoDB
* Neo4j

You can create endpoints in every service with **different query** types, then register them by *REST Post* request with *BenchmarkService* 
and collect execution reports.

### Prerequisites

To install docker and docker-compose which are needed to run the services run the following commands

```
sudo apt-get update
```
Install docker engine:
```
sudo apt-get install docker-ce docker-ce-cli containerd.io
```
Install docker-compose engine:
```
sudo curl -L https://github.com/docker/compose/releases/download/1.18.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
```
## Usage

Every service needs to have a class that will register itself in BenchmarkService, registration is performed using rest **POST** request:

```
 Benchmark benchmark = Benchmark.builder()
                .hostName(applicationName)
                .port(serverPort)
                .benchmarkEndpoints(List.of("/likes", "/friends"))
                .build();
        restTemplate.postForEntity(
                String.format("http://%s:%d/benchmarks/register", {benchmarkService_host}, {benchmarkService_port}),
                {benchmark_json_example_is_below},
                String.class
        );
```
JSON form of Benchmark for registration:
```
{
  hostName: 'host',
  port: {integer}
  benchmarkEndpoints: ['/example', '/another']
}
```
Benchmarkservice expects a response from each service in the following **JSON** format:  

**Example:**
```
{
      "hostName": "{host_or_hostName_if_deployed_in_same_network}",
      "port": {your_service_port},
      "endpointName": "/example",
      "indicators": {
        "timeInSec": 0.025,
        "querySize": 2000
      },
      "errors": {
        "hasErrors": false,
        "message": null,
        "stackTrace": null
      }
}
```
## BenchmarkService API

* http://localhost:8989/benchmarks - executes all registered benchmarks and their endpoints with overall report generation.

* http://localhost:8989/benchmarks?iterations={number_of_iterations} - executes all registered benchmarks and their endpoints 
specified number of times with aggregated reports.

* http://localhost:8989/benchmarks/{registered_benchmark_name} - executes particular registered benchmark and its endpoints.

* http://localhost:8989/benchmarks/{registered_benchmark_name}?iterations={number_of_iterations} - executes particular registered benchmark and its endpoints 
specified number of times with aggregated reports.

## Deployment

You should run the following command once and wait for completion:
```
sudo docker-compose build
```
Then you can run everything without rebuilding
```
sudo docker-compose up
```

## Other

* [Swagger UI](https://swagger.io/) - http://localhost:8989/swagger-ui.html
* [Spring boot actuator](https://www.baeldung.com/spring-boot-actuators) - http://localhost:8989/actuator

## Built With

* [Spring Boot](https://spring.io/projects/spring-boot) - Create stand-alone Spring applications
* [Maven](https://maven.apache.org/) - Dependency Management
