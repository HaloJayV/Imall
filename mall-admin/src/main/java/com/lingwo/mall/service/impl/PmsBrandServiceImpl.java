package com.lingwo.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.lingwo.mall.dto.PmsBrandParam;
import com.lingwo.mall.mapper.PmsBrandMapper;
import com.lingwo.mall.mapper.PmsProductMapper;
import com.lingwo.mall.model.PmsBrand;
import com.lingwo.mall.model.PmsBrandExample;
import com.lingwo.mall.model.PmsProduct;
import com.lingwo.mall.model.PmsProductExample;
import com.lingwo.mall.service.PmsBrandService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品品牌Service实现类
 * Created by lingwo on 2018/4/26.
 */
@Service
public class PmsBrandServiceImpl implements PmsBrandService {
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<PmsBrand> listAllBrand() {
        return brandMapper.selectByExample(new PmsBrandExample());
    }

    @Override
    public int createBrand(PmsBrandParam pmsBrandParam) {
        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam, pmsBrand);
        //如果创建时首字母为空，取名称的第一个为首字母
        if (StringUtils.isEmpty(pmsBrand.getFirstLetter())) {
            pmsBrand.setFirstLetter(pmsBrand.getName().substring(0, 1));
        }
        return brandMapper.insertSelective(pmsBrand);
    }


    // 更新品牌
    @Override
    public int updateBrand(Long id, PmsBrandParam pmsBrandParam) {
        PmsBrand pmsBrand = new PmsBrand();
        BeanUtils.copyProperties(pmsBrandParam, pmsBrand);
        pmsBrand.setId(id);
        //如果创建时首字母为空，取名称的第一个为首字母
        if (StringUtils.isEmpty(pmsBrand.getFirstLetter())) {
            pmsBrand.setFirstLetter(pmsBrand.getName().substring(0, 1));
        }
        //更新品牌时要更新商品中的品牌名称
        PmsProduct product = new PmsProduct();
        product.setBrandName(pmsBrand.getName());
        PmsProductExample example = new PmsProductExample();
        example.createCriteria().andBrandIdEqualTo(id);
        productMapper.updateByExampleSelective(product,example);
        return brandMapper.updateByPrimaryKeySelective(pmsBrand);
    }

    @Override
    public int deleteBrand(Long id) {
        return brandMapper.deleteByPrimaryKey(id);
    }

    @Override
    public int deleteBrand(List<Long> ids) {
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.createCriteria().andIdIn(ids);
        return brandMapper.deleteByExample(pmsBrandExample);
    }

    @Override
    public List<PmsBrand> listBrand(String keyword, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.setOrderByClause("sort desc");
        PmsBrandExample.Criteria criteria = pmsBrandExample.createCriteria();
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        return brandMapper.selectByExample(pmsBrandExample);
    }

    @Override
    public PmsBrand getBrand(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    @Override
    public int updateShowStatus(List<Long> ids, Integer showStatus) {
        PmsBrand pmsBrand = new PmsBrand();
        pmsBrand.setShowStatus(showStatus);
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.createCriteria().andIdIn(ids);
        return brandMapper.updateByExampleSelective(pmsBrand, pmsBrandExample);
    }

    @Override
    public int updateFactoryStatus(List<Long> ids, Integer factoryStatus) {
        PmsBrand pmsBrand = new PmsBrand();
        pmsBrand.setFactoryStatus(factoryStatus);
        PmsBrandExample pmsBrandExample = new PmsBrandExample();
        pmsBrandExample.createCriteria().andIdIn(ids);
        return brandMapper.updateByExampleSelective(pmsBrand, pmsBrandExample);
    }
}