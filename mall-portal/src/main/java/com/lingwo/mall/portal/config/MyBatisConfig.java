package com.lingwo.mall.portal.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * MyBatis相关配置
 * Created by lingwo on 2019/4/8.
 */
@Configuration
@EnableTransactionManagement
@MapperScan({"com.lingwo.mall.mapper","com.lingwo.mall.portal.dao"})
public class MyBatisConfig {
}
