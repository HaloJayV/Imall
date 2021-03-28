package com.lingwo.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.lingwo.mall.mapper.OmsOrderReturnReasonMapper;
import com.lingwo.mall.model.OmsOrderReturnReason;
import com.lingwo.mall.model.OmsOrderReturnReasonExample;
import com.lingwo.mall.service.OmsOrderReturnReasonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 订单原因管理Service实现类
 * Created by lingwo on 2018/10/17.
 */
@Service
public class OmsOrderReturnReasonServiceImpl implements OmsOrderReturnReasonService {
    @Autowired
    private OmsOrderReturnReasonMapper returnReasonMapper;
    @Override
    public int create(OmsOrderReturnReason returnReason) {
        returnReason.setCreateTime(new Date());
        return returnReasonMapper.insert(returnReason);
    }

    @Override
    public int update(Long id, OmsOrderReturnReason returnReason) {
        returnReason.setId(id);
        return returnReasonMapper.updateByPrimaryKey(returnReason);
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrderReturnReasonExample example = new OmsOrderReturnReasonExample();
        example.createCriteria().andIdIn(ids);
        return returnReasonMapper.deleteByExample(example);
    }

    @Override
    public List<OmsOrderReturnReason> list(Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        OmsOrderReturnReasonExample example = new OmsOrderReturnReasonExample();
        example.setOrderByClause("sort desc");
        return returnReasonMapper.selectByExample(example);
    }

    @Override
    public int updateStatus(List<Long> ids, Integer status) {
        // status：0->不启用退货原因；1->启用
        if(!status.equals(0)&&!status.equals(1)){
            return 0;
        }
        OmsOrderReturnReason record = new OmsOrderReturnReason();
        record.setStatus(status);
        OmsOrderReturnReasonExample example = new OmsOrderReturnReasonExample();
        example.createCriteria().andIdIn(ids);
        return returnReasonMapper.updateByExampleSelective(record,example);
    }

    @Override
    public OmsOrderReturnReason getItem(Long id) {
        return returnReasonMapper.selectByPrimaryKey(id);
    }
}
