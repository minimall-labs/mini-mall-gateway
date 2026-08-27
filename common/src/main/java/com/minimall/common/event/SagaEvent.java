package com.minimall.common.event;

/**
 * Shared RocketMQ saga payload. One order currently supports a single SKU line for clarity.
 */
public record SagaEvent(
        String eventId,
        String eventType,
        Long orderId,
        Long userId,
        Long skuId,
        Integer qty,
        Long amountCent,
        Boolean forceFail,
        Long userCouponId
) {
    public static final String ORDER_CREATED = "ORDER_CREATED";
    public static final String PAYMENT_SUCCEEDED = "PAYMENT_SUCCEEDED";
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";

    public static final String TOPIC_ORDER_CREATED = "order-created";
    public static final String TOPIC_PAYMENT_SUCCEEDED = "payment-succeeded";
    public static final String TOPIC_PAYMENT_FAILED = "payment-failed";

    public SagaEvent(
            String eventId,
            String eventType,
            Long orderId,
            Long userId,
            Long skuId,
            Integer qty,
            Long amountCent,
            Boolean forceFail) {
        this(eventId, eventType, orderId, userId, skuId, qty, amountCent, forceFail, null);
    }
}
