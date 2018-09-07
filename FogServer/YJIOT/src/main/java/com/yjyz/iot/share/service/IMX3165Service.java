package com.yjyz.iot.share.service;

import java.util.List;

import com.yjyz.iot.device.entity.DevAccount;
import com.yjyz.iot.device.entity.DevConsume;
import com.yjyz.iot.device.entity.DevControl;
import com.yjyz.iot.device.entity.DevRelation;
import com.yjyz.iot.device.entity.DeviceInfo;
import com.yjyz.iot.ota.entity.OTAFileInfo;
import com.yjyz.iot.ota.entity.OTAUpdateLog;
import com.yjyz.iot.share.dto.ActivateDto;

public interface IMX3165Service {
	DeviceInfo activate(ActivateDto dto) throws Exception;

	DeviceInfo getDeviceByID(String deviceId) throws Exception;

	int updateDeviceInfo(DeviceInfo devInfo) throws Exception;

	OTAFileInfo selectByOTACheck(OTAFileInfo otaCheck) throws Exception;

	int saveOTAUpdateLog(OTAUpdateLog log) throws Exception;

	int updateDevAccount(DevAccount record) throws Exception;

	boolean chipsConfirm(String orderNo) throws Exception;

	public DevConsume getUnConOrder(String deviceId) throws Exception;

	public List<DevControl> getDeviceLastStatus(String deviceId) throws Exception;

	public DeviceInfo recoverydevicegrant(String deviceId) throws Exception;

	public boolean checkdevicesuperuser(String deviceId) throws Exception;

	public boolean devRelationAdd(List<DevRelation> dataList) throws Exception;
}