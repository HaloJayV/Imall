package com.lingwo.mall.portal.domain;

import lombok.Getter;

/**
 * 消息队列枚举配置
 * Created by lingwo on 2018/9/14.
 */
@Getter
public enum QueueEnum {
    /**
     * 消息通知队列
     */
    QUEUE_ORDER_CANCEL("mall.order.direct", "mall.order.cancel", "mall.order.cancel"),
    /**
     * 消息通知ttl队列
     */
    QUEUE_TTL_ORDER_CANCEL("mall.order.direct.ttl", "mall.order.cancel.ttl", "mall.order.cancel.ttl");

    /**
     * 交换名称
     */
    private String exchange;
    /**
     * 队列名称
     */
    private String name;
    /**
     * 路由键
     */
    private String routeKey;

    QueueEnum(String exchange, String name, String routeKey) {
        // 接收生产者发送的消息，并根据路由键发送给指定队列
        this.exchange = exchange; // 交换机
        this.name = name; // 队列名
        this.routeKey = routeKey; // 路由键
    }
}
