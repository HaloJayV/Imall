package com.lingwo.mall.portal.service;

import com.lingwo.mall.portal.domain.OmsOrderReturnApplyParam;

/**
 * 订单退货管理Service
 * Created by lingwo on 2018/10/17.
 */
public interface OmsPortalOrderReturnApplyService {
    /**
     * 提交申请
     */
    int create(OmsOrderReturnApplyParam returnApply);
}
