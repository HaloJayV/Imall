package com.lingwo.mall.component;

import com.lingwo.mall.service.UmsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 资源与角色访问对应关系操作组件
 * Created by lingwo on 2020/7/17.
 */
@Component
public class ResourceRoleRulesHolder {

    @Autowired
    private UmsResourceService resourceService;

    // 初始化资源和角色映射关系
    @PostConstruct
    public void initResourceRolesMap(){
        resourceService.initResourceRolesMap();
    }
}
