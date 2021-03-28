package com.lingwo.mall.service;

import com.lingwo.mall.model.PmsSkuStock;

import java.util.List;

/**
 * sku商品库存管理Service
 * Created by lingwo on 2018/4/27.
 */
public interface PmsSkuStockService {
    /**
     * 根据产品id和skuCode模糊搜索
     */
    List<PmsSkuStock> getList(Long pid, String keyword);

    /**
     * 批量更新商品库存信息
     */
    int update(Long pid, List<PmsSkuStock> skuStockList);
}