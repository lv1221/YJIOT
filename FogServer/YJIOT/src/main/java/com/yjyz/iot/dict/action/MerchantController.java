package com.yjyz.iot.dict.action;

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
import com.yjyz.iot.comm.GISQuery;
import com.yjyz.iot.comm.RetInfoDto;
import com.yjyz.iot.dict.entity.DictMerchant;
import com.yjyz.iot.dict.service.IDictService;
import com.yjyz.iot.security.utils.ClientJwtToken;
import com.yjyz.iot.security.utils.ClientJwtUtil;

/**
 * 
 * @class :ChargeController
 * @TODO :
 * @author:Herolizhen
 * @date :2017年11月7日下午5:35:35
 */
@RestController
@RequestMapping("/dict")
public class MerchantController {
	private static Log log = LogFactory.getLog(MerchantController.class);
	@Autowired
	private IDictService dictService;
	@Autowired
	ClientJwtUtil jwtUtil;

	@Transactional
	@RequestMapping(value = "/addMerchant", method = RequestMethod.POST)
	public RetInfoDto addMerchant(@RequestBody DictMerchant dto, @RequestHeader String Authorization) {

		RetInfoDto info = new RetInfoDto();
		ClientJwtToken jwtToken;
		try {
			jwtToken = jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			dto.setAppId(jwtToken.getApp_id());
			DictMerchant dictMerchant = this.dictService.addMerchant(dto);
			if (dictMerchant != null) {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("dictMerchant", dictMerchant);
			} else {
				info.meta.put("code", ConstParm.ERR_INSERT);
				info.meta.put("message", "addMerchant fail.");
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "addMerchant fail.");
			info.meta.put("code", ConstParm.ERR_INSERT);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/delMerchant", method = RequestMethod.POST)
	public RetInfoDto delMerchant(@RequestBody DictMerchant dto, @RequestHeader String Authorization) {

		RetInfoDto info = new RetInfoDto();
		try {
			jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			boolean isOk = this.dictService.delMerchant(dto);
			if (isOk) {
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.ERR_DELETE);
				info.meta.put("message", "delMerchant fail.");
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "delMerchant fail.");
			info.meta.put("code", ConstParm.ERR_DELETE);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/updMerchant", method = RequestMethod.POST)
	public RetInfoDto updMerchant(@RequestBody DictMerchant dto, @RequestHeader String Authorization) {

		RetInfoDto info = new RetInfoDto();
		try {
			jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			boolean isOk = this.dictService.updMerchant(dto);
			if (isOk) {
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.ERR_UPDATE);
				info.meta.put("message", "updMerchant fail.");
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "updMerchant fail.");
			info.meta.put("code", ConstParm.ERR_UPDATE);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/selMerchantByName", method = RequestMethod.POST)
	public RetInfoDto selMerchantByName(@RequestBody DictMerchant dto, @RequestHeader String Authorization) {

		RetInfoDto info = new RetInfoDto();
		ClientJwtToken jwtToken;
		try {
			jwtToken = jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			List<DictMerchant> merchantList = this.dictService.selMerchantByName(dto.getName(),jwtToken.getApp_id());
			if (merchantList.size() > 0) {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("merchantList", merchantList);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.meta.put("message", "selMerchantByName no data.");
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "selMerchantByName fail.");
			info.meta.put("code", ConstParm.ERR_SELECT);
		}
		return info;
	}

	/**
	 * @name:selMerchantAll
	 * @TODO:
	 * @date:2018年1月22日 下午11:39:03
	 * @param Authorization
	 * @return RetInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/selMerchantAll", method = RequestMethod.POST)
	public RetInfoDto selMerchantAll(@RequestHeader String Authorization) {

		RetInfoDto info = new RetInfoDto();
		ClientJwtToken jwtToken;
		try {
			jwtToken = jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			List<DictMerchant> merchantList = this.dictService.selMerchantAll(jwtToken.getApp_id());
			if (merchantList.size() > 0) {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("merchantList", merchantList);
			} else {
				//info.meta.put("code", ConstParm.ERR_NO_MERCHANT);
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.meta.put("message", "selMerchantByName no data.");
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "selMerchantAll fail.");
			info.meta.put("code", ConstParm.ERR_SELECT);
		}
		return info;
	}

	/**
	 * @name:selectByGisRect
	 * @TODO:
	 * @date:2018年1月22日 下午4:00:15
	 * @param dto
	 * @param Authorization
	 * @return RetInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/selMerchantByGisRect", method = RequestMethod.POST)
	public RetInfoDto  selMerchantByGisRect(@RequestBody GISQuery dto, @RequestHeader String Authorization) {
		RetInfoDto info = new RetInfoDto();
		ClientJwtToken jwtToken;
		try {
			jwtToken = jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			dto.setAppId(jwtToken.getApp_id());
			List<DictMerchant> list = this.dictService.selectByGISRect(dto);
			info.meta.put("code", ConstParm.SUCESS_CODE);
			info.data.put("data", list);
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "selectByGISRect fail.");
			info.meta.put("code", ConstParm.ERR_SELECT);
		}
		return info;
	}

}