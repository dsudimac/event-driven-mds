# event-driven-mds
Test project for MDS informatički inženjering

## Description
Simple event driven system where Order Api Service receives orders on REST endpoint and sends messages asynchronously 
through Kafka messaging broker to the Inventory Service to update stock levels.

## Technologies
Maven mono-repo with two services: Order Api Service and Inventory Processing Service (the parent pom is used as aggregator parent).
Both services are built using Spring Boot and Spring Kafka.  

Order Service consists of two modules order-api-service-model which is used for sharing the model 
between the services and order-api-service-app which contains the REST API implementation. The model is shared as a 
dependency (sharing model implementation is a bad practice but for simplicity...).
Docker is used for containerization and docker compose for orchestration of the services and Kafka broker.

Orders are saved in ConcurrentHashMap for simplicity. DTOs, messaging events, and Entities are mapped using MapStruct. 
Lombok is used for reducing boilerplate code. Validation is done using Spring Validation only in web layer.

There is also simple implementation of traceId propagation using Kafka headers.

### Libraries
- Spring Boot
- MapStruct
- Lombok
- Spring Kafka
- Spring Validation
- Spring Web

## Messaging
Order Api Service on start creates three topics if not exists (inventory.orders.v{version}, 
inventory.orders.v{version}-dlt and inventory.orders.invalid.v{version}-). inventory.orders.invalid topic is poison pill 
messages. Order Api Service produces OrderCreatedEvent which is the only class in shared model.

Order Api Service publisher is configured for retry-ing and producer side idempotency. The publishing works in 
fire-and-forget mode, so the web layer returns 202 code. After retrying if the message is not sent successfully, 
the error is logged (better solution would be outbox pattern).

Inventory Service consumer is configured with retry and error handling. If the message processing fails after retries, 
the message is sent to DLT topic. If the message is invalid (fails deserialization) it is sent to poison pill topic.


## Building and running the application
For building the image use the following command in the root of the project (DockerDesktop should be running):
```
docker compose build
```
And run the application with:
```
docker compose up
```
The Order Api Service will be available on http://localhost:8080 and health endpoint on 
http://localhost:8080/actuator/health.

## Testing the application
For testing the inventory store contains two items with the following stock levels:
(item-1, 10 units) and (item-2, 5 units)

Use curl command to create order for example:
```
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "123",
    "itemId": "item-1",
    "quantity": 10
  }'
```
In the logs you will find: ```Order 123 reserved.```
If you repeat the same command you will get two records in logs: 
```
Order 123 rejected.
Order with ID 123 has already been processed
```
If you create order with quantity more than available stock, you will get:
    
```
Order 123 rejected.
"No item with ID item-1 in stock or insufficient quantity"
```
