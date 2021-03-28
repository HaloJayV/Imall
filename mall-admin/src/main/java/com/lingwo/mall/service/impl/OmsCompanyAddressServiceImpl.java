package com.lingwo.mall.service.impl;

import com.lingwo.mall.mapper.OmsCompanyAddressMapper;
import com.lingwo.mall.model.OmsCompanyAddress;
import com.lingwo.mall.model.OmsCompanyAddressExample;
import com.lingwo.mall.service.OmsCompanyAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 收货地址管理Service实现类
 * Created by lingwo on 2018/10/18.
 */
@Service
public class OmsCompanyAddressServiceImpl implements OmsCompanyAddressService {
    @Autowired
    private OmsCompanyAddressMapper companyAddressMapper;
    @Override
    public List<OmsCompanyAddress> list() {
        return companyAddressMapper.selectByExample(new OmsCompanyAddressExample());
    }
}
