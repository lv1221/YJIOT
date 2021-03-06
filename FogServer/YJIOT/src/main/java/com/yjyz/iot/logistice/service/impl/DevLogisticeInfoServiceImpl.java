package com.yjyz.iot.logistice.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yjyz.iot.logistice.dao.DevLogisticeInfoMapper;
import com.yjyz.iot.logistice.entity.DevLogisticeInfo;
import com.yjyz.iot.logistice.service.IDevLogisticeInfoService;

/**
 * 设备物流管理
 * 
 * @class :DevLogisticeInfoServiceImpl
 * @TODO :
 * @author:HeroLizhen
 * @date :2017年12月25日上午10:19:24
 */
@Service("devLogisticeInfoService")
public class DevLogisticeInfoServiceImpl implements IDevLogisticeInfoService {
	@Autowired
	private DevLogisticeInfoMapper devLogisticeInfoDao;

	/**
	 * @name:addLogisticeInfo
	 * @TODO:增加物流信息
	 * @date:2017年12月25日 上午10:19:35
	 * @param info
	 * @param userId
	 * @return
	 * @throws Exception
	 *             boolean
	 */
	@Override
	public DevLogisticeInfo addLogisticeInfo(DevLogisticeInfo info, String userId) throws Exception {
		info.setGuid(UUID.randomUUID().toString());
		info.setOpUser(userId);
		int ret = this.devLogisticeInfoDao.insertSelective(info);
		if (ret == 1) {
			return info;
		} else {
			return null;
		}
	}

	/**
	 * @name:updLogisticeInfo
	 * @TODO:修改物流信息
	 * @date:2017年12月25日 上午10:19:49
	 * @param info
	 * @return
	 * @throws Exception
	 *             boolean
	 */
	@Override
	public boolean updLogisticeInfo(DevLogisticeInfo info) throws Exception {
		int ret = this.devLogisticeInfoDao.updateByPrimaryKeySelective(info);
		if (ret == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @name:selLogisticeInfo
	 * @TODO:查询设备物流信息
	 * @date:2017年12月25日 上午10:20:01
	 * @param info
	 * @return
	 * @throws Exception
	 *             List<DevLogisticeInfo>
	 */
	@Override
	public List<DevLogisticeInfo> selectByDeviceMac(String deviceMac) throws Exception {
		List<DevLogisticeInfo> ret = this.devLogisticeInfoDao.selectByDeviceMac(deviceMac);
		return ret;
	}
}
