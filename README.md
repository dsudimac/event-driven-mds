# event-driven-mds
Test project for MDS informatički inženjering

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
