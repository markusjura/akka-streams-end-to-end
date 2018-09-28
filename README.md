# Akka Streams end-to-end #

This is the code repository for talk "Using Akka Streams to write Microservice that supports end-to-end backpressure" at the [NCA Live Coding Event](https://nevercodealone.de/otto/). 

## Talk description

Let's write a microservice based on Akka Streams that truly handles backpressure throughout the entire application.

Traditionally, routes built with Akka HTTP directly interact with actors implementing domain logic. One potential issue is the lack of backpressure: when the route just fires messages at the poor domain actors, these messages pile up in the mailbox faster than they can get processed. This is especially the case when the domain actors interact with a database or external services, waiting for a response.

Our microservice offers an HTTP API and executes several steps sequentially. By using Akka Streams as the processing engine, incoming HTTP calls will fail fast while a system is overloaded.

## Getting started

1. Start the HTTP server in reload mode:
    ```
    sbt "~reStart"
    ```
2. Send HTTP request to server:
    ```
    curl http://localhost:8080 \
      --request POST \
      --header "Content-Type: application/json" \
      --data '{"question": "What is your age?"}'
    ```

## License

This code is open source software licensed under the [Apache 2.0 License](http://www.apache.org/licenses/LICENSE-2.0.html).
