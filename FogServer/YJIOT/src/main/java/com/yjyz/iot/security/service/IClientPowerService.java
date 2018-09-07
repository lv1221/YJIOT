package com.yjyz.iot.security.service;

import java.util.List;

import com.yjyz.iot.device.entity.DeviceInfo;
import com.yjyz.iot.security.dto.UserDevCommDto;
import com.yjyz.iot.security.entity.DeviceInfoQuery;
import com.yjyz.iot.security.entity.PowClientUser;
import com.yjyz.iot.security.entity.PowClientUserQuery;
import com.yjyz.iot.security.entity.PowUserDev;
import com.yjyz.iot.security.utils.ClientJwtToken;
import com.yjyz.iot.sms.SMSResult;

public interface IClientPowerService {

	SMSResult getVerCode(String appId,String tel) throws Exception;

	boolean userReg(PowClientUser user) throws Exception;

	PowClientUser userLogin(PowClientUser user) throws Exception;

	PowClientUser getUserInfoByClientId(String clientId) throws Exception;

	PowClientUser getUserInfoByTel(String tel) throws Exception;

	boolean updateUserInfo(PowClientUser user) throws Exception;

	boolean bindDevice(PowUserDev userDev) throws Exception;

	public boolean unBindDevice(PowUserDev userDev) throws Exception;

	boolean grantDeviceToUser(UserDevCommDto dto, ClientJwtToken token) throws Exception;

	public List<DeviceInfoQuery> getUserDevice(String clientId) throws Exception;

	public List<PowClientUserQuery> getDeviceUser(String deviceId) throws Exception;

	public boolean updateDevInfo(DeviceInfo info) throws Exception;
}