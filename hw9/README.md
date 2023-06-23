# HW9: Actors Search Aggregator

## Objective

Get hands-on experience in using actors.

## Description

It is necessary to implement a search aggregator that collects the top 5 responses at the user's request through the API 
of well-known search engines and issues them to the user. For example, making a request to Google, Yandex, Bing and 
returns 15 answers (it should be clear what the answer from a search engine). You can not use the real API, but 
implement a StubServer that will return results in a convenient format (json, xml, protobuf, etc.).

Application Architecture:
- A master-actor is created for each request, which will collect results from search engines
- Master-actor creates a child-actor for each search engine, to which he sends the original "search query"
- Master-actor sets himself a receive timeout, how long he will wait for responses from child-actors
- Child-actor makes a request to the appropriate search service and sends its result to master-actor
- Master-actor saves them when receiving each response, if he received all 3 responses or the timeout time has passed, 
  then sends the collected aggregated result for further processing
- Master-actor must die after returning the aggregated result

## Instructions

- In the stub-server, it is necessary to implement the possibility of sticking so that you can check the scenario 
  when the master-actor did not wait for answers from all search engines
- [Examples](https://github.com/akirakozov/software-design/tree/master/java/akka) from the lecture.
