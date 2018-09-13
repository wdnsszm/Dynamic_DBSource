/**
 * 
 */
package com.dynamic.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.dynamic.datasource.DataSourceKey;

/**
 * @author zhangmaozhuang
 * @date   2018年9月12日下午3:08:48
 * 数据源上下文配置，用于切换数据源
 */
public class DynamicDataSourceContextHolder {

	//用于在切换数据源时保证不会被其他线程修改   可重入锁
	private  static Lock lock=new ReentrantLock();
	private static int counter=0;
	//ThreadLocal用于线程间的数据隔离
	private static final ThreadLocal<Object> CONTEXT_HOLDER=ThreadLocal.withInitial(DataSourceKey.master::name);
	//所有数据库
	public static List<Object> dataSourceKeys = new ArrayList<>();
	//从数据库
	public static List<Object> slaveDataSourceKeys = new ArrayList<>();
	
	//切换数据库
	public static void setDataSourceKey(String key) {
        CONTEXT_HOLDER.set(key);
    }
	
	//使用主数据库
	public static void useMasterDataSource() {
        CONTEXT_HOLDER.set(DataSourceKey.master.name());
    }
	
	//使用分支数据库  当使用只读数据源时通过轮循方式选择要使用的数据源
	 public static void useSlaveDataSource() {
		 lock.lock();
		 
		 //分支数据库的个数
		 try {
			 
			int datasourceKeyIndex = counter%slaveDataSourceKeys.size();
			 CONTEXT_HOLDER.set(String.valueOf(slaveDataSourceKeys.get(datasourceKeyIndex)));
			 counter++;
		} catch (Exception e) {
			System.out.println("切换从数据库错误");
			useMasterDataSource();
			e.printStackTrace();
		}
		 finally {
			lock.unlock();
		}
		 
	 }
	
	
	public static String  getDataSourceKey() {
		
		return (String) CONTEXT_HOLDER.get();
	}
	
	public static void clearDataSourceKey() {
		CONTEXT_HOLDER.remove();
	}
	
	public static boolean containDataSourceKey(String key) {
        return dataSourceKeys.contains(key);
    }

}
