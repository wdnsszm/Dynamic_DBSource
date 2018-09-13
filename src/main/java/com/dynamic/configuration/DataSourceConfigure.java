/**
 * 
 */
package com.dynamic.configuration;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.dynamic.datasource.DataSourceKey;

/**
 * @author zhangmaozhuang
 * @date   2018年9月12日下午3:10:04
 * @discription 数据源配置类 在该类中生成多个数据源实例并将其注入到ApplicationContext中
 */
@Configuration
public class DataSourceConfigure {
	/* *
	 *  master datasource 
	 *  @Primary 注解用于标识默认使用的DataSource bean，该注解可以用于master
	 *  @CongigurationProperties 用于从application.properties 中读取配置，为bean设置属性
	 *   
	 */
	@Bean("master")
	@Primary
	@ConfigurationProperties(prefix="spring.datasource.druid.master")
	public  DataSource master() {
		return DruidDataSourceBuilder.create().build();
	}
	@Bean("test1")
	@ConfigurationProperties(prefix="spring.datasource.druid.test1")
	public DataSource test1() {
		return DruidDataSourceBuilder.create().build();
	}
	@Bean("test2")
	@ConfigurationProperties(prefix="spring.datasource.druid.test2")
	public DataSource test2() {
		return DruidDataSourceBuilder.create().build();
	}
	@Bean("dynamicDataSource")
	public DataSource dynamicDataSource() {
		DynamicRoutingDataSource dynamicRoutingDataSource =new DynamicRoutingDataSource();
		Map<Object, Object> dataSourceMap =new HashMap<Object, Object>();
		dataSourceMap.put(DataSourceKey.master.name(), master());
		dataSourceMap.put(DataSourceKey.test1.name(), test1());
		dataSourceMap.put(DataSourceKey.test2.name(), test2());
		//将master作为默认的数据源
		dynamicRoutingDataSource.setDefaultTargetDataSource(master());
		//将map中的数据源作为指定的数据源
		dynamicRoutingDataSource.setTargetDataSources(dataSourceMap);
		DynamicDataSourceContextHolder.dataSourceKeys.addAll(dataSourceMap.keySet());
		
		DynamicDataSourceContextHolder.slaveDataSourceKeys.addAll(dataSourceMap.keySet());
        DynamicDataSourceContextHolder.slaveDataSourceKeys.remove(DataSourceKey.master.name());
        return dynamicRoutingDataSource;

	}
	@Bean
	@ConfigurationProperties(prefix = "mybatis")
    public SqlSessionFactoryBean sqlSessionFactoryBean() {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        // Here is very important, if don't config this, will can't switch datasource
        // put all datasource into SqlSessionFactoryBean, then will autoconfig SqlSessionFactory
        sqlSessionFactoryBean.setDataSource(dynamicDataSource());
        return sqlSessionFactoryBean;
    }
	
	@Bean
    public PlatformTransactionManager transactionManager() {
        return new DataSourceTransactionManager(dynamicDataSource());
    }
	
}
