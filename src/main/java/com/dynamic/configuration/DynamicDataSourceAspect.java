/**
 * 
 */
package com.dynamic.configuration;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author zhangmaozhuang
 * @date   2018年9月13日上午9:56:24
 * 动态数据源切换的切面，切 DAO 层，通过 DAO 层方法名判断使用哪个数据源，
 * 实现数据源切换 关于切面的 Order 可以可以不设，
 * 因为 @Transactional 是最低的，取决于其他切面的设置，
 * 并且在 org.springframework.core.annotation.AnnotationAwareOrderComparator 会重新排序
 */
@Aspect
@Component
public class DynamicDataSourceAspect {
	
	private final String[] QUERY_PREFIX = {"select"};
	
	@Pointcut("execution( * com.dynamic.mapper.*.*(..))")
    public void daoAspect() {
		
	}
    
	//切换数据源  
	@Before("daoAspect()")
	public void switchDataSource(JoinPoint point) {
        Boolean isQueryMethod = isQueryMethod(point.getSignature().getName());
        if (isQueryMethod) {
            DynamicDataSourceContextHolder.useSlaveDataSource();
            
            DynamicDataSourceContextHolder.getDataSourceKey(); 
                    
            point.getSignature();
        }
    }
	//回复数据源
	@After("daoAspect()")
	public void restoreDataSource(JoinPoint point) {
		DynamicDataSourceContextHolder.clearDataSourceKey();
                DynamicDataSourceContextHolder.getDataSourceKey();
                point.getSignature();
	}

	/**
	 * @param name
	 * @return
	 * 判断是否以查询前缀开始，根据业务需求，可以对QUERY_PREFIX 数组中的前缀内容修改
	 */
	private Boolean isQueryMethod(String methodname) {
		for(String prefix : QUERY_PREFIX) {
			if(methodname.startsWith(prefix)) {
                return true;
            }
			
		}
		return false;
	}
}
			
			
	
	 


