package com.lingwo.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.lingwo.mall.dao.OmsOrderDao;
import com.lingwo.mall.dao.OmsOrderOperateHistoryDao;
import com.lingwo.mall.dto.*;
import com.lingwo.mall.mapper.OmsOrderMapper;
import com.lingwo.mall.mapper.OmsOrderOperateHistoryMapper;
import com.lingwo.mall.model.OmsOrder;
import com.lingwo.mall.model.OmsOrderExample;
import com.lingwo.mall.model.OmsOrderOperateHistory;
import com.lingwo.mall.service.OmsOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单管理Service实现类
 * Created by lingwo on 2018/10/11.
 */
@Service
public class OmsOrderServiceImpl implements OmsOrderService {
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderDao orderDao;
    @Autowired
    private OmsOrderOperateHistoryDao orderOperateHistoryDao;
    @Autowired
    private OmsOrderOperateHistoryMapper orderOperateHistoryMapper;

    // 根据订单查询条件分页查询第pageNum页的pageSize条数据
    @Override
    public List<OmsOrder> list(OmsOrderQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return orderDao.getList(queryParam);
    }

    // 根据订单号、物流公司和单号进行发货
    @Override
    public int delivery(List<OmsOrderDeliveryParam> deliveryParamList) {
        //批量发货，即批量更新该订单的发货信息
        int count = orderDao.delivery(deliveryParamList);
        //添加订单发货操作记录
        List<OmsOrderOperateHistory> operateHistoryList = deliveryParamList.stream()
                .map(omsOrderDeliveryParam -> {
                    OmsOrderOperateHistory history = new OmsOrderOperateHistory();
                    history.setOrderId(omsOrderDeliveryParam.getOrderId());
                    history.setCreateTime(new Date());
                    history.setOperateMan("后台管理员");
                    history.setOrderStatus(2);
                    history.setNote("完成发货");
                    return history;
                }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(operateHistoryList);
        // 返回发货成功的数量
        return count;
    }

    @Override
    public int close(List<Long> ids, String note) {
        OmsOrder record = new OmsOrder();
        // 设置订单状态为已关闭
        record.setStatus(4);
        OmsOrderExample example = new OmsOrderExample();
        // 设置查询条件：根据订单id查询为删除的所有订单记录
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        // 根据查询条件对象example，对指定订单号的多条订单进行批量修改订单状态为已关闭
        int count = orderMapper.updateByExampleSelective(record, example);
        // 添加订单操作记录
        List<OmsOrderOperateHistory> historyList = ids.stream().map(orderId -> {
            OmsOrderOperateHistory history = new OmsOrderOperateHistory();
            history.setOrderId(orderId);
            history.setCreateTime(new Date());
            history.setOperateMan("后台管理员");
            history.setOrderStatus(4);
            history.setNote("订单关闭:"+note);
            return history;
        }).collect(Collectors.toList());
        orderOperateHistoryDao.insertList(historyList);
        return count;
    }

    // 根据订单号批量删除订单
    @Override
    public int delete(List<Long> ids) {
        OmsOrder record = new OmsOrder();
        // 设置删除状态为已删除
        record.setDeleteStatus(1);
        OmsOrderExample example = new OmsOrderExample();
        // 设置检索条件
        example.createCriteria().andDeleteStatusEqualTo(0).andIdIn(ids);
        return orderMapper.updateByExampleSelective(record, example);
    }

    @Override
    public OmsOrderDetail detail(Long id) {
        // 根据订单id获取订单信息、订单商品列表和订单操作记录列表
        return orderDao.getDetail(id);
    }

    @Override
    public int updateReceiverInfo(OmsReceiverInfoParam receiverInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(receiverInfoParam.getOrderId());
        order.setReceiverName(receiverInfoParam.getReceiverName());
        order.setReceiverPhone(receiverInfoParam.getReceiverPhone());
        order.setReceiverPostCode(receiverInfoParam.getReceiverPostCode());
        order.setReceiverDetailAddress(receiverInfoParam.getReceiverDetailAddress());
        order.setReceiverProvince(receiverInfoParam.getReceiverProvince());
        order.setReceiverCity(receiverInfoParam.getReceiverCity());
        order.setReceiverRegion(receiverInfoParam.getReceiverRegion());
        order.setModifyTime(new Date());
        /*根据主键订单号更新订单信息*/
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(receiverInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(receiverInfoParam.getStatus());
        history.setNote("修改收货人信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateMoneyInfo(OmsMoneyInfoParam moneyInfoParam) {
        OmsOrder order = new OmsOrder();
        order.setId(moneyInfoParam.getOrderId());
        order.setFreightAmount(moneyInfoParam.getFreightAmount());
        order.setDiscountAmount(moneyInfoParam.getDiscountAmount());
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        //插入操作记录
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(moneyInfoParam.getOrderId());
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(moneyInfoParam.getStatus());
        history.setNote("修改费用信息");
        orderOperateHistoryMapper.insert(history);
        return count;
    }

    @Override
    public int updateNote(Long id, String note, Integer status) {
        OmsOrder order = new OmsOrder();
        order.setId(id);
        order.setNote(note);
        order.setModifyTime(new Date());
        int count = orderMapper.updateByPrimaryKeySelective(order);
        OmsOrderOperateHistory history = new OmsOrderOperateHistory();
        history.setOrderId(id);
        history.setCreateTime(new Date());
        history.setOperateMan("后台管理员");
        history.setOrderStatus(status); // 备注时修改订单状态
        history.setNote("修改备注信息："+note);
        orderOperateHistoryMapper.insert(history);
        return count;
    }
}
