package com.yjyz.iot.security.service.impl;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.yjyz.iot.comm.ConstParm;
import com.yjyz.iot.device.dao.DeviceInfoMapper;
import com.yjyz.iot.device.entity.DeviceInfo;
import com.yjyz.iot.notice.dao.NoticeConfigMapper;
import com.yjyz.iot.notice.entity.NoticeConfig;
import com.yjyz.iot.security.dao.PowClientUserMapper;
import com.yjyz.iot.security.dao.PowUserDevMapper;
import com.yjyz.iot.security.dto.UserDevCommDto;
import com.yjyz.iot.security.entity.DeviceInfoQuery;
import com.yjyz.iot.security.entity.PowClientUser;
import com.yjyz.iot.security.entity.PowClientUserQuery;
import com.yjyz.iot.security.entity.PowUserDev;
import com.yjyz.iot.security.entity.PowUserDevKey;
import com.yjyz.iot.security.service.IClientPowerService;
import com.yjyz.iot.security.utils.ClientJwtToken;
import com.yjyz.iot.sms.ISendSMS;
import com.yjyz.iot.sms.SMSResult;

@Service("clientPowerService")
public class ClientPowerServiceImpl implements IClientPowerService {

	@Resource
	PowClientUserMapper powClientUserDao;
	@Resource
	PowUserDevMapper powUserDevDao;
	@Resource
	DeviceInfoMapper deviceInfoDao;
	@Autowired
	private NoticeConfigMapper noticeConfigDao;
	@Autowired
	private ISendSMS sendSMS;

	@Value("${cfg.sys.device_ttl}")
	private long DEV_TTL;

	@Override
	public SMSResult getVerCode(String appId, String tel) throws Exception {
		Random rd = new Random();
		String verCode = String.valueOf(rd.nextInt(8000) + 1000);

		SMSResult rst;
		NoticeConfig pushConfig = this.noticeConfigDao.selectByPrimaryKey(appId);

		if (pushConfig == null) {
			rst = this.sendSMS.sendVerCode(tel, verCode);
		} else {
			rst = this.sendSMS.sendVerCode(pushConfig.getYtxAppid(), pushConfig.getVerTemplateId(), tel, verCode);
		}
		rst.setVerCode(verCode);
		return rst;
	}

	@Override
	public boolean userReg(PowClientUser user) throws Exception {
		int ret = 0;
		PowClientUser powClientUser = this.powClientUserDao.findUserByTel(user.getTel());
		if (powClientUser == null) {
			String uuid = UUID.randomUUID().toString();
			user.setClientId(uuid);
			user.setOpenid(uuid);
			user.setRemark(ConstParm.CLIENT_USER_TYPENAME);
			user.setType(ConstParm.CLIENT_USER_TYPE);
			ret = this.powClientUserDao.insert(user);
		} else {
			powClientUser.setPassword(user.getPassword());
			ret = this.powClientUserDao.updateByPrimaryKeySelective(powClientUser);
		}
		if (ret == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public PowClientUser getUserInfoByClientId(String clientId) throws Exception {
		return this.powClientUserDao.selectByClientId(clientId);
	}

	@Override
	public PowClientUser getUserInfoByTel(String tel) throws Exception {
		return this.powClientUserDao.findUserByTel(tel);
	}

	@Override
	public boolean updateUserInfo(PowClientUser user) throws Exception {
		int ret = this.powClientUserDao.updateByClientIdSelective(user);
		if (ret == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public PowClientUser userLogin(PowClientUser user) throws Exception {
		PowClientUser powClientUser = this.powClientUserDao.findUserByTel(user.getTel());
		if (powClientUser != null) {
			if (powClientUser.getPassword().equals(user.getPassword())) {
				return powClientUser;
			}
		}
		return null;
	}

	@Override
	public boolean bindDevice(PowUserDev userDev) throws Exception {
		int ret = this.powUserDevDao.insert(userDev);
		if (ret == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean unBindDevice(PowUserDev userDev) throws Exception {
		PowUserDevKey key = new PowUserDevKey();
		key.setClientId(userDev.getClientId());
		key.setDeviceId(userDev.getDeviceId());
		int ret = this.powUserDevDao.deleteByPrimaryKey(key);
		if (ret == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean grantDeviceToUser(UserDevCommDto dto, ClientJwtToken token) throws Exception {

		PowUserDevKey key = new PowUserDevKey();
		key.setClientId(token.getUser_id());
		// key.setDeviceId(token.getDevice_id());
		key.setDeviceId(dto.getDeviceId());

		PowUserDev userDev = this.powUserDevDao.selectByPrimaryKey(key);
		if (!userDev.getOwnType()) {
			return false;
		}

		PowClientUser powClientUser = this.powClientUserDao.findUserByTel(dto.getTel());
		if (!powClientUser.getAppId().equals(token.getApp_id())) {
			return false;
		}

		key.setClientId(powClientUser.getClientId());
		key.setDeviceId(dto.getDeviceId());

		userDev = this.powUserDevDao.selectByPrimaryKey(key);
		int ret = 0;
		if (userDev == null) {
			userDev = new PowUserDev();
			userDev.setClientId(powClientUser.getClientId());
			userDev.setDeviceId(dto.getDeviceId());
			userDev.setOwnType(dto.isOwnType());
			ret = this.powUserDevDao.insert(userDev);

		} else {
			userDev.setOwnType(dto.isOwnType());
			ret = this.powUserDevDao.updateByPrimaryKeySelective(userDev);
		}
		if (ret == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<DeviceInfoQuery> getUserDevice(String clientId) throws Exception {
		List<DeviceInfoQuery> devList = this.powUserDevDao.selectUserDev(clientId);
		long nowTime = System.currentTimeMillis();
		for (int i = 0; i < devList.size(); i++) {
			DeviceInfoQuery devInfo = devList.get(i);
			long lastTime = devInfo.getLastTime().getTime();
			if (lastTime + DEV_TTL <= nowTime) {
				devInfo.setIsOnline(false);
			}
		}
		return devList;
	}

	@Override
	public List<PowClientUserQuery> getDeviceUser(String deviceId) throws Exception {
		return this.powUserDevDao.selectDevUser(deviceId);
	}

	@Override
	public boolean updateDevInfo(DeviceInfo info) throws Exception {
		int ret = this.deviceInfoDao.updateByPrimaryKeySelective(info);
		if (ret == 1) {
			return true;
		} else {
			return false;
		}
	}

}