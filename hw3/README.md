# HW3: Web-Server refactoring

## Objective

To gain practical experience in applying refactoring techniques

## Description

Download application from [here](https://github.com/akirakozov/software-design/tree/master/java/refactoring).

The application is a simple web server that stores information about products and their price.
The following methods are supported:
- http://localhost:8081/get-products - view all products in the database
- http://localhost:8081/add-product?name=iphone6&price=300 - add a new product
  http://localhost:8081/query?command=sum - run some query with data in the database

It is necessary to refactor this code (the logic of the methods must not change), for example:
- remove copypaste
- add a separate layer for interaction with database
- add a separate layer for creating html-response
- etc

## Instructions

- Start by adding tests (in separate commits)
- Perform each individual refactoring as a separate commit
- It's necessary to have well-formed commit history
