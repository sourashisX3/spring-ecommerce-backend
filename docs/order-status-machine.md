# Order Status Machine

```mermaid
stateDiagram-v2
    [*] --> PENDING : Checkout initiated
    PENDING --> CONFIRMED : Payment verified
    PENDING --> CANCELLED : User cancels / Timeout

    CONFIRMED --> PROCESSING : Admin starts fulfillment
    CONFIRMED --> CANCELLED : Admin cancels

    PROCESSING --> SHIPPED : Carrier pickup scanned
    PROCESSING --> ON_HOLD : Stock issue / Admin action

    SHIPPED --> DELIVERED : Customer confirms / Auto-confirm
    SHIPPED --> PARTIALLY_DELIVERED : Split shipment

    DELIVERED --> RETURN_REQUESTED : Customer initiates return
    PARTIALLY_DELIVERED --> DELIVERED : Remaining items delivered
    PARTIALLY_DELIVERED --> RETURN_REQUESTED : Customer initiates return

    RETURN_REQUESTED --> REFUND_PENDING : Return approved
    RETURN_REQUESTED --> RETURN_REJECTED : Return rejected
    RETURN_REQUESTED --> EXCHANGE_INITIATED : Exchange approved

    REFUND_PENDING --> REFUNDED : Payment refunded
    REFUND_PENDING --> PARTIALLY_REFUNDED : Partial refund
    EXCHANGE_INITIATED --> REPLACEMENT_SHIPPED : Replacement sent

    CANCELLED --> [*]
    REFUNDED --> [*]
    RETURN_REJECTED --> DELIVERED

    note right of PENDING
        Order holds cart snapshot
        Price locked at checkout
        Inventory reserved
    end note

    note right of SHIPPED
        Delivery tracking active
        Return window starts
    end note
```

## Order Status Transitions

| From | To | Trigger | By |
|------|----|---------|----|
| PENDING | CONFIRMED | Payment success | System |
| PENDING | CANCELLED | Cancel request / timeout | User / System |
| CONFIRMED | PROCESSING | Admin starts fulfillment | Admin |
| CONFIRMED | CANCELLED | Admin cancels | Admin |
| PROCESSING | SHIPPED | Carrier pickup | Admin / System |
| PROCESSING | ON_HOLD | Stock issue | Admin |
| SHIPPED | DELIVERED | Delivery confirmation | System / User |
| SHIPPED | PARTIALLY_DELIVERED | Split delivery | System |
| DELIVERED | RETURN_REQUESTED | Customer initiates return | User |
| PARTIALLY_DELIVERED | DELIVERED | Remaining delivered | System |
| RETURN_REQUESTED | REFUND_PENDING | Admin approves refund | Admin |
| RETURN_REQUESTED | RETURN_REJECTED | Admin rejects return | Admin |
| RETURN_REQUESTED | EXCHANGE_INITIATED | Admin approves exchange | Admin |
| REFUND_PENDING | REFUNDED | Refund processed | System |
| REFUND_PENDING | PARTIALLY_REFUNDED | Partial refund | System |
| EXCHANGE_INITIATED | REPLACEMENT_SHIPPED | Replacement dispatched | Admin |

## Return Status Machine

```mermaid
stateDiagram-v2
    [*] --> PENDING : Return request created
    PENDING --> APPROVED : Admin approves
    PENDING --> REJECTED : Admin rejects
    PENDING --> CANCELLED : User cancels

    APPROVED --> ITEM_RECEIVED : Warehouse confirms receipt
    ITEM_RECEIVED --> REFUNDED : Refund processed
    ITEM_RECEIVED --> EXCHANGED : Replacement shipped
    ITEM_RECEIVED --> STORE_CREDITED : Store credit issued
```
