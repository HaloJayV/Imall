package com.lingwo.mall.search.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis相关配置
 * Created by lingwo on 2019/4/8.
 */
@Configuration
@MapperScan({"com.lingwo.mall.mapper","com.lingwo.mall.search.dao"})
public class MyBatisConfig {
}
