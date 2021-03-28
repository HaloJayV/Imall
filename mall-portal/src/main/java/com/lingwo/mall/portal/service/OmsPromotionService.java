package com.lingwo.mall.portal.service;

import com.lingwo.mall.model.OmsCartItem;
import com.lingwo.mall.portal.domain.CartPromotionItem;

import java.util.List;

/**
 * Created by lingwo on 2018/8/27.
 * 促销管理Service
 */
public interface OmsPromotionService {
    /**
     * 计算购物车中的促销活动信息
     * @param cartItemList 购物车
     */
    List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList);
}
