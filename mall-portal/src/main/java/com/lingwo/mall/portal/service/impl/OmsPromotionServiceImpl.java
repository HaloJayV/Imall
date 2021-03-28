package com.lingwo.mall.portal.service.impl;

import com.lingwo.mall.model.OmsCartItem;
import com.lingwo.mall.model.PmsProductFullReduction;
import com.lingwo.mall.model.PmsProductLadder;
import com.lingwo.mall.model.PmsSkuStock;
import com.lingwo.mall.portal.dao.PortalProductDao;
import com.lingwo.mall.portal.domain.CartPromotionItem;
import com.lingwo.mall.portal.domain.PromotionProduct;
import com.lingwo.mall.portal.service.OmsPromotionService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by lingwo on 2018/8/27.
 * 促销管理Service实现类
 */
@Service
public class OmsPromotionServiceImpl implements OmsPromotionService {
    @Autowired
    private PortalProductDao portalProductDao;

    // 购物车优惠计算
    @Override
    public List<CartPromotionItem> calcCartPromotion(List<OmsCartItem> cartItemList) {
        // 1.先根据productId对CartItem进行分组，以spu为单位进行计算优惠
        Map<Long, List<OmsCartItem>> productCartMap = groupCartItemBySpu(cartItemList);
        // 2.根据购物车每一项商品信息 查询所有商品的优惠相关信息：基本细腻、库存、折扣、满减
        List<PromotionProduct> promotionProductList = getPromotionProductList(cartItemList);
        // 3.根据商品促销类型计算商品促销优惠价格
        List<CartPromotionItem> cartPromotionItemList = new ArrayList<>();

        // 4.forEach遍历购物车每一项商品信息并计算优惠结果，productId为key，OmsCartItem为value
        for (Map.Entry<Long, List<OmsCartItem>> entry : productCartMap.entrySet()) {
            // 获取每个商品ID
            Long productId = entry.getKey();
            // 根据商品id获取商品自身相应促销信息
            PromotionProduct promotionProduct = getPromotionProductById(productId, promotionProductList);
            // 该商品在该购物车中的商品项集合
            List<OmsCartItem> itemList = entry.getValue();
            // 促销类型：0->没有促销使用原价;1->使用促销价；2->使用会员价；3->使用阶梯价格；4->使用满减价格；5->限时购
            // 获取商品对应促销类型
            Integer promotionType = promotionProduct.getPromotionType();
            // 该商品使用促销价
            if (promotionType == 1) {
                //单品促销
                for (OmsCartItem item : itemList) {
                    // 商品活动
                    CartPromotionItem cartPromotionItem = new CartPromotionItem();
                    // 相当于给当前商品对象添加商品活动信息
                    BeanUtils.copyProperties(item,cartPromotionItem);
                    cartPromotionItem.setPromotionMessage("单品促销");
                    //商品原价-促销价
                    // 根据商品id从库存表中获取商品的库存信息
                    PmsSkuStock skuStock = getOriginalPrice(promotionProduct, item.getProductSkuId());
                    // 从库存中获取商品原价
                    BigDecimal originalPrice = skuStock.getPrice();
                    //单品促销使用原价
                    cartPromotionItem.setPrice(originalPrice);
                    // 优惠了：商品原价-促销价
                    cartPromotionItem.setReduceAmount(originalPrice.subtract(skuStock.getPromotionPrice()));
                    // 真实库存 = 当前库存-锁定库存
                    cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
                    // 该商品对应积分
                    cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                    // 会员成长值
                    cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                    // 接收每一项处理后的商品
                    cartPromotionItemList.add(cartPromotionItem);
                }
                // 使用折扣价格
            } else if (promotionType == 3) {
                // 当前购物车商品数量
                int count = getCartItemCount(itemList);
                // 该商品对应数量的折扣对象
                PmsProductLadder ladder = getProductLadder(count, promotionProduct.getProductLadderList());
                if(ladder!=null){
                    // 处理购物车每一项商品
                    for (OmsCartItem item : itemList) {
                        // 购物车促销信息
                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        // 跟当前商品对象添加促销信息
                        BeanUtils.copyProperties(item,cartPromotionItem);
                        // 打折信息
                        String message = getLadderPromotionMessage(ladder);
                        // 设置该商品打折信息
                        cartPromotionItem.setPromotionMessage(message);
                        // 根据商品skuId获取对应库存信息
                        PmsSkuStock skuStock = getOriginalPrice(promotionProduct,item.getProductSkuId());
                        // 从库存获取原价
                        BigDecimal originalPrice = skuStock.getPrice();
                        // 折扣了：商品原价-折扣*商品原价
                        BigDecimal reduceAmount = originalPrice.subtract(ladder.getDiscount().multiply(originalPrice));
                        // 设置折扣金额
                        cartPromotionItem.setReduceAmount(reduceAmount);
                        // 减少库存
                        cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
                        // 增加积分
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        // 增加会员成长值
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        // 封装每个商品项
                        cartPromotionItemList.add(cartPromotionItem);
                    }
                }else{
                    // 没有折扣价格，则不打折
                    handleNoReduce(cartPromotionItemList,itemList,promotionProduct);
                }
            } else if (promotionType == 4) {
                // 处理满减优惠
                // 获取商品未优惠前的总价
                BigDecimal totalAmount= getCartItemAmount(itemList,promotionProductList);
                // 根据商品总价、商品满减优惠对象集合，获取商品对应的满减优惠对象
                PmsProductFullReduction fullReduction = getProductFullReduction(totalAmount,promotionProduct.getProductFullReductionList());
                // 该商品有满减优惠
                if(fullReduction!=null){
                    for (OmsCartItem item : itemList) {
                        CartPromotionItem cartPromotionItem = new CartPromotionItem();
                        // 给商品对象添加满减优惠信息
                        BeanUtils.copyProperties(item,cartPromotionItem);
                        String message = getFullReductionPromotionMessage(fullReduction);
                        cartPromotionItem.setPromotionMessage(message);
                        // 根据库存获取商品单原价
                        PmsSkuStock skuStock= getOriginalPrice(promotionProduct, item.getProductSkuId());
                        BigDecimal originalPrice = skuStock.getPrice();
                        // 满减优惠的金额 = (商品原价/总价)*满减金额   (商品原价/总价)向最接近数字方向舍入
                        BigDecimal reduceAmount = originalPrice.divide(totalAmount,RoundingMode.HALF_EVEN).multiply(fullReduction.getReducePrice());
                        // 设置满减减去的金额
                        cartPromotionItem.setReduceAmount(reduceAmount);
                        // 减库存
                        cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
                        // 添加积分
                        cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
                        // 添加成长值
                        cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
                        // 封装集合
                        cartPromotionItemList.add(cartPromotionItem);
                    }
                }else{
                    // 没有满减
                    handleNoReduce(cartPromotionItemList,itemList,promotionProduct);
                }
            } else {
                //无任何优惠
                handleNoReduce(cartPromotionItemList, itemList,promotionProduct);
            }
        }
        return cartPromotionItemList;
    }


    /**
     * 查询所有商品的优惠相关信息：基本信息、库存、打折、满减
     */
    private List<PromotionProduct> getPromotionProductList(List<OmsCartItem> cartItemList) {
        List<Long> productIdList = new ArrayList<>();
        for(OmsCartItem cartItem:cartItemList){
            productIdList.add(cartItem.getProductId());
        }
        // 根据商品id批量获取所有商品信息以及优惠信息：库存、打折、满减
        return portalProductDao.getPromotionProductList(productIdList);
    }

    /**
     * 以spu为单位，即以商品ID为单位对购物车中商品进行分组
     */
    private Map<Long, List<OmsCartItem>> groupCartItemBySpu(List<OmsCartItem> cartItemList) {
        // 以商品id为key，购物车商品项为value，用TreeMap存储。便于快速检索、自然排序
        Map<Long, List<OmsCartItem>> productCartMap = new TreeMap<>();
        for (OmsCartItem cartItem : cartItemList) {
            // 批量通过购物车中每个商品项商品id获取商品项信息集合
            List<OmsCartItem> productCartItemList = productCartMap.get(cartItem.getProductId());
            // 购物车商品项集合
            if (productCartItemList == null) {
                productCartItemList = new ArrayList<>();
                productCartItemList.add(cartItem);
                // 放进map
                productCartMap.put(cartItem.getProductId(), productCartItemList);
            } else {
                // 根据商品id进行分组
                productCartItemList.add(cartItem);
            }
        }
        return productCartMap;
    }

    /**
     * 获取满减促销消息
     */
    private String getFullReductionPromotionMessage(PmsProductFullReduction fullReduction) {
        StringBuilder sb = new StringBuilder();
        sb.append("满减优惠：");
        sb.append("满");
        sb.append(fullReduction.getFullPrice());
        sb.append("元，");
        sb.append("减");
        sb.append(fullReduction.getReducePrice());
        sb.append("元");
        return sb.toString();
    }

    /**
     * 对没满足优惠条件的商品进行处理
     */
    private void handleNoReduce(List<CartPromotionItem> cartPromotionItemList, List<OmsCartItem> itemList,PromotionProduct promotionProduct) {
        for (OmsCartItem item : itemList) {
            CartPromotionItem cartPromotionItem = new CartPromotionItem();
            // 商品对象添加对应优惠信息
            BeanUtils.copyProperties(item,cartPromotionItem);
            cartPromotionItem.setPromotionMessage("无优惠");
            cartPromotionItem.setReduceAmount(new BigDecimal(0));
            PmsSkuStock skuStock = getOriginalPrice(promotionProduct,item.getProductSkuId());
            if(skuStock!=null){
                cartPromotionItem.setRealStock(skuStock.getStock()-skuStock.getLockStock());
            }
            cartPromotionItem.setIntegration(promotionProduct.getGiftPoint());
            cartPromotionItem.setGrowth(promotionProduct.getGiftGrowth());
            cartPromotionItemList.add(cartPromotionItem);
        }
    }

    private PmsProductFullReduction getProductFullReduction(BigDecimal totalAmount,List<PmsProductFullReduction> fullReductionList) {
        //按条件从高到低排序
        fullReductionList.sort(new Comparator<PmsProductFullReduction>() {
            @Override
            public int compare(PmsProductFullReduction o1, PmsProductFullReduction o2) {
                // 根据满减金额从高到低排序，即倒序排序
                return o2.getFullPrice().subtract(o1.getFullPrice()).intValue();
            }
        });
        for(PmsProductFullReduction fullReduction:fullReductionList){
            // 只返回原本商品总价 >= 满减金额的满减对象
            if(totalAmount.subtract(fullReduction.getFullPrice()).intValue()>=0){
                return fullReduction;
            }
        }
        return null;
    }

    /**
     * 根据折扣对象获取打折优惠的促销信息
     */
    private String getLadderPromotionMessage(PmsProductLadder ladder) {
        StringBuilder sb = new StringBuilder();
        sb.append("打折优惠：");
        sb.append("满");
        sb.append(ladder.getCount());
        sb.append("件，");
        sb.append("打");
        sb.append(ladder.getDiscount().multiply(new BigDecimal(10)));
        sb.append("折");
        return sb.toString();
    }

    /**
     * 根据购买商品数量获取满足条件的打折优惠策略
     */
    private PmsProductLadder getProductLadder(int count, List<PmsProductLadder> productLadderList) {
        //按数量从大到小排序
        productLadderList.sort(new Comparator<PmsProductLadder>() {
            @Override
            public int compare(PmsProductLadder o1, PmsProductLadder o2) {
                return o2.getCount() - o1.getCount();
            }
        });
        for (PmsProductLadder productLadder : productLadderList) {
            if (count >= productLadder.getCount()) {
                return productLadder;
            }
        }
        return null;
    }

    /**
     * 获取购物车中指定商品的数量，以sku为单位
     */
    private int getCartItemCount(List<OmsCartItem> itemList) {
        int count = 0;
        for (OmsCartItem item : itemList) {
            count += item.getQuantity();
        }
        return count;
    }

    /**
     * 获取购物车中指定商品的原本总价
     */
    private BigDecimal getCartItemAmount(List<OmsCartItem> itemList, List<PromotionProduct> promotionProductList) {
        BigDecimal amount = new BigDecimal(0);
        for (OmsCartItem item : itemList) {
            // 根据商品信息获取商品对应优惠对象
            PromotionProduct promotionProduct = getPromotionProductById(item.getProductId(), promotionProductList);
            // 获取库存对象
            PmsSkuStock skuStock = getOriginalPrice(promotionProduct,item.getProductSkuId());
            // 总价 = 原价 * 商品数量
            amount = amount.add(skuStock.getPrice().multiply(new BigDecimal(item.getQuantity())));
        }
        return amount;
    }

    /**
     * 根据商品id从库存表中获取商品的原价
     */
    private PmsSkuStock getOriginalPrice(PromotionProduct promotionProduct, Long productSkuId) {
        for (PmsSkuStock skuStock : promotionProduct.getSkuStockList()) {
            if (productSkuId.equals(skuStock.getId())) {
                return skuStock;
            }
        }
        return null;
    }

    /**
     * 根据商品id获取商品的促销信息
     */
    private PromotionProduct getPromotionProductById(Long productId, List<PromotionProduct> promotionProductList) {
        for (PromotionProduct promotionProduct : promotionProductList) {
            if (productId.equals(promotionProduct.getId())) {
                return promotionProduct;
            }
        }
        return null;
    }
}
