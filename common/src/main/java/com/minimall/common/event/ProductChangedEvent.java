package com.minimall.common.event;

/** Product catalog change for search read-model sync. */
public record ProductChangedEvent(
        String eventId,
        String eventType,
        Long skuId,
        String name,
        Long priceCent
) {
    public static final String PRODUCT_UPSERTED = "PRODUCT_UPSERTED";
    public static final String TOPIC_PRODUCT_CHANGED = "product-changed";
}
