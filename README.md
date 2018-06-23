# N26
N26 statistics code challenge

# Requirement

The main use case for our API is to calculate real time statistic from the last 60 seconds. There will be two APIs, one of them is called every time a transaction is made. It is also the sole input of this rest API. The other one returns the statistic based of the transactions of the last 60.

# REST API endpoints 

POST /transactions

```
curl -XPOST -H "Content-Type:application/json" -d "{\"amount\":5, \"timestamp\":1529734017000}" localhost:8080/transactions
```

GET /statistics

```
curl localhost:8080/statistics
```

# Running

mvn clean install

mvn spring-boot:run

# Solution

POST transaction - store (within last minute) transactions in a ConcurrentSkipListMap - ensures order and thread safety and allows fast search concurrently.

Scheduled task that runs every second to update the statistic (stored in cache), based on transactions from the past minute.

GET statistics - retrieve statistic from cache O(1).

# Observations

PUT in the ConcurrentSkipListMap has the complexity of O(log(n)). Focused on making the GET /statistic, O(1).

As the key in the transactions map is the transaction timestamp, there cannot be 2 transactions with the same timestamp.
