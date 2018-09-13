/**
 * 
 */
package com.dynamic.configuration;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * @author zhangmaozhuang
 * @date   2018年9月12日下午4:06:32
 */
public class DynamicRoutingDataSource extends AbstractRoutingDataSource {

	/* (non-Javadoc)
	 * @see org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource#determineCurrentLookupKey()
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		// TODO Auto-generated method stub
		return DynamicDataSourceContextHolder.getDataSourceKey();
	}

}
