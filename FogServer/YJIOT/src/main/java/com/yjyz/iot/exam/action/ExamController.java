package com.yjyz.iot.exam.action;

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
import com.yjyz.iot.exam.dto.DevExamDto;
import com.yjyz.iot.exam.entity.DevExamDetail;
import com.yjyz.iot.exam.entity.DevExamMain;
import com.yjyz.iot.exam.service.IDevExamService;
import com.yjyz.iot.security.utils.ClientJwtToken;
import com.yjyz.iot.security.utils.ClientJwtUtil;

/**
 * @class :ExamController
 * @TODO :
 * @author:HeroLizhen
 * @date :2017年12月21日上午12:15:00
 */
@RestController
@RequestMapping("/device")
public class ExamController {

	@Autowired
	private IDevExamService devExamService;

	private static Log log = LogFactory.getLog(ExamController.class);

	@Autowired
	ClientJwtUtil jwtUtil;

	/**
	 * @name:startExam
	 * @TODO:移动端调用，通知设备开始检测
	 * @date:2017年12月22日 下午12:21:43
	 * @param dto
	 * @param Authorization
	 * @return RetInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/startExam", method = RequestMethod.POST)
	public RetInfoDto startExam(@RequestBody DevExamDto dto, @RequestHeader String Authorization) {
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
			DevExamMain devExamMain = this.devExamService.startExam(dto, token.getUser_id());
			if (devExamMain == null) {
				info.meta.put("message", "no dvice for this MAC.");
				info.meta.put("code", ConstParm.ERR_NO_DEVINFO);
			} else {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", devExamMain);
			}

		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "startExam fail.");
			info.meta.put("code", ConstParm.ERR_EXAM_STARTEXAM);
		}
		return info;
	}

	/**
	 * @name:getExamResult
	 * @TODO:移动端获取设备的检测结果
	 * @date:2017年12月22日 下午12:21:10
	 * @param dto
	 * @param Authorization
	 * @return RetInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/getExamResult", method = RequestMethod.POST)
	public RetInfoDto getExamResult(@RequestBody DevExamDto dto, @RequestHeader String Authorization) {
		RetInfoDto info = new RetInfoDto();
		try {
			jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			List<DevExamDetail> examDetail = this.devExamService.getExamResult(dto);
			info.meta.put("code", ConstParm.SUCESS_CODE);
			info.data.put("examResult", examDetail);
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "startExam fail.");
			info.meta.put("code", ConstParm.ERR_EXAM_GETEXAMRESULT);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/confirmExamResult", method = RequestMethod.POST)
	public RetInfoDto confirmExamResult(@RequestBody DevExamMain dto, @RequestHeader String Authorization) {
		RetInfoDto info = new RetInfoDto();
		try {
			jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			boolean isOk = this.devExamService.updateExamMain(dto);
			if (isOk) {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", isOk);
			} else {
				info.meta.put("message", "confirmExamResult  fail.");
				info.meta.put("code", ConstParm.ERR_EXAM_CONFIRMEXAMRESULT);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "updateExamMain fail.");
			info.meta.put("code", ConstParm.ERR_EXAM_CONFIRMEXAMRESULT);
		}
		return info;
	}

	/**
	 * @name:saveExamItem
	 * @TODO: 设备端调用，保存检测结果
	 * @date:2017年12月22日 下午12:20:16
	 * @param dto
	 * @return RetInfoDto
	 */
	@RequestMapping(value = "/saveExamItem", method = RequestMethod.POST)
	public RetInfoDto saveExamItem(@RequestBody DevExamDto dto) {
		RetInfoDto info = new RetInfoDto();

		try {
			boolean isOk = this.devExamService.saveExamItem(dto);
			if (isOk) {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", isOk);
			} else {
				info.meta.put("message", "saveExamItem  fail.");
				info.meta.put("code", ConstParm.ERR_EXAM_SAVEEXAMITEM);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "saveExamItem fail.");
			info.meta.put("code", ConstParm.ERR_EXAM_SAVEEXAMITEM);
		}
		return info;
	}

	/**
	 * @name:stopExam
	 * @TODO: 移动端调用，通知设备停止监测，设备收到后，需要上报测试结果。
	 * @date:2017年12月22日 下午12:20:12
	 * @param dto
	 * @param Authorization
	 * @return RetInfoDto
	 */
	@RequestMapping(value = "/stopExam", method = RequestMethod.POST)
	public RetInfoDto stopExam(@RequestBody DevExamMain dto, @RequestHeader String Authorization) {
		RetInfoDto info = new RetInfoDto();
		try {
			jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			boolean isOk = this.devExamService.stopExam(dto);
			if (isOk) {
				info.meta.put("code", ConstParm.SUCESS_CODE);
				info.data.put("data", isOk);
			} else {
				info.meta.put("message", "stopExam  fail.");
				info.meta.put("code", ConstParm.ERR_EXAM_STOPEXM);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "saveExamItem fail.");
			info.meta.put("code", ConstParm.ERR_EXAM_STOPEXM);
		}
		return info;
	}

	/**
	 * @name:getExamList
	 * @TODO: 通过mac获取设备检测主记录
	 * @date:2017年12月25日 下午1:36:34
	 * @param dto
	 * @param Authorization
	 * @return RetInfoDto
	 */
	@Transactional
	@RequestMapping(value = "/getExamList", method = RequestMethod.POST)
	public RetInfoDto getExamList(@RequestBody DevExamDto dto, @RequestHeader String Authorization) {
		RetInfoDto info = new RetInfoDto();
		try {
			jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			List<DevExamMain> examList = this.devExamService.queryExamList(dto);
			info.meta.put("code", ConstParm.SUCESS_CODE);
			info.data.put("examList", examList);
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "getExamList fail.");
			info.meta.put("code", ConstParm.ERR_EXAM_QUERYEXAMlIST);
		}
		return info;
	}

	@Transactional
	@RequestMapping(value = "/confirmExamManu", method = RequestMethod.POST)
	public RetInfoDto confirmExamManu(@RequestBody DevExamDto dto, @RequestHeader String Authorization) {
		RetInfoDto info = new RetInfoDto();
		try {
			jwtUtil.parseToken(Authorization);
		} catch (Exception e) {
			info.meta.put("message", "access token is wrong!");
			info.meta.put("code", ConstParm.ERR_CODE_JWT);
			return info;
		}

		try {
			boolean isOk = this.devExamService.confirmExamManu(dto);
			if (isOk) {
				info.meta.put("code", ConstParm.SUCESS_CODE);
			} else {
				info.meta.put("code", ConstParm.ERR_EXAM_CONFIRMEXAMMANU);
			}
		} catch (Exception e) {
			log.error(e);
			info.meta.put("message", "confirmExamManu fail.");
			info.meta.put("code", ConstParm.ERR_EXAM_CONFIRMEXAMMANU);
		}
		return info;
	}

}