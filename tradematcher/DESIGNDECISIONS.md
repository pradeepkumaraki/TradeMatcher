# Design Decisions

## Data Structures

### Order Book: `TreeMap`
The order book uses a `TreeMap<BigDecimal, LinkedList<Order>>` to manage orders.

**Key Benefits:**
-   **Natural Price Levels**: The map structure (`Price -> Queue`) is a direct representation of an order book.
-   **Price Priority**: `TreeMap` keeps prices sorted, providing fast access to the best bid/ask.
-   **Efficient Matching**: The ordered iterator is efficient for walking the book (matching an order against multiple price levels).

## Architecture

### Strategy Pattern (`MatchingStrategy`)
The matching algorithm is abstracted behind the `MatchingStrategy` interface.
-   **Benefit**: Decouples the engine from the matching logic, allowing algorithms to be swapped (e.g., Price-Time vs. Pro-Rata).
-   **Testing**: Simplifies unit testing of matching rules in isolation.

### Factory Pattern (`OrderBookFactory`)
The `OrderBookFactory` is used to create `OrderBook` instances.
-   **Benefit**: Decouples the engine from the `OrderBook`'s implementation. The backing data structure can be changed without modifying the engine.
