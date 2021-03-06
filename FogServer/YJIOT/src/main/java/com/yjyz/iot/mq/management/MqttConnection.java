package com.yjyz.iot.mq.management;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import org.springframework.beans.factory.annotation.Autowired;

import com.yjyz.iot.device.dao.DevAccountMapper;
import com.yjyz.iot.mq.management.view.ConnectionView;

/**
 * MQTT连接定时监测任务
 * 
 * @class :MqttConnection
 * @TODO :
 * @author:HeroLizhen
 * @date :2018年3月22日上午11:30:55
 */
public class MqttConnection {

	private static final String ObjectNameString = "org.apache.activemq:type=Broker,brokerName=localhost,connector=clientConnectors,connectorName=mqtt,connectionViewType=clientId,connectionName=*";
	@Autowired
	private MBeanServerConnection connection;
	@Resource
	DevAccountMapper devAccountDao;

	public void queryConnections() throws Exception {
		ObjectName mbeanName = new ObjectName(ObjectNameString);
		Set<ObjectInstance> set = connection.queryMBeans(mbeanName, null);
		for (Iterator<ObjectInstance> it = set.iterator(); it.hasNext();) {
			ObjectInstance oi = (ObjectInstance) it.next();
			System.out.println("\t" + oi.getObjectName());
			ConnectionView view = new ConnectionView(connection, oi.getObjectName());
			System.out.println(view.getClientId());
			System.out.println(view.isConnected());
			System.out.println(view.isActive());
			System.out.println(view.isSlow());
		}
	}

	/**
	 * @name:queryConnectionNames
	 * @TODO:后去mqtt连接名
	 * @date:2018年3月22日 上午11:30:11
	 * @return
	 * @throws Exception
	 *             List<String>
	 */
	public List<String> queryConnectionNames() throws Exception {
		ObjectName objectQuery = new ObjectName(ObjectNameString);
		Set<ObjectInstance> set = connection.queryMBeans(objectQuery, null);
		List<String> list = new ArrayList<String>();
		for (Iterator<ObjectInstance> it = set.iterator(); it.hasNext();) {
			ObjectInstance oi = (ObjectInstance) it.next();
			String objectName = oi.getObjectName().toString();
			list.add(objectName.split("connectionName=")[1]);
		}
		return list;
	}

	/**
	 * @name:queryConnectionViews
	 * @TODO:获取所有mtqq连接的详细信息
	 * @date:2018年3月22日 上午11:30:28
	 * @return
	 * @throws Exception
	 *             List<ConnectionView>
	 */
	public List<ConnectionView> queryConnectionViews() throws Exception {
		List<ConnectionView> list = new ArrayList<ConnectionView>();
		ObjectName objectQuery = new ObjectName(ObjectNameString);
		Set<ObjectInstance> set = connection.queryMBeans(objectQuery, null);
		for (Iterator<ObjectInstance> it = set.iterator(); it.hasNext();) {
			ObjectInstance oi = (ObjectInstance) it.next();
			ConnectionView view = new ConnectionView(connection, oi.getObjectName());
			list.add(view);
		}
		return list;
	}

	public void devOnlineUpdate() {
		try {
			List<String> cnList = this.queryConnectionNames();
			if (cnList.size() > 0) {
				 this.devAccountDao.updateMqttDevOnlineState(cnList);
			}
			System.out.println("Eshare Current online device count ：" + cnList.size());
		} catch (Exception e) {
			System.out.println(e.getStackTrace());

		}
	}
}
