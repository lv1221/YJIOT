package com.yjyz.iot.logistice.dao;

import java.util.List;

import com.yjyz.iot.logistice.entity.DevLogisticeInfo;

public interface DevLogisticeInfoMapper {
	int deleteByPrimaryKey(String guid);

	int insert(DevLogisticeInfo record);

	int insertSelective(DevLogisticeInfo record);

	DevLogisticeInfo selectByPrimaryKey(String guid);

	int updateByPrimaryKeySelective(DevLogisticeInfo record);

	int updateByPrimaryKey(DevLogisticeInfo record);

	List<DevLogisticeInfo> selectByDeviceMac(String deviceMac);
}