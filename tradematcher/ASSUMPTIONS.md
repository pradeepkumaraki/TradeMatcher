# Assumptions

## Execution Price
Execution price is determined by the **resting order**. This means the incoming order gets the price improvement.

## Order Processing
- **Strict Timestamp Ordering**: Orders are processed sequentially based on timestamp.
- **Zero Quantity**: Constructing an `Order` with `qty <= 0` throws an exception. The engine will silently ignore any processed orders that have a quantity of zero.

## Matching Logic
- **Price-Time Priority**:
  - Buy: High price, then early time.
  - Sell: Low price, then early time.
- **Matching Condition**: `Buy Price >= Sell Price`.
- **Partial Fills**: Supported. Remaining quantity stays in the book.

## README Discrepancy
The `README.md` example output contradicts the requirement "Execution price is the resting order's price".
- **Example Output**: Shows execution at the *incoming* order's price (e.g., Match 2 at $149.00).
- **Correct Logic**: Execution must be at the *resting* order's price (e.g., Match 2 at $150.00).
- **Implementation**: Follows the correct logic (Resting Price).

Additionally, the example has a typo for S3's unfilled price ($279.00 vs $2795.00).

## Concurrency
The current engine is **single-threaded**. For real-time use, this would need to be handled via synchronization or a single-threaded event loop.
