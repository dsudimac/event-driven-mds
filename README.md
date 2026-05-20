# event-driven-mds
Test project for MDS informatički inženjering

For building the image use the following command in the root of the project:
```
docker compose build
```
And run the application with:
```
docker compose up
```
For testing use curl command for example:
```
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "123",
    "itemId": "item-1",
    "quantity": 2
  }'
```
