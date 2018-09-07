package com.yjyz.iot.share.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yjyz.iot.comm.ConstParm;
import com.yjyz.iot.comm.RetInfoDto;
import com.yjyz.iot.device.entity.DevAccount;
import com.yjyz.iot.device.entity.DevConsume;
import com.yjyz.iot.device.entity.DevControl;
import com.yjyz.iot.device.entity.DevRelation;
import com.yjyz.iot.device.entity.DeviceInfo;
import com.yjyz.iot.device.service.IDevRealTimeService;
import com.yjyz.iot.mq.producer.topic.TopicSender;
import com.yjyz.iot.notice.service.INoticeService;
import com.yjyz.iot.ota.entity.OTAFileInfo;
import com.yjyz.iot.ota.entity.OTAUpdateLog;
import com.yjyz.iot.security.utils.JWTToken;
import com.yjyz.iot.security.utils.JWTUtil;
import com.yjyz.iot.share.dto.ActivateDto;
import com.yjyz.iot.share.dto.ChipsConfirmDto;
import com.yjyz.iot.share.dto.ClientCommDto;
import com.yjyz.iot.share.dto.OTACheckDto;
import com.yjyz.iot.share.dto.OTAFileInfoDto;
import com.yjyz.iot.share.dto.OTAUpdateLogDto;
import com.yjyz.iot.share.dto.RecoveryDto;
import com.yjyz.iot.share.dto.SyncStatusDto;
import com.yjyz.iot.share.dto.TokenAuth;
import com.yjyz.iot.share.service.IMX3165Service;

/**
 * @class :DeviceController
 * @TODO :基本功能已经有了，需要增加复杂token的验证
 * @author:Herolizhen
 * @date :2017年9月14日下午4:27:28
 */

@CrossOrigin
@RestController
@RequestMapping("/device")
public class MX3165Controller {

	private static Log log = LogFactory.getLog(MX3165Controller.class);

	@Autowired
	private IMX3165Service mx3165Service;
	@Autowired
	private IDevRealTimeService devRealTimeService;
	@Autowired
	private TopicSender topicSender;
	@Autowired
	INoticeService noticeService;
	@Autowired
	JWTUtil jwtUtil;

	/**
	 * @name:activate
	 * @TODO: 设备初始化的时候激活,同步增加设备台账记录
	 * @date:2017年9月14日 下午4:32:55
	 * @param activate
	 * @return RetDataInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/activate", method = RequestMethod.POST)
	public RetInfoDto activate(@RequestBody ActivateDto dto, HttpServletRequest request) {
		log.debug("product_id:	" + dto.product_id);
		log.debug("mac:	" + dto.mac);
		log.debug("devicepw:	" + dto.devicepw);
		log.debug("mxchipsn:	" + dto.mxchipsn);

		RetInfoDto info = new RetInfoDto();
		try {
			String ip = request.getHeader("X-Forwarded-For");
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("WL-Proxy-Client-IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_CLIENT_IP");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getHeader("HTTP_X_FORWARDED_FOR");
			}
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
				ip = request.getRemoteAddr();
			}
			// log.error("mxchipsn: " + ip);
			ip = ip.split(",")[0];
			dto.setRegIp(ip);
			DeviceInfo devInfo = this.mx3165Service.activate(dto);
			info.meta.put("message", "device activate Success.");
			info.meta.put("code", ConstParm.SUCESS_CODE);
			info.data.put("deviceid", devInfo.getDeviceId());
		} catch (Exception e) {
			log.info(e);
			info.meta.put("message", "device activate error.");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_ACTIVE);
		}
		return info;
	}

	/**
	 * @name:tokenAuth
	 * @TODO:token验证
	 * @date:2017年9月14日 下午4:34:57
	 * @param tokenAuth
	 * @return RetDataInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/token-auth", method = RequestMethod.POST)
	public RetInfoDto tokenAuth(@RequestBody TokenAuth tokenAuth) {
		log.debug("deviceid:	" + tokenAuth.Deviceid);
		log.debug("password:	" + tokenAuth.Password);

		RetInfoDto info = new RetInfoDto();

		try {
			DeviceInfo devInfo = this.mx3165Service.getDeviceByID(tokenAuth.Deviceid);
			if (devInfo == null) {
				info.meta.put("message", "DeviceID  is not exist!");
				info.meta.put("code", ConstParm.ERR_SHARE_MX3165_TOKENAUTH);
				return info;
			}

			if (!devInfo.getDevicePw().equals(tokenAuth.Password)) {
				info.meta.put("message", "DeviceID  password wrong!");
				info.meta.put("code", ConstParm.ERR_SHARE_MX3165_PASSWORD);
				return info;
			}

			JWTToken devToken = new JWTToken(35429, devInfo.getDeviceId());
			String token = jwtUtil.createJWT(devToken, 1);
			info.meta.put("message", "device jwt response success");
			info.meta.put("code", ConstParm.SUCESS_CODE);
			info.data.put("token", token);
			return info;
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "token auth error.");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_TOKENAUTH);
			return info;
		}
	}

	/**
	 * @name:syncStatus
	 * @TODO: 应间端同步设备固件信息JWT认证
	 * @date:2017年9月14日 下午4:35:25
	 * @param syncStatus
	 * @param Authorization
	 * @return RetDataInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/syncstatus", method = RequestMethod.POST)
	public RetInfoDto syncStatus(@RequestBody SyncStatusDto syncStatus, @RequestHeader String Authorization) {
		log.debug("product_id:	" + syncStatus.getProduct_id());
		log.debug("moduletype:	" + syncStatus.getModuletype());
		log.debug("firmware:	" + syncStatus.getFirmware());
		log.debug("mico:	" + syncStatus.getMico());
		RetInfoDto info = new RetInfoDto();

		JWTToken token;
		try {
			token = jwtUtil.parseToken(Authorization.split(" ")[1]);
		} catch (Exception e) {
			info.meta.put("message", "get token error!");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_TOKEN_ERROR);
			return info;
		}

		try {
			DeviceInfo devInfo = new DeviceInfo();
			devInfo.setDeviceId(token.getDeviceid());
			devInfo.setModuleName(syncStatus.getModuletype());
			devInfo.setFirmware(syncStatus.getFirmware());
			devInfo.setIotVersion(syncStatus.getMico());

			this.mx3165Service.updateDeviceInfo(devInfo);

			info.meta.put("message", "Device Status Sync succeed");
			info.meta.put("code", ConstParm.SUCESS_CODE);
			info.data.put("UpdateDeviceStatus", true);
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "sync status error.");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_SYNCSTATUS);
		}
		return info;
	}

	/**
	 * @name:updatecheck
	 * @TODO:硬件端检查更新固件
	 * @date:2017年9月14日 下午4:36:02
	 * @param checkOTA
	 * @return RetDataInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/updatecheck", method = RequestMethod.POST)
	public RetInfoDto updatecheck(@RequestBody OTACheckDto checkDto) {
		log.debug("updatecheck exec.");
		log.debug("Product_id:	" + checkDto.getProduct_id());
		log.debug("Modulename:	" + checkDto.getModulename());
		log.debug("Firmware:	" + checkDto.getFirmware());
		log.debug("Firmware_type:	" + checkDto.getFirmware_type());
		RetInfoDto info = new RetInfoDto();

		try {
			OTAFileInfo checkOTA = new OTAFileInfo();
			checkOTA.setProductId(checkDto.getProduct_id());
			checkOTA.setModulename(checkDto.getModulename());
			checkOTA.setFirmware(checkDto.getFirmware());

			OTAFileInfo otaFile = this.mx3165Service.selectByOTACheck(checkOTA);
			if (otaFile != null) {
				int clientVer = Integer.valueOf(checkOTA.getFirmware().split("@")[1]);
				int serverVer = Integer.valueOf(otaFile.getVersion());
				if (clientVer < serverVer) {
					OTAFileInfoDto dto = new OTAFileInfoDto();
					BeanUtils.copyProperties(otaFile, dto);
					DateFormat sdf_ut = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.FFFFFF");
					DateFormat sdf_et = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					dto.setEffect_time(sdf_et.format(otaFile.getEffectTime()));
					dto.setUpload_time(sdf_ut.format(otaFile.getUploadTime()));
					dto.setId(otaFile.getId());
					dto.setOtaid(otaFile.getOtaId());
					dto.setProductid(otaFile.getProductId());
					dto.setUpgrade(otaFile.getUpgrade());
					dto.setVerson(otaFile.getVersion());
					info.meta.put("message", "latest version");
					info.meta.put("code", ConstParm.SUCESS_CODE);
					info.data.put("OTAstatus", "True");
					info.data.put("File", dto.toJSONObject());
				} else {
					info.meta.put("message", "No update exists");
					info.meta.put("code", ConstParm.ERR_SHARE_MX3165_OTA_NO_UPDATE);
					info.data.put("OTAstatus", "False");
					info.data.put("File", "{}");
				}
			} else {
				info.meta.put("message", "No update exists");
				info.meta.put("code", ConstParm.ERR_SHARE_MX3165_OTA_NO_UPDATE);
				info.data.put("OTAstatus", "False");
				info.data.put("File", "{}");
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "update check error.");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_UPDATECHECK);
		}
		return info;

	}

	/**
	 * @name:otauploadlog
	 * @TODO:硬件端更新固件更新信息 JWT认证
	 * @date:2017年9月14日 下午4:36:31
	 * @param updateLog
	 * @param Authorization
	 * @return RetDataInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/otauploadlog", method = RequestMethod.POST)
	public RetInfoDto otauploadlog(@RequestBody OTAUpdateLogDto updateLog, @RequestHeader String Authorization) {
		log.debug("otauploadlog exec .");
		log.debug("Deviceid:	" + updateLog.getDeviceid());
		log.debug("Software:	" + updateLog.getSoftware());
		log.debug("Modulename:	" + updateLog.getModulename());

		RetInfoDto info = new RetInfoDto();
		try {
			jwtUtil.parseToken(Authorization.split(" ")[1]);
		} catch (Exception e) {
			info.meta.put("message", "get token error!");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_TOKEN_ERROR);
			return info;
		}

		try {
			OTAUpdateLog log = new OTAUpdateLog();
			log.setDeviceId(updateLog.getDeviceid());
			log.setSoftware(updateLog.getSoftware());
			log.setModulename(updateLog.getModulename());
			log.setSoftware(updateLog.getSoftware());
			log.setUpdateTime(new Date());

			this.mx3165Service.saveOTAUpdateLog(log);
			info.meta.put("message", "Device Status Sync succeed");
			info.meta.put("code", ConstParm.SUCESS_CODE);
			info.data.put("UpdateDeviceStatus", true);
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "otauploadlog Fail.");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_OTAUPLOADLOG);
			info.data.put("UpdateDeviceStatus", false);
		}

		return info;

	}

	/**
	 * @name:checkdevicesuperuser
	 * @TODO:硬件端检查设备高级用户 JWT认证
	 * @date:2017年9月14日 下午4:37:01
	 * @param Authorization
	 * @return RetDataInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/checkdevicesuperuser", method = RequestMethod.GET)
	public RetInfoDto checkdevicesuperuser(@RequestHeader String Authorization) {
		log.debug("checkdevicesuperuser exec.");

		RetInfoDto info = new RetInfoDto();
		JWTToken token;
		try {
			token = jwtUtil.parseToken(Authorization.split(" ")[1]);
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "get token error!");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_TOKEN_ERROR);
			return info;
		}

		try {
			boolean isSuper = this.mx3165Service.checkdevicesuperuser(token.getDeviceid());
			if (isSuper) {
				info.meta.put("message", "Check Device Superuser sucess.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("CheckDeviceSuperUser", true);
			} else {
				info.meta.put("message", "Check Device Superuser sucess.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("CheckDeviceSuperUser", false);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Check Device Superuser error!");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_TOKEN_ERROR);
		}
		return info;
	}

	/**
	 * 暂未使用
	 * 
	 * @name:generatedevicevercode
	 * @TODO:硬件端调用，获取设备验证码
	 * @date:2017年9月14日 下午4:37:21
	 * @return RetDataInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/generatedevicevercode", method = RequestMethod.POST)
	public RetInfoDto generatedevicevercode() {
		log.debug("generatedevicevercode exec.");
		RetInfoDto info = new RetInfoDto();
		info.meta.put("message", "Generate Device Vercode Success.");
		info.meta.put("code", ConstParm.SUCESS_CODE);
		info.data.put("vercode", "492f0f70-d2cb-11e5-a739-00163e0204c0");
		return info;
	}

	/**
	 * 设备解绑，使用
	 * 
	 * @name:recoverydevicegrant
	 * @TODO:硬件端获取设备恢复出厂设置权限
	 * @date:2017年9月14日 下午4:37:38
	 * @param recoveryDto
	 * @return RetDataInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/recoverydevicegrant", method = RequestMethod.POST)
	public RetInfoDto recoverydevicegrant(@RequestBody RecoveryDto recoveryDto, @RequestHeader String Authorization) {

		log.debug("recoverydevicegrant exec.");
		log.debug("Deviceid:	" + recoveryDto.getDeviceid());

		RetInfoDto info = new RetInfoDto();

		try {
			jwtUtil.parseToken(Authorization.split(" ")[1]);
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "get token error!");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_TOKEN_ERROR);
			return info;
		}

		try {
			DeviceInfo devInfo = this.mx3165Service.recoverydevicegrant(recoveryDto.getDeviceid());

			if (devInfo != null) {
				info.meta.put("message", "recoverydevicegrant sucess.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("CheckDeviceSuperUser", true);
			} else {
				info.meta.put("message", "recoverydevicegrant error.");
				info.meta.put("code", ConstParm.ERR_SHARE_MX3165_TOKEN_ERROR);
				info.data.put("recoverydevicegrant", false);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "get token error!");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_RECOVERDEVICEGRANT);
			info.data.put("recoverydevicegrant", false);
		}

		return info;
	}

	/**
	 * 暂未使用
	 * 
	 * @name:servertime
	 * @TODO:设备端同步时间
	 * @date:2017年9月14日 下午4:37:57
	 * @return RetDataInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/servertime", method = RequestMethod.GET)
	public RetInfoDto servertime() {
		RetInfoDto info = new RetInfoDto();
		info.meta.put("message", "get server time ok.");
		info.meta.put("code", 0);
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String datetime1 = sdf.format(date);
		info.data.put("datetime", datetime1);
		sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
		String datetime2 = sdf.format(date);
		info.data.put("datetime1", datetime2);
		// info.data.put("timestamp", date.getTime());
		info.data.put("timestamp", String.valueOf(date.getTime()));
		sdf = new SimpleDateFormat("EEE", Locale.US);
		String week = sdf.format(date);
		info.data.put("week", week);
		sdf = new SimpleDateFormat("EEE");
		week = sdf.format(date);
		info.data.put("week1", week);
		return info;
	}

	/**
	 * @name:saveRealData
	 * @TODO:硬件端，发送设备实时数据JWT认证
	 * @date:2017年9月14日 下午4:38:43
	 * @param devRealDataInfo
	 * @param Authorization
	 * @return RetDataInfoDto
	 */
	// @Transactional
	@RequestMapping(value = "/sendeventadv", method = RequestMethod.POST, consumes = "application/json;charset=UTF-8", produces = "application/json;charset=UTF-8")
	public RetInfoDto saveRealData(@RequestBody ClientCommDto clientCommDto, @RequestHeader String Authorization) {
		log.debug("sendeventadv exec.");
		log.debug(clientCommDto.getPayload());

		RetInfoDto info = new RetInfoDto();
		String deviceId;
		try {
			JWTToken token = jwtUtil.parseToken(Authorization.split(" ")[1]);
			deviceId = token.getDeviceid();
		} catch (Exception e) {
			info.meta.put("message", "token Fail.");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_TOKEN_ERROR);
			return info;
		}
		try {
			// 保存实时数据
			if (clientCommDto.getFlag() == 1) {
				// 从服务器获取设备信息
				int hasTable = this.devRealTimeService.existTable(deviceId);
				if (hasTable == 0) {
					this.devRealTimeService.createTable(deviceId, clientCommDto.getPayload());
				}
				this.devRealTimeService.insertData(deviceId, clientCommDto.getPayload());
			}
			// 更新设备在线时间
			DevAccount devAccount = new DevAccount();
			devAccount.setDeviceId(deviceId);
			devAccount.setLastTime(new Date(System.currentTimeMillis()));
			devAccount.setIsOnline(true);
			this.mx3165Service.updateDevAccount(devAccount);

			topicSender.send("d2c." + deviceId + ".status", clientCommDto.getPayload());

			info.meta.put("message", "Real Data Save Success.");
			info.meta.put("code", ConstParm.SUCESS_CODE);
			// 返回订单数据
			DevConsume devConsume = this.mx3165Service.getUnConOrder(deviceId);
			if (devConsume != null) {
				info.data.put("orderFlag", ConstParm.DICT_SHARE_MX3165_DATA);
				Map<String, Object> orderInfo = new HashMap<String, Object>();
				orderInfo.put("chips", devConsume.getChips());
				orderInfo.put("type", 1);
				orderInfo.put("orderNo", devConsume.getOrderNo());
				orderInfo.put("status", devConsume.getStatus());
				info.data.put("orderInfo", orderInfo);
			} else {
				info.data.put("orderFlag", ConstParm.DICT_SHARE_MX3165_NODATA);
			}

			// 获取最新状态

			List<DevControl> conList = this.mx3165Service.getDeviceLastStatus(deviceId);

			if (conList.size() == 0) {
				info.data.put("statusFlag", ConstParm.DICT_SHARE_MX3165_NODATA);
			} else {
				info.data.put("statusFlag", ConstParm.DICT_SHARE_MX3165_DATA);
				Map<String, Object> statusInfo = new HashMap<String, Object>();
				for (int i = 0; i < conList.size(); i++) {
					DevControl dc = conList.get(i);
					try {
						statusInfo.put(dc.getCmd(), Integer.parseInt(dc.getParm().toString()));
					} catch (Exception e) {
						statusInfo.put(dc.getCmd(), dc.getParm());
					}
				}
				info.data.put("statusInfo", statusInfo);
			}
			// 发送通知
			if (clientCommDto.getNoticeType() != 0) {
				noticeService.noticeSend(deviceId, clientCommDto.getNoticeType(), clientCommDto.getPushNo());
			}

		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "saveRealData Fail.");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_SENDREALDATA);
			return info;
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/chipsConfirm", method = RequestMethod.POST)
	public RetInfoDto chipsConfirm(@RequestBody ChipsConfirmDto chipsConfirmDto, @RequestHeader String Authorization) {
		log.debug("chipsConfirm exec.");
		log.debug(chipsConfirmDto.getOrderNo());
		RetInfoDto info = new RetInfoDto();

		try {
			jwtUtil.parseToken(Authorization.split(" ")[1]);
		} catch (Exception e) {
			info.meta.put("message", "get token error!");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_TOKEN_ERROR);
			return info;
		}

		try {
			boolean isOk = this.mx3165Service.chipsConfirm(chipsConfirmDto.getOrderNo());
			if (isOk) {
				info.meta.put("message", "ChipsConfirm update succeed");
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("ChipsConfirm", true);
			} else {
				info.meta.put("message", "ChipsConfirm fail no Data");
				info.meta.put("code", ConstParm.ERR_SHARE_MX3165_CHIPSCONFIRM);
				info.data.put("ChipsConfirm", false);
			}

		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "ChipsConfirm fail no Data");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_CHIPSCONFIRM);
			info.data.put("ChipsConfirm", false);
		}

		return info;
	}

	@Transactional
	@RequestMapping(value = "/devRelationAdd", method = RequestMethod.POST)
	public RetInfoDto devRelationAdd(@RequestBody List<DevRelation> dataList, @RequestHeader String Authorization) {
		log.debug("devRelationAdd exec.");
		RetInfoDto info = new RetInfoDto();

		try {
			jwtUtil.parseToken(Authorization.split(" ")[1]);
		} catch (Exception e) {
			info.meta.put("message", "get token error!");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_TOKEN_ERROR);
			return info;
		}

		try {
			boolean isOk = this.mx3165Service.devRelationAdd(dataList);
			if (isOk) {
				info.meta.put("message", "DevRelationAdd succeed");
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("DevRelationAdd", true);
			} else {
				info.meta.put("message", "DevRelationAdd fail no Data");
				info.meta.put("code", ConstParm.ERR_SHARE_MX3165_DEVRELATIONADD);
				info.data.put("DevRelationAdd", false);
			}

		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "DevRelationAdd fail no Data");
			info.meta.put("code", ConstParm.ERR_SHARE_MX3165_DEVRELATIONADD);
			info.data.put("DevRelationAdd", false);
		}
		return info;
		// List<DevRelation> dataList = new ArrayList<DevRelation>();
		// DevRelation data = new DevRelation();
		// data.setDeviceId("123123123");
		// data.setDeviceIdChild("bbbbbbb");
		// data.setGate(1);
		// dataList.add(data);
		// data = new DevRelation();
		// data.setDeviceId("22222222222");
		// data.setDeviceIdChild("ccccccccccc");
		// data.setGate(2);
		// dataList.add(data);
		// return dataList;
	}
}