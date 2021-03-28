package com.lingwo.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.lingwo.mall.dao.OmsOrderReturnApplyDao;
import com.lingwo.mall.dto.OmsOrderReturnApplyResult;
import com.lingwo.mall.dto.OmsReturnApplyQueryParam;
import com.lingwo.mall.dto.OmsUpdateStatusParam;
import com.lingwo.mall.mapper.OmsOrderReturnApplyMapper;
import com.lingwo.mall.model.OmsOrderReturnApply;
import com.lingwo.mall.model.OmsOrderReturnApplyExample;
import com.lingwo.mall.service.OmsOrderReturnApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 订单退货管理Service
 * Created by lingwo on 2018/10/18.
 */
@Service
public class OmsOrderReturnApplyServiceImpl implements OmsOrderReturnApplyService {
    @Autowired
    private OmsOrderReturnApplyDao returnApplyDao;
    @Autowired
    private OmsOrderReturnApplyMapper returnApplyMapper;
    @Override
    public List<OmsOrderReturnApply> list(OmsReturnApplyQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum,pageSize);
        return returnApplyDao.getList(queryParam);
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrderReturnApplyExample example = new OmsOrderReturnApplyExample();
        // 通过退货申请id号删除对应的状态为已拒绝的退货申请记录
        example.createCriteria().andIdIn(ids).andStatusEqualTo(3);
        return returnApplyMapper.deleteByExample(example);
    }

    @Override
    public int updateStatus(Long id, OmsUpdateStatusParam statusParam) {
        Integer status = statusParam.getStatus();
        OmsOrderReturnApply returnApply = new OmsOrderReturnApply();
        // 申请状态：1->退货中；2->已完成；3->已拒绝
        if(status.equals(1)){
            //确认同意退货，设置为退货中
            returnApply.setId(id);
            returnApply.setStatus(1);
            // 设置退款金额
            returnApply.setReturnAmount(statusParam.getReturnAmount());
            // 设置退货公司地址
            returnApply.setCompanyAddressId(statusParam.getCompanyAddressId());
            returnApply.setHandleTime(new Date());
            // 设置收货人
            returnApply.setHandleMan(statusParam.getHandleMan());
            // 设置收获备注信息
            returnApply.setHandleNote(statusParam.getHandleNote());
        }else if(status.equals(2)){
            //完成退货
            returnApply.setId(id);
            returnApply.setStatus(2);
            returnApply.setReceiveTime(new Date());
            returnApply.setReceiveMan(statusParam.getReceiveMan());
            returnApply.setReceiveNote(statusParam.getReceiveNote());
        }else if(status.equals(3)){
            //拒绝退货
            returnApply.setId(id);
            returnApply.setStatus(3);
            returnApply.setHandleTime(new Date());
            returnApply.setHandleMan(statusParam.getHandleMan());
            returnApply.setHandleNote(statusParam.getHandleNote());
        }else{
            return 0;
        }
        return returnApplyMapper.updateByPrimaryKeySelective(returnApply);
    }

    // 根据退货申请主键获取订单退货申请信息和对应收获公司地址
    @Override
    public OmsOrderReturnApplyResult getItem(Long id) {
        return returnApplyDao.getDetail(id);
    }
}
