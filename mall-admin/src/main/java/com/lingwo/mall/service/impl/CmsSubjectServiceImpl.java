package com.lingwo.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.lingwo.mall.mapper.CmsSubjectMapper;
import com.lingwo.mall.model.CmsSubject;
import com.lingwo.mall.model.CmsSubjectExample;
import com.lingwo.mall.service.CmsSubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 商品专题Service实现类
 * Created by lingwo on 2018/6/1.
 */
@Service
public class CmsSubjectServiceImpl implements CmsSubjectService {
    @Autowired
    private CmsSubjectMapper subjectMapper;

    @Override
    public List<CmsSubject> listAll() {
        return subjectMapper.selectByExample(new CmsSubjectExample());
    }

    // 根据关键字分页获取第pageNum页，pageSize行记录的数据
    @Override
    public List<CmsSubject> list(String keyword, Integer pageNum, Integer pageSize) {
        // 只对该语句之后第一个查询语句得到的数据进行分页，查询第pageNum页，每页数据有pageSize个记录
        PageHelper.startPage(pageNum, pageSize);
        CmsSubjectExample example = new CmsSubjectExample();
        CmsSubjectExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andTitleLike("%" + keyword + "%");
        }
        return subjectMapper.selectByExample(example);
    }
}
