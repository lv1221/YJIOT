package com.yjyz.iot.logistice.action;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yjyz.iot.comm.ConstParm;
import com.yjyz.iot.comm.RetInfoDto;
import com.yjyz.iot.device.dto.DevCommDto;
import com.yjyz.iot.logistice.entity.DevLogisticeInfo;
import com.yjyz.iot.logistice.service.IDevLogisticeInfoService;
import com.yjyz.iot.security.utils.ClientJwtToken;
import com.yjyz.iot.security.utils.ClientJwtUtil;

@RestController
@RequestMapping("/device")
public class LogisticeController {
	@Autowired
	private IDevLogisticeInfoService devLogisticeInfoService;

	private static Log log = LogFactory.getLog(LogisticeController.class);

	@Autowired
	ClientJwtUtil jwtUtil;

	@Transactional
	@RequestMapping(value = "/addLogisticeInfo", method = RequestMethod.POST)
	public RetInfoDto addLogisticeInfo(@RequestBody DevLogisticeInfo dto, @RequestHeader String Authorization) {
		RetInfoDto info = new RetInfoDto();
		ClientJwtToken token;
		try {
			token = jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			DevLogisticeInfo data = this.devLogisticeInfoService.addLogisticeInfo(dto, token.getUser_id());
			if (data != null) {
				info.meta.put("message", "LogisticeInfo save sucess.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			} else {
				info.meta.put("message", "LogisticeInfo save error.");
				info.meta.put("code", ConstParm.ERR_LOGISTICE_ADD);
			}

		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "addLogisticeInfo fail.");
			info.meta.put("code", ConstParm.ERR_LOGISTICE_ADD);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/updLogisticeInfo", method = RequestMethod.POST)
	public RetInfoDto updLogisticeInfo(@RequestBody DevLogisticeInfo dto, @RequestHeader String Authorization) {
		RetInfoDto info = new RetInfoDto();
		try {
			jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			boolean isOk = this.devLogisticeInfoService.updLogisticeInfo(dto);
			if (isOk) {
				info.meta.put("message", "LogisticeInfo update sucess.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("message", "LogisticeInfo update error.");
				info.meta.put("code", ConstParm.ERR_LOGISTICE_UPDATE);
			}

		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "updLogisticeInfo fail.");
			info.meta.put("code", ConstParm.ERR_LOGISTICE_UPDATE);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/selLogisticeInfoByMac", method = RequestMethod.POST)
	public RetInfoDto selLogisticeInfoByMac(@RequestBody DevCommDto dto, @RequestHeader String Authorization) {
		RetInfoDto info = new RetInfoDto();
		try {
			jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			List<DevLogisticeInfo> data = this.devLogisticeInfoService.selectByDeviceMac(dto.getDeviceMac());
			info.meta.put("code", ConstParm.SUCESS_CODE);
			info.data.put("data", data);

		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "selLogisticeInfo fail.");
			info.meta.put("code", ConstParm.ERR_LOGISTICE_SELECT);
		}
		return info;
	}
}