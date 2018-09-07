package com.yjyz.iot.stats.action;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.yjyz.iot.comm.ConstParm;
import com.yjyz.iot.comm.RetInfoDto;
import com.yjyz.iot.security.utils.ClientJwtToken;
import com.yjyz.iot.security.utils.ClientJwtUtil;
import com.yjyz.iot.stats.entity.StatsParm;
import com.yjyz.iot.stats.service.IStatsService;

@RestController
@RequestMapping("/stats")
public class StatsController {
	@Autowired
	private IStatsService statsService;
	@Autowired
	ClientJwtUtil jwtUtil;
	private static Log log = LogFactory.getLog(StatsController.class);

	@Transactional
	@RequestMapping(value = "/devStateType", method = RequestMethod.POST)
	public RetInfoDto devStateType(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.devStateType(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for devStateType stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats devStateType Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_DEVSTATETYPE);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/userRegMonth", method = RequestMethod.POST)
	public RetInfoDto userRegMonth(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.userRegMonth(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for userRegMonth stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats userRegMonth Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_USERREGMONTH);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/fundMonth", method = RequestMethod.POST)
	public RetInfoDto fundMonth(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.fundMonth(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for fundMonth stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats fundMonth Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_FUNDMONTH);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/fundMerChantDay", method = RequestMethod.POST)
	public RetInfoDto fundMerChantDay(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.fundMerChantDay(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for fundMerChantDay stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats fundMerChantDay Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_FUNDMERCHANTDAY);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/fundMerChantWeek", method = RequestMethod.POST)
	public RetInfoDto fundMerChantWeek(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.fundMerChantWeek(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for fundMerChantWeek stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats fundMerChantWeek Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_FUNDMERCHANTWEEK);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/fundTimesDay", method = RequestMethod.POST)
	public RetInfoDto fundTimesDay(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.fundTimesDay(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for fundTimesDay stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats fundTimesDay Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_FUNDTIMESDAY);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/fundTimesWeek", method = RequestMethod.POST)
	public RetInfoDto fundTimesWeek(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.fundTimesWeek(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for fundTimesWeek stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats fundTimesWeek Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_FUNDTIMESWEEK);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/devSeller", method = RequestMethod.POST)
	public RetInfoDto devSeller(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.devSeller(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for devSeller stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats devSeller Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_DEVSELLER);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/devOnlineState", method = RequestMethod.POST)
	public RetInfoDto devOnlineState(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.devOnlineState(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for devOnlineState stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats devOnlineState Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_DEVONLINESTATE);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/fundSellerWeek", method = RequestMethod.POST)
	public RetInfoDto fundSellerWeek(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.fundSellerWeek(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for fundSellerWeek stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats fundSellerWeek Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_FUNDSELLERWEEK);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/fundDevTop15", method = RequestMethod.POST)
	public RetInfoDto fundDevTop15(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.fundDevTop15(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for fundDevTop15 stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats fundDevTop15 Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_FUNDDEVTOP15);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/fundUserTop10", method = RequestMethod.POST)
	public RetInfoDto fundUserTop10(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.fundUserTop10(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for fundUserTop10 stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats fundUserTop10 Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_FUNDUSERTOP10);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/userRegTypeWeek", method = RequestMethod.POST)
	public RetInfoDto userRegTypeWeek(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.userRegTypeWeek(dto);
			if (data.size() == 0) {
				info.meta.put("message", "no data for userRegTypeWeek stats.");
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", data);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats userRegTypeWeek Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_USERREGTYPEWEEK);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/devExamMain", method = RequestMethod.POST)
	public RetInfoDto devExamMain(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.devExamMain(dto);
			info.meta.put("code", ConstParm.SUCESS_CODE);
			info.data.put("data", data);

		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats userRegTypeWeek Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_DEVEXAMMAIN);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/devExamItem", method = RequestMethod.POST)
	public RetInfoDto devExamItem(@RequestHeader String Authorization) {

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
			StatsParm dto = new StatsParm();
			dto.setAppId(jwtToken.getApp_id());
			List<Map<String, Object>> data = this.statsService.devExamItem(dto);
			info.meta.put("code", ConstParm.SUCESS_CODE);
			info.data.put("data", data);

		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "Stats userRegTypeWeek Fail.");
			info.meta.put("code", ConstParm.ERR_STATS_DEVEXAMITEM);
		}
		return info;
	}
}