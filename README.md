## Problem Overview

Design and implement a simplified order matching engine that matches buy-side orders with sell-side orders. The system should support batch processing initially, but the design must be extensible to support real-time streaming order matching in the future.

### Requirements

Build an order matching system that:

1. **Accepts orders** with the following attributes:
   - Order ID (unique identifier)
   - Instrument name (e.g., "AAPL", "GOOGL", "TSLA")
   - Side (BUY or SELL)
   - Quantity (integer)
   - Price (decimal)
   - Timestamp (when order was placed)

2. **Matches orders** based on these rules:
   - Buy and sell orders match when they are for the same instrument
   - Buy order price must be ≥ Sell order price
   - Orders are matched using **price-time priority**:
     - Best price gets priority (highest buy price, lowest sell price)
     - For same price, earlier timestamp gets priority
   - One order can be matched with multiple counter-party orders
   - Partial fills are allowed

3. **Returns match results** showing:
   - Buy order ID and sell order ID
   - Instrument name
   - Matched quantity
   - Execution price (typically the resting order's price)
   - Any remaining unfilled quantities

### Example

**Input Orders:**
```
Buy Orders:
- B1: AAPL, BUY, 100 shares @ $150.00, timestamp: 10:00:00
- B2: AAPL, BUY, 50 shares @ $151.00, timestamp: 10:00:05
- B3: GOOGL, BUY, 75 shares @ $2800.00, timestamp: 10:00:10

Sell Orders:
- S1: AAPL, SELL, 80 shares @ $150.50, timestamp: 10:00:02
- S2: AAPL, SELL, 30 shares @ $149.00, timestamp: 10:00:07
- S3: GOOGL, SELL, 100 shares @ $2795.00, timestamp: 10:00:12
```

**Expected Output:**
```
Matches:
1. Instrument: AAPL, Quantity: 50, Price: $150.50
2. Instrument: AAPL, Quantity: 30, Price: $149.00
3. Instrument: GOOGL, Quantity: 75, Price: $2795.00


Unfilled Orders:
1. B1: Buy, Instrument: AAPL, Quantity: 70, Price: $150.00
2. S1: Sell, Instrument: AAPL, Quantity: 30 shares @ $150.50
3. S3: Sell, Instrument: GOOGL, Quantity 25, Price: $279.00


### Implementation Requirements

1. **Define data structures** for:
   - Order
   - Match/Fill
   - Order book per instrument

2. **Implement the matching algorithm**:
   - Core matching logic
   - Price-time priority ordering
   - Partial fill handling

3. **Write unit tests** covering:
   - Full fills (one-to-one matching)
   - Partial fills (one order matched against multiple)
   - No matches (price mismatch)
   - Multiple instruments

4. **Consider edge cases**:
   - Empty order books
   - Zero quantity orders
   - Identical prices and timestamps
   - Very large quantity differences
```

### Assumptions:
```
In case you are not clear on some of the requirements/statements, Feel free to make your own assumptions and do state those assumptions in the commit - preferably in a separate file, or a separate section 
```
