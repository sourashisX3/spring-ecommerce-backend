package com.example.ecommerce_backend.core.config;

import com.example.ecommerce_backend.modules.currency.entity.Currency;
import com.example.ecommerce_backend.modules.currency.repository.CurrencyRepository;
import com.example.ecommerce_backend.modules.discount.entity.DiscountType;
import com.example.ecommerce_backend.modules.discount.repository.DiscountTypeRepository;
import com.example.ecommerce_backend.modules.wallet.entity.WalletTransactionType;
import com.example.ecommerce_backend.modules.wallet.repository.WalletTransactionTypeRepository;
import com.example.ecommerce_backend.modules.order.entity.OrderStatus;
import com.example.ecommerce_backend.modules.order.entity.OrderStatusTransition;
import com.example.ecommerce_backend.modules.order.repository.OrderStatusRepository;
import com.example.ecommerce_backend.modules.order.repository.OrderStatusTransitionRepository;
import com.example.ecommerce_backend.modules.payment.entity.PaymentGateway;
import com.example.ecommerce_backend.modules.payment.entity.PaymentStatus;
import com.example.ecommerce_backend.modules.payment.entity.RefundStatus;
import com.example.ecommerce_backend.modules.payment.repository.PaymentGatewayRepository;
import com.example.ecommerce_backend.modules.payment.repository.PaymentStatusRepository;
import com.example.ecommerce_backend.modules.payment.repository.RefundStatusRepository;
import com.example.ecommerce_backend.modules.permission.entity.Permission;
import com.example.ecommerce_backend.modules.permission.repository.PermissionsRepository;
import com.example.ecommerce_backend.modules.returns.entity.ReturnCondition;
import com.example.ecommerce_backend.modules.returns.entity.ReturnStatus;
import com.example.ecommerce_backend.modules.returns.entity.ReturnType;
import com.example.ecommerce_backend.modules.returns.repository.ReturnConditionRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnStatusRepository;
import com.example.ecommerce_backend.modules.returns.repository.ReturnTypeRepository;
import com.example.ecommerce_backend.modules.role.entity.Role;
import com.example.ecommerce_backend.modules.role.repository.RolesRepository;
import com.example.ecommerce_backend.modules.shipping.entity.DeliveryStatus;
import com.example.ecommerce_backend.modules.shipping.entity.ShippingCarrier;
import com.example.ecommerce_backend.modules.shipping.repository.DeliveryStatusRepository;
import com.example.ecommerce_backend.modules.shipping.repository.ShippingCarrierRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Order(1)
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final PermissionsRepository permissionsRepository;
    private final RolesRepository rolesRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderStatusTransitionRepository orderStatusTransitionRepository;
    private final DiscountTypeRepository discountTypeRepository;
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final ShippingCarrierRepository shippingCarrierRepository;
    private final ReturnTypeRepository returnTypeRepository;
    private final ReturnConditionRepository returnConditionRepository;
    private final PaymentStatusRepository paymentStatusRepository;
    private final RefundStatusRepository refundStatusRepository;
    private final DeliveryStatusRepository deliveryStatusRepository;
    private final ReturnStatusRepository returnStatusRepository;
    private final WalletTransactionTypeRepository walletTransactionTypeRepository;
    private final CurrencyRepository currencyRepository;

    public DataSeeder(PermissionsRepository permissionsRepository, RolesRepository rolesRepository,
                      OrderStatusRepository orderStatusRepository,
                      OrderStatusTransitionRepository orderStatusTransitionRepository,
                      DiscountTypeRepository discountTypeRepository,
                      PaymentGatewayRepository paymentGatewayRepository,
                      ShippingCarrierRepository shippingCarrierRepository,
                       ReturnTypeRepository returnTypeRepository,
                       ReturnConditionRepository returnConditionRepository,
                       PaymentStatusRepository paymentStatusRepository,
                       RefundStatusRepository refundStatusRepository,
                       DeliveryStatusRepository deliveryStatusRepository,
                       ReturnStatusRepository returnStatusRepository,
                       WalletTransactionTypeRepository walletTransactionTypeRepository,
                       CurrencyRepository currencyRepository) {
        this.permissionsRepository = permissionsRepository;
        this.rolesRepository = rolesRepository;
        this.orderStatusRepository = orderStatusRepository;
        this.orderStatusTransitionRepository = orderStatusTransitionRepository;
        this.discountTypeRepository = discountTypeRepository;
        this.paymentGatewayRepository = paymentGatewayRepository;
        this.shippingCarrierRepository = shippingCarrierRepository;
        this.returnTypeRepository = returnTypeRepository;
        this.returnConditionRepository = returnConditionRepository;
        this.paymentStatusRepository = paymentStatusRepository;
        this.refundStatusRepository = refundStatusRepository;
        this.deliveryStatusRepository = deliveryStatusRepository;
        this.returnStatusRepository = returnStatusRepository;
        this.walletTransactionTypeRepository = walletTransactionTypeRepository;
        this.currencyRepository = currencyRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedPermissions();
        seedRoles();
        seedOrderStatuses();
        seedDiscountTypes();
        seedPaymentGateways();
        seedShippingCarriers();
        seedReturnTypes();
        seedReturnConditions();
        seedPaymentStatuses();
        seedRefundStatuses();
        seedDeliveryStatuses();
        seedReturnStatuses();
        seedWalletTransactionTypes();
        seedCurrencies();
    }

    private void seedPermissions() {
        Map<String, String> permissions = new LinkedHashMap<>();
        permissions.put("*:*", "Full access to all resources");
        permissions.put("user:read", "View user profiles");
        permissions.put("user:write", "Create, update or delete users");
        permissions.put("role:read", "View roles and their permissions");
        permissions.put("role:write", "Create or delete roles");
        permissions.put("permission:read", "View permissions");
        permissions.put("permission:write", "Create or delete permissions");
        permissions.put("user_permission:read", "View user-level permission overrides");
        permissions.put("user_permission:write", "Assign or remove user-level permission overrides");
        permissions.put("product:read", "View products");
        permissions.put("product:write", "Create, update or delete products");
        permissions.put("category:read", "View categories");
        permissions.put("category:write", "Create, update or delete categories");
        permissions.put("brand:read", "View brands");
        permissions.put("brand:write", "Create, update or delete brands");
        permissions.put("tag:read", "View tags");
        permissions.put("tag:write", "Create, update or delete tags");
        permissions.put("order:read", "View orders");
        permissions.put("order:write", "Create, update or delete orders");
        permissions.put("order:update_status", "Update order status");
        permissions.put("coupon:read", "View coupons");
        permissions.put("coupon:write", "Create, update or delete coupons");
        permissions.put("offer:read", "View offers");
        permissions.put("offer:write", "Create, update or delete offers");
        permissions.put("discount:read", "View discounts");
        permissions.put("discount:write", "Create, update or delete discounts");
        permissions.put("payment:read", "View payments");
        permissions.put("payment:write", "Process payments and refunds");
        permissions.put("shipping:read", "View shipping carriers");
        permissions.put("shipping:write", "Manage shipping carriers");
        permissions.put("delivery:read", "View deliveries");
        permissions.put("delivery:write", "Update deliveries");
        permissions.put("return:read", "View return requests");
        permissions.put("return:write", "Manage returns, types, and conditions");
        permissions.put("wallet:read", "View wallet");
        permissions.put("wallet:write", "Manage wallet credits/debits");
        permissions.put("currency:read", "View currencies");
        permissions.put("currency:write", "Manage currencies");
        permissions.put("chatbot:read", "View chat bot questions");
        permissions.put("chatbot:write", "Manage chat bot questions");
        permissions.put("banner:read", "View banners");
        permissions.put("banner:write", "Create, update or delete banners");

        for (Map.Entry<String, String> entry : permissions.entrySet()) {
            permissionsRepository.findByPermissionName(entry.getKey())
                    .orElseGet(() -> {
                        log.info("Seeding permission: {}", entry.getKey());
                        return permissionsRepository.save(
                                Permission.builder()
                                        .permissionName(entry.getKey())
                                        .permissionDescription(entry.getValue())
                                        .build()
                        );
                    });
        }
    }

    private void seedOrderStatuses() {
        Map<String, String> statuses = new LinkedHashMap<>();
        statuses.put("PENDING", "Pending");
        statuses.put("CONFIRMED", "Confirmed");
        statuses.put("PROCESSING", "Processing");
        statuses.put("SHIPPED", "Shipped");
        statuses.put("DELIVERED", "Delivered");
        statuses.put("CANCELLED", "Cancelled");
        statuses.put("RETURN_REQUESTED", "Return Requested");
        statuses.put("REFUNDED", "Refunded");

        List<String> orderedCodes = List.of("PENDING", "CONFIRMED", "PROCESSING", "SHIPPED",
                "DELIVERED", "CANCELLED", "RETURN_REQUESTED", "REFUNDED");

        Map<String, OrderStatus> saved = new LinkedHashMap<>();
        int idx = 0;
        for (String code : orderedCodes) {
            String name = statuses.get(code);
            if (orderStatusRepository.findByCode(code).isEmpty()) {
                log.info("Seeding order status: {}", code);
                OrderStatus status = orderStatusRepository.save(
                        OrderStatus.builder()
                                .code(code)
                                .name(name)
                                .sortOrder(idx)
                                .isActive(true)
                                .build()
                );
                saved.put(code, status);
            } else {
                saved.put(code, orderStatusRepository.findByCode(code).get());
            }
            idx++;
        }

        Map<String[], String> transitions = new LinkedHashMap<>();
        transitions.put(new String[]{"PENDING", "CONFIRMED"}, "admin");
        transitions.put(new String[]{"PENDING", "CANCELLED"}, "admin");
        transitions.put(new String[]{"PENDING", "CANCELLED"}, "user");
        transitions.put(new String[]{"CONFIRMED", "PROCESSING"}, "admin");
        transitions.put(new String[]{"CONFIRMED", "CANCELLED"}, "admin");
        transitions.put(new String[]{"PROCESSING", "SHIPPED"}, "admin");
        transitions.put(new String[]{"PROCESSING", "CANCELLED"}, "admin");
        transitions.put(new String[]{"SHIPPED", "DELIVERED"}, "admin");
        transitions.put(new String[]{"DELIVERED", "RETURN_REQUESTED"}, "user");
        transitions.put(new String[]{"RETURN_REQUESTED", "REFUNDED"}, "admin");

        Role adminRole = rolesRepository.findByRoleName("ADMIN").orElse(null);
        Role userRole = rolesRepository.findByRoleName("USER").orElse(null);

        for (Map.Entry<String[], String> entry : transitions.entrySet()) {
            String[] codes = entry.getKey();
            String roleKey = entry.getValue();
            OrderStatus from = saved.get(codes[0]);
            OrderStatus to = saved.get(codes[1]);
            if (from == null || to == null) continue;

            String roleName = "admin".equals(roleKey) ? "ADMIN" : "USER";
            Role allowedByRole = "admin".equals(roleKey) ? adminRole : userRole;
            if (allowedByRole == null) continue;

            if (orderStatusTransitionRepository
                    .findByFromStatus_CodeAndToStatus_CodeAndAllowedBy_RoleName(codes[0], codes[1], roleName)
                    .isEmpty()) {
                log.info("Seeding order status transition: {} -> {} ({})", codes[0], codes[1], roleName);
                orderStatusTransitionRepository.save(
                        OrderStatusTransition.builder()
                                .fromStatus(from)
                                .toStatus(to)
                                .allowedBy(allowedByRole)
                                .build()
                );
            }
        }
    }

    private void seedRoles() {
        Permission allPermission = perm("*:*");
        ensureRoleHasPermissions("SUPER_ADMIN", "Super admin with full access", Set.of(allPermission));

        ensureRoleHasPermissions("USER", "Default user role", Set.of());

        Set<Permission> adminPermissions = Set.of(
                perm("product:read"), perm("product:write"),
                perm("category:read"), perm("category:write"),
                perm("brand:read"), perm("brand:write"),
                perm("tag:read"), perm("tag:write"),
                perm("order:read"), perm("order:write"), perm("order:update_status"),
                perm("payment:read"), perm("payment:write"),
                perm("shipping:read"), perm("shipping:write"),
                perm("delivery:read"), perm("delivery:write"),
                perm("return:read"), perm("return:write"),
                perm("wallet:read"), perm("wallet:write"),
                perm("currency:read"), perm("currency:write"),
                perm("chatbot:read"), perm("chatbot:write"),
                perm("user:read"), perm("user:write"),
                perm("coupon:read"), perm("coupon:write"),
                perm("discount:read"), perm("discount:write"),
                perm("offer:read"), perm("offer:write"),
                perm("banner:read"), perm("banner:write"),
                perm("role:read"), perm("permission:read"), perm("user_permission:read")
        );
        ensureRoleHasPermissions("ADMIN", "Administrator with moderate access", adminPermissions);
    }

    private Permission perm(String name) {
        return permissionsRepository.findByPermissionName(name)
                .orElseThrow(() -> new RuntimeException(name + " permission not found after seeding"));
    }

    private void ensureRoleHasPermissions(String roleName, String description, Set<Permission> required) {
        Role role = rolesRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    log.info("Seeding {} role", roleName);
                    return rolesRepository.save(
                            Role.builder()
                                    .roleName(roleName)
                                    .roleDescription(description)
                                    .permissions(Set.of())
                                    .build()
                    );
                });
        Set<Permission> existing = role.getPermissions() != null ? role.getPermissions() : Set.of();
        Set<Permission> merged = new HashSet<>(existing);
        merged.addAll(required);
        if (merged.size() != existing.size()) {
            log.info("Updating permissions for {} role ({} -> {})", roleName, existing.size(), merged.size());
            role.setPermissions(merged);
            rolesRepository.save(role);
        }
    }

    private void seedDiscountTypes() {
        Map<String, String> types = new LinkedHashMap<>();
        types.put("PERCENTAGE", "Percentage");
        types.put("FIXED_AMOUNT", "Fixed Amount");

        for (Map.Entry<String, String> entry : types.entrySet()) {
            if (discountTypeRepository.findByCode(entry.getKey()).isEmpty()) {
                log.info("Seeding discount type: {}", entry.getKey());
                discountTypeRepository.save(
                        DiscountType.builder()
                                .code(entry.getKey())
                                .name(entry.getValue())
                                .computation(entry.getKey())
                                .isActive(true)
                                .build()
                );
            }
        }
    }

    private void seedPaymentGateways() {
        Map<String, String> gateways = new LinkedHashMap<>();
        gateways.put("MOCK", "Mock Payment Gateway");
        gateways.put("STRIPE", "Stripe");
        gateways.put("PAYPAL", "PayPal");

        for (Map.Entry<String, String> entry : gateways.entrySet()) {
            if (paymentGatewayRepository.findByCode(entry.getKey()).isEmpty()) {
                log.info("Seeding payment gateway: {}", entry.getKey());
                paymentGatewayRepository.save(
                        PaymentGateway.builder()
                                .code(entry.getKey())
                                .name(entry.getValue())
                                .isActive(true)
                                .build()
                );
            }
        }
    }

    private void seedShippingCarriers() {
        Map<String, String> carriers = new LinkedHashMap<>();
        carriers.put("FEDEX", "FedEx");
        carriers.put("UPS", "UPS");
        carriers.put("DHL", "DHL");

        for (Map.Entry<String, String> entry : carriers.entrySet()) {
            if (shippingCarrierRepository.findByCode(entry.getKey()).isEmpty()) {
                log.info("Seeding shipping carrier: {}", entry.getKey());
                shippingCarrierRepository.save(
                        ShippingCarrier.builder()
                                .code(entry.getKey())
                                .name(entry.getValue())
                                .isActive(true)
                                .build()
                );
            }
        }
    }

    private void seedReturnTypes() {
        Map<String, String> types = new LinkedHashMap<>();
        types.put("REFUND", "Refund to original payment method");
        types.put("EXCHANGE", "Exchange for same or different product");
        types.put("STORE_CREDIT", "Credit to store wallet");

        for (Map.Entry<String, String> entry : types.entrySet()) {
            if (returnTypeRepository.findByCode(entry.getKey()).isEmpty()) {
                log.info("Seeding return type: {}", entry.getKey());
                returnTypeRepository.save(
                        ReturnType.builder()
                                .code(entry.getKey())
                                .name(entry.getValue())
                                .isActive(true)
                                .build()
                );
            }
        }
    }

    private void seedReturnConditions() {
        Map<String, String> conditions = new LinkedHashMap<>();
        conditions.put("DAMAGED", "Product is damaged");
        conditions.put("DEFECTIVE", "Product is defective");
        conditions.put("NOT_AS_DESCRIBED", "Product not as described");

        for (Map.Entry<String, String> entry : conditions.entrySet()) {
            if (returnConditionRepository.findByCode(entry.getKey()).isEmpty()) {
                log.info("Seeding return condition: {}", entry.getKey());
                returnConditionRepository.save(
                        ReturnCondition.builder()
                                .code(entry.getKey())
                                .name(entry.getValue())
                                .isActive(true)
                                .build()
                );
            }
        }
    }

    private void seedPaymentStatuses() {
        Map<String, String> statuses = new LinkedHashMap<>();
        statuses.put("PENDING", "Pending");
        statuses.put("COMPLETED", "Completed");
        statuses.put("FAILED", "Failed");
        statuses.put("PARTIALLY_REFUNDED", "Partially Refunded");
        statuses.put("REFUNDED", "Refunded");

        int idx = 0;
        for (Map.Entry<String, String> entry : statuses.entrySet()) {
            if (paymentStatusRepository.findByCode(entry.getKey()).isEmpty()) {
                log.info("Seeding payment status: {}", entry.getKey());
                paymentStatusRepository.save(
                        PaymentStatus.builder()
                                .code(entry.getKey())
                                .name(entry.getValue())
                                .sortOrder(idx)
                                .isActive(true)
                                .build()
                );
            }
            idx++;
        }
    }

    private void seedRefundStatuses() {
        Map<String, String> statuses = new LinkedHashMap<>();
        statuses.put("PENDING", "Pending");
        statuses.put("COMPLETED", "Completed");
        statuses.put("FAILED", "Failed");

        int idx = 0;
        for (Map.Entry<String, String> entry : statuses.entrySet()) {
            if (refundStatusRepository.findByCode(entry.getKey()).isEmpty()) {
                log.info("Seeding refund status: {}", entry.getKey());
                refundStatusRepository.save(
                        RefundStatus.builder()
                                .code(entry.getKey())
                                .name(entry.getValue())
                                .sortOrder(idx)
                                .isActive(true)
                                .build()
                );
            }
            idx++;
        }
    }

    private void seedDeliveryStatuses() {
        Map<String, String> statuses = new LinkedHashMap<>();
        statuses.put("PENDING", "Pending");
        statuses.put("SHIPPED", "Shipped");
        statuses.put("DELIVERED", "Delivered");
        statuses.put("CANCELLED", "Cancelled");

        int idx = 0;
        for (Map.Entry<String, String> entry : statuses.entrySet()) {
            if (deliveryStatusRepository.findByCode(entry.getKey()).isEmpty()) {
                log.info("Seeding delivery status: {}", entry.getKey());
                deliveryStatusRepository.save(
                        DeliveryStatus.builder()
                                .code(entry.getKey())
                                .name(entry.getValue())
                                .sortOrder(idx)
                                .isActive(true)
                                .build()
                );
            }
            idx++;
        }
    }

    private void seedReturnStatuses() {
        Map<String, String> statuses = new LinkedHashMap<>();
        statuses.put("PENDING", "Pending");
        statuses.put("APPROVED", "Approved");
        statuses.put("REJECTED", "Rejected");
        statuses.put("CLOSED", "Closed");

        int idx = 0;
        for (Map.Entry<String, String> entry : statuses.entrySet()) {
            if (returnStatusRepository.findByCode(entry.getKey()).isEmpty()) {
                log.info("Seeding return status: {}", entry.getKey());
                returnStatusRepository.save(
                        ReturnStatus.builder()
                                .code(entry.getKey())
                                .name(entry.getValue())
                                .sortOrder(idx)
                                .isActive(true)
                                .build()
                );
            }
            idx++;
        }
    }

    private void seedWalletTransactionTypes() {
        Map<String, String> types = new LinkedHashMap<>();
        types.put("CREDIT", "Credit");
        types.put("DEBIT", "Debit");

        int idx = 0;
        for (Map.Entry<String, String> entry : types.entrySet()) {
            if (walletTransactionTypeRepository.findByCode(entry.getKey()).isEmpty()) {
                log.info("Seeding wallet transaction type: {}", entry.getKey());
                walletTransactionTypeRepository.save(
                        WalletTransactionType.builder()
                                .code(entry.getKey())
                                .name(entry.getValue())
                                .sortOrder(idx)
                                .isActive(true)
                                .build()
                );
            }
            idx++;
        }
    }

    private void seedCurrencies() {
        Map<String, Object[]> currencies = new LinkedHashMap<>();
        currencies.put("USD", new Object[]{"US Dollar", "$"});
        currencies.put("EUR", new Object[]{"Euro", "€"});
        currencies.put("GBP", new Object[]{"British Pound", "£"});
        currencies.put("INR", new Object[]{"Indian Rupee", "₹"});

        int idx = 0;
        for (Map.Entry<String, Object[]> entry : currencies.entrySet()) {
            if (currencyRepository.findByCode(entry.getKey()).isEmpty()) {
                log.info("Seeding currency: {}", entry.getKey());
                Object[] val = entry.getValue();
                currencyRepository.save(
                        Currency.builder()
                                .code(entry.getKey())
                                .name((String) val[0])
                                .symbol((String) val[1])
                                .isDefault("INR".equals(entry.getKey()))
                                .sortOrder(idx)
                                .isActive(true)
                                .build()
                );
            } else if ("INR".equals(entry.getKey())
                    && currencyRepository.findByIsDefaultTrueAndIsActiveTrue().isEmpty()) {
                Currency inr = currencyRepository.findByCode("INR").get();
                inr.setDefault(true);
                currencyRepository.save(inr);
                log.info("Promoting INR as the store default currency");
            }
            idx++;
        }
    }
}
