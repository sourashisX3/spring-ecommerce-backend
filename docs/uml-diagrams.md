# UML Diagrams

## 1. Architecture Component Diagram

```mermaid
graph TB
    subgraph "Client Layer"
        WEB["Web App (KMP)"]
        MOBILE["Android / iOS App (KMP)"]
    end

    subgraph "API Gateway / REST"
        REST["REST Controllers<br/>/api/**"]
        WS["WebSocket STOMP<br/>/ws"]
    end

    subgraph "Authentication"
        JWT["JWT Filter<br/>OncePerRequestFilter"]
        AUTH["AuthService<br/>Login / Register / Refresh"]
        OTP["OTP Service"]
    end

    subgraph "Business Modules"
        PRODUCT["Product Module<br/>Product, Category, Brand, Tag<br/>Variant, Image, Review"]
        ORDER["Order Module<br/>Order, OrderItem<br/>OrderStatus, History"]
        PAYMENT["Payment Module<br/>Payment, Refund<br/>Wallet, Gateway"]
        USER["User Module<br/>User, Role, Permission"]
        CART["Cart / Wishlist"]
        SHIPPING["Shipping Module<br/>Delivery, Address, Carrier"]
        RETURNS["Returns Module<br/>ReturnRequest, ReturnItem"]
    end

    subgraph "Notification"
        EVENT["Event Bus<br/>Spring Events"]
        NOTIF["NotificationService<br/>Persist + Push"]
        NOTIF_WS["WebSocket Push<br/>/user/queue/notifications"]
    end

    subgraph "Chat / Support"
        CHAT_BOT["ChatBotService<br/>Q&A Triage"]
        CHAT_SVC["ChatService<br/>Room Management"]
        CHAT_WS["WebSocket<br/>/topic/chat/room/{id}"]
    end

    subgraph "Data Layer"
        MYSQL[(MySQL<br/>All Tables)]
        REDIS[(Redis<br/>Future)]
    end

    subgraph "Authorization"
        AOP["AuthorizationAspect<br/>@RequiresPermission"]
    end

    WEB --> REST
    MOBILE --> REST
    WEB --> WS
    MOBILE --> WS
    REST --> JWT
    REST --> AUTH
    REST --> PRODUCT
    REST --> ORDER
    REST --> PAYMENT
    REST --> USER
    REST --> CART
    REST --> SHIPPING
    REST --> RETURNS
    PRODUCT --> MYSQL
    ORDER --> MYSQL
    PAYMENT --> MYSQL
    USER --> MYSQL
    CART --> MYSQL
    SHIPPING --> MYSQL
    RETURNS --> MYSQL
    AUTH --> USER
    AOP --> USER

    ORDER -->|publishEvent| EVENT
    PAYMENT -->|publishEvent| EVENT
    SHIPPING -->|publishEvent| EVENT
    AUTH -->|publishEvent| EVENT
    EVENT -->|@EventListener| NOTIF
    NOTIF --> NOTIF_WS

    CHAT_SVC --> CHAT_WS
    CHAT_BOT --> CHAT_SVC
    CHAT_SVC --> MYSQL
    WS --> CHAT_SVC
    WS --> CHAT_BOT
```

## 2. Notification System — Sequence Diagram

```mermaid
sequenceDiagram
    actor User
    participant Controller as REST Controller
    participant Service as OrderService
    participant EventBus as ApplicationEventPublisher
    participant NotifSvc as NotificationService
    participant DB as MySQL
    participant WS as WebSocket / STOMP
    participant Client as Client App (KMP)

    User->>Controller: POST /api/orders/checkout
    Controller->>Service: createOrder(request)
    Service->>Service: save order to DB
    Service->>EventBus: publishEvent(OrderCreatedEvent)
    EventBus-->>NotifSvc: @EventListener (async)
    NotifSvc->>NotifSvc: build Notification entity

    Note over NotifSvc: {type: "ORDER_CONFIRMED",<br/>title: "Order Confirmed",<br/>body: "Your order ORD-AB12 has been placed",<br/>deepLink: "ecommerce://orders/{uuid}"}

    NotifSvc->>DB: INSERT into notifications
    NotifSvc->>WS: convertAndSendToUser(userId, "/queue/notifications", dto)
    WS-->>Client: STOMP MESSAGE
    Service-->>Controller: return Order
    Controller-->>User: 201 Created + Order data

    Note over Client: User taps notification
    Note over Client: deepLink: ecommerce://orders/{uuid}
    Note over Client: KMP App navigates to OrderDetailScreen
```

## 3. Admin Notification — Sequence Diagram

```mermaid
sequenceDiagram
    actor NewUser
    participant AuthSvc as AuthService
    participant EventBus as ApplicationEventPublisher
    participant NotifSvc as NotificationService
    participant WS as WebSocket / STOMP
    actor Admin

    NewUser->>AuthSvc: POST /api/auth/register
    AuthSvc->>AuthSvc: create user in DB
    AuthSvc->>EventBus: publishEvent(UserRegisteredEvent(userId))
    EventBus-->>NotifSvc: @EventListener (async)
    NotifSvc->>NotifSvc: build Notification
    NotifSvc->>WS: convertAndSend("/topic/admin/notifications", dto)
    WS-->>Admin: STOMP MESSAGE (all connected admins)
    AuthSvc-->>NewUser: 201 Created
```

## 4. Chat Bot Triage — Sequence Diagram

```mermaid
sequenceDiagram
    actor User
    participant ChatBot as ChatBotService
    participant ChatSvc as ChatService
    participant DB as MySQL
    participant WS as WebSocket / STOMP
    participant Agent as Support Agent

    User->>WS: CONNECT (JWT token)
    WS-->>User: CONNECTED

    User->>WS: SEND /app/chat/create
    WS->>ChatSvc: createRoom(userId)
    ChatSvc->>ChatSvc: room.status = BOT_ACTIVE
    ChatSvc->>WS: subscribe user to /topic/chat/room/{id}

    ChatSvc->>ChatBot: getRootQuestion()
    ChatBot->>DB: SELECT * FROM chat_bot_questions WHERE parent_id IS NULL
    ChatBot->>WS: send(roomId, botMessage + quickReply options)

    WS-->>User: Bot: "Hello! How can I help you?" [Order Issue, Payment Issue, Return, Account, Other]

    User->>WS: SEND /app/chat/room/{id}/send {content: "Order Issue"}
    WS->>ChatSvc: processMessage(roomId, userId, "Order Issue")
    ChatSvc->>ChatBot: getNextQuestion("order_issue")
    ChatBot->>DB: SELECT * FROM chat_bot_questions WHERE parent_id = ?
    ChatBot->>WS: send(roomId, botResponse + nextOptions)

    WS-->>User: Bot: "What's the problem?" [Not Received, Wrong Item, Damaged, Cancel, Other]

    User->>WS: SEND /app/chat/room/{id}/send {content: "Cancel Order"}
    WS->>ChatSvc: processMessage(roomId, userId, "Cancel Order")
    ChatBot->>ChatBot: botResponse = "You can cancel within 30 mins..."
    ChatBot->>ChatBot: check if isEscalationPoint
    ChatBot->>WS: send(roomId, botResponse + "Did this help?" [Yes, Connect to Agent])

    WS-->>User: Bot: "You can cancel within 30 mins..." [Yes, thanks!, Connect me to an agent]

    User->>WS: SEND /app/chat/room/{id}/send {content: "Connect to Agent"}
    ChatSvc->>ChatSvc: room.status = AWAITING_AGENT
    ChatSvc->>DB: UPDATE chat_rooms SET status = 'AWAITING_AGENT'
    Note over ChatSvc: Notification sent to all ADMIN agents via WebSocket

    Agent->>WS: SEND /app/chat/room/{id}/assign
    ChatSvc->>ChatSvc: room.agentId = agentId
    ChatSvc->>ChatSvc: room.status = ACTIVE
    ChatSvc->>WS: send(roomId, systemMsg: "Agent XYZ has joined")
    ChatSvc->>WS: subscribe agent to /topic/chat/room/{id}

    WS-->>User: System: "Agent XYZ has joined the chat"
    WS-->>Agent: System: "Agent XYZ has joined the chat"

    Note over User,Agent: Real-time bidirectional chat begins
    User->>WS: SEND /app/chat/room/{id}/send {content: "I need help"}
    WS->>ChatSvc: saveMessage(roomId, USER, userId, content)
    ChatSvc->>WS: broadcast to /topic/chat/room/{id}
    WS-->>Agent: Agent receives message
    Agent->>WS: SEND /app/chat/room/{id}/send {content: "I can help!"}
    WS-->>User: User receives message
```

## 5. Class Diagram — Core Domain

```mermaid
classDiagram
    class User {
        -Long id
        -String uuid
        -String firstName
        -String lastName
        -String email
        -String dialCode
        -String phoneNumber
        -String password
        -String profilePictureUrl
        -UserAddress address
        -boolean isActive
        -boolean isEmailVerified
        -boolean isPhoneVerified
        -Role role
        -List~UserPermission~ userPermissions
        -Instant createdAt
        -Instant updatedAt
    }

    class Role {
        -Long id
        -String roleName
        -String roleDescription
        -Set~Permission~ permissions
        -Instant createdAt
        -Instant updatedAt
    }

    class Permission {
        -Long id
        -String permissionName
        -String permissionDescription
        -Instant createdAt
        -Instant updatedAt
    }

    User "1" --> "*" UserPermission : has
    Role "1" --> "*" UserPermission : grants
    Role "*" --> "*" Permission : via_role_permissions
    User "*" --> "1" Role : has
```

## 6. Class Diagram — Notification Module

```mermaid
classDiagram
    class Notification {
        -Long id
        -String uuid
        -User user
        -String type
        -String title
        -String body
        -String deepLink
        -boolean isRead
        -Instant createdAt
        -Instant readAt
    }

    class NotificationService {
        +sendNotification(userId, type, title, body, deepLink)
        +getUserNotifications(userId, pageable)
        +markAsRead(notificationId)
        +markAllAsRead(userId)
        +getUnreadCount(userId)
        -buildAndPush(userId, type, title, body, deepLink)
    }

    class NotificationController {
        +GET /api/notifications
        +GET /api/notifications/unread-count
        +PATCH /api/notifications/{id}/read
        +PATCH /api/notifications/read-all
    }

    class WebSocketConfig {
        +configureMessageBroker()
        +registerStompEndpoints()
        +configureClientInboundChannel()
    }

    class JwtChannelInterceptor {
        +preSend(message, channel)
        -extractToken(message)
        -authenticate(token)
    }

    NotificationController --> NotificationService
    NotificationService --> Notification
    NotificationService --> WebSocketConfig : uses simpMessagingTemplate
    Notification "1" --> "1" User : targets
```

## 7. Class Diagram — Chat Module

```mermaid
classDiagram
    class ChatRoom {
        -Long id
        -String uuid
        -User user
        -User agent
        -String status
        -String topic
        -Instant createdAt
        -Instant assignedAt
        -Instant closedAt
    }

    class ChatMessage {
        -Long id
        -String uuid
        -ChatRoom room
        -String senderType
        -User sender
        -String content
        -String messageType
        -String metadata
        -Instant createdAt
        -Instant readAt
    }

    class ChatBotQuestion {
        -Long id
        -ChatBotQuestion parent
        -String questionKey
        -String questionText
        -String options
        -String botResponse
        -boolean isEscalationPoint
        -int sortOrder
        -boolean isActive
    }

    class ChatService {
        +createRoom(userId)
        +assignAgent(roomId, agentId)
        +closeRoom(roomId)
        +sendMessage(roomId, senderType, senderId, content, messageType, metadata)
        +getRoomMessages(roomId, pageable)
        +getUserRooms(userId)
        +getOpenRooms()
    }

    class ChatBotService {
        +getRootQuestion()
        +getNextQuestion(currentKey, selectedOption)
        +processUserSelection(questionKey, optionValue)
        +isEscalationRequired(questionKey)
    }

    class ChatController {
        +POST /api/chat/rooms
        +GET /api/chat/rooms
        +GET /api/chat/rooms/{uuid}/messages
        +PATCH /api/chat/rooms/{uuid}/assign
        +PATCH /api/chat/rooms/{uuid}/close
    }

    class ChatWebSocketController {
        +@MessageMapping /chat/create
        +@MessageMapping /chat/room/{roomUuid}/send
        +@MessageMapping /chat/room/{roomUuid}/typing
    }

    ChatRoom "1" --> "*" ChatMessage : contains
    ChatRoom "*" --> "1" User : customer
    ChatRoom "*" --> "0..1" User : agent
    ChatBotQuestion "1" --> "0..*" ChatBotQuestion : parent_children
    ChatController --> ChatService
    ChatWebSocketController --> ChatService
    ChatWebSocketController --> ChatBotService
    ChatService --> ChatRoom
    ChatService --> ChatMessage
    ChatBotService --> ChatBotQuestion
```

## 8. Event Model

```mermaid
classDiagram
    class OrderCreatedEvent {
        +Object source
        +Long userId
        +String orderUuid
    }

    class OrderStatusChangedEvent {
        +Object source
        +Long userId
        +String orderUuid
        +String newStatus
    }

    class PaymentProcessedEvent {
        +Object source
        +Long userId
        +String paymentUuid
        +String orderUuid
    }

    class PaymentFailedEvent {
        +Object source
        +Long userId
        +String paymentUuid
        +String orderUuid
    }

    class DeliveryStatusChangedEvent {
        +Object source
        +Long userId
        +String deliveryUuid
        +String newStatus
    }

    class UserRegisteredEvent {
        +Object source
        +Long userId
    }

    NotificationService ..> OrderCreatedEvent : @EventListener
    NotificationService ..> OrderStatusChangedEvent : @EventListener
    NotificationService ..> PaymentProcessedEvent : @EventListener
    NotificationService ..> PaymentFailedEvent : @EventListener
    NotificationService ..> DeliveryStatusChangedEvent : @EventListener
    NotificationService ..> UserRegisteredEvent : @EventListener
```
