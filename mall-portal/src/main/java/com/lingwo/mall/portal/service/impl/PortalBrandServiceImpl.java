package com.lingwo.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.lingwo.mall.common.api.CommonPage;
import com.lingwo.mall.mapper.PmsBrandMapper;
import com.lingwo.mall.mapper.PmsProductMapper;
import com.lingwo.mall.model.PmsBrand;
import com.lingwo.mall.model.PmsProduct;
import com.lingwo.mall.model.PmsProductExample;
import com.lingwo.mall.portal.dao.HomeDao;
import com.lingwo.mall.portal.service.PortalBrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 前台品牌管理Service实现类
 * Created by lingwo on 2020/5/15.
 */
@Service
public class PortalBrandServiceImpl implements PortalBrandService {
    @Autowired
    private HomeDao homeDao;
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductMapper productMapper;

    /**
     * @param pageNum 第几页 默认1
     * @param pageSize 每页记录数 默认6
     * @return
     */
    @Override
    public List<PmsBrand> recommendList(Integer pageNum, Integer pageSize) {
        // 开始索引，默认从0开始
        int offset = (pageNum - 1) * pageSize;
        return homeDao.getRecommendBrandList(offset, pageSize);
    }

    @Override
    public PmsBrand detail(Long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    @Override
    public CommonPage<PmsProduct> productList(Long brandId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andDeleteStatusEqualTo(0)
                .andBrandIdEqualTo(brandId);
        List<PmsProduct> productList = productMapper.selectByExample(example);
        return CommonPage.restPage(productList);
    }
}
