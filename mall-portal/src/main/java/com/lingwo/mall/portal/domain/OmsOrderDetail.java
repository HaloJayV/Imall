package com.lingwo.mall.portal.domain;

import com.lingwo.mall.model.OmsOrder;
import com.lingwo.mall.model.OmsOrderItem;

import java.util.List;

/**
 * 包含订单商品信息的订单详情
 * Created by lingwo on 2018/9/4.
 */
public class OmsOrderDetail extends OmsOrder {
    private List<OmsOrderItem> orderItemList;

    public List<OmsOrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OmsOrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
