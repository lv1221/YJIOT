package com.yjyz.iot.exam.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.yjyz.iot.comm.ConstParm;
import com.yjyz.iot.device.dao.DevControlMapper;
import com.yjyz.iot.device.dao.DeviceInfoMapper;
import com.yjyz.iot.device.entity.DevControl;
import com.yjyz.iot.device.entity.DeviceInfo;
import com.yjyz.iot.exam.dao.DevExamDetailMapper;
import com.yjyz.iot.exam.dao.DevExamMainMapper;
import com.yjyz.iot.exam.dto.DevExamDto;
import com.yjyz.iot.exam.entity.DevExamDetail;
import com.yjyz.iot.exam.entity.DevExamMain;
import com.yjyz.iot.exam.service.IDevExamService;
import com.yjyz.iot.mq.producer.topic.TopicSender;

/**
 * @class :DevTestServiceImpl
 * @TODO :
 * @author:HeroLizhen
 * @date :2017年12月13日下午4:02:19
 */
@Service("devExamService")
public class DevExamServiceImpl implements IDevExamService {
	@Autowired
	private DeviceInfoMapper deviceInfoDao;
	@Autowired
	private DevExamMainMapper devExamMainDao;
	@Autowired
	private DevExamDetailMapper devExamDetailDao;
	@Autowired
	private DevControlMapper devControlDao;
	@Autowired
	private TopicSender topicSender;

	@Override
	public DevExamMain startExam(DevExamDto dto, String userId) throws Exception {
		DeviceInfo devInfo = this.deviceInfoDao.selectByMAC(dto.getDeviceMac());
		if (devInfo == null) {
			return null;
		}
		DevExamMain devExamMain = new DevExamMain();
		devExamMain.setExamId(UUID.randomUUID().toString());
		devExamMain.setDeviceId(devInfo.getDeviceId());
		devExamMain.setType(dto.getType());
		devExamMain.setOpUser(userId);
		this.devExamMainDao.insertSelective(devExamMain);

		DevControl devControl = new DevControl();
		devControl.setId(UUID.randomUUID().toString());
		devControl.setDeviceId(devExamMain.getDeviceId());
		devControl.setOpUser(userId);
		devControl.setCmd("EXAMID");
		devControl.setParm(devExamMain.getExamId());
		this.devControlDao.insertSelective(devControl);

		devControl = new DevControl();
		devControl.setId(UUID.randomUUID().toString());
		devControl.setDeviceId(devExamMain.getDeviceId());
		devControl.setOpUser(userId);
		devControl.setCmd("EXAMTYPE");
		devControl.setParm(devExamMain.getType());
		this.devControlDao.insertSelective(devControl);

		devControl = new DevControl();
		devControl.setId(UUID.randomUUID().toString());
		devControl.setDeviceId(devExamMain.getDeviceId());
		devControl.setOpUser(userId);
		devControl.setCmd("EXAMSTATE");
		devControl.setParm(ConstParm.DEV_EXAM_START);
		this.devControlDao.insertSelective(devControl);

		Map<String, Object> cmap = new HashMap<String, Object>();
		cmap.put("EXAMID", devExamMain.getExamId());
		cmap.put("EXAMTYPE", devExamMain.getType()); // 1表示自动，2表示人工
		cmap.put("EXAMSTATE", ConstParm.DEV_EXAM_START);
		JSONObject jObj = new JSONObject(cmap);

		topicSender.send("c2d." + devExamMain.getDeviceId() + ".commands", jObj.toJSONString());

		return devExamMain;
	}

	/**
	 * @name:getExamResult
	 * @TODO:移动端请求测试结论
	 * @date:2017年12月13日 下午4:37:17
	 * @param DevTestDto
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public List<DevExamDetail> getExamResult(DevExamDto dto) throws Exception {
		return this.devExamDetailDao.selectByExamId(dto.getExamId());
	}

	/**
	 * @name:updateExamMain
	 * @TODO:移动端确认测试结论
	 * @date:2017年12月13日 下午4:37:17
	 * @param devTestMain
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean updateExamMain(DevExamMain devExamMain) throws Exception {
		int ret = this.devExamMainDao.updateByPrimaryKeySelective(devExamMain);
		if (ret == 1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @name:saveExamItem
	 * @TODO:硬件设备调用，测试完毕后上传测试数据，云端并通知移动端进行最终确认WIFI
	 * @date:2017年12月13日 下午4:37:17
	 * @param dto
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean saveExamItem(DevExamDto dto) throws Exception {

		for (int i = 0; i < dto.getExamItems().size(); i++) {
			DevExamDetail detail = dto.getExamItems().get(i);
			detail.setDeviceId(dto.getDeviceId());
			detail.setExamId(dto.getExamId());
			detail.setGuid(UUID.randomUUID().toString());
			this.devExamDetailDao.insertSelective(detail);
		}

		Map<String, Object> cmap = new HashMap<String, Object>();
		cmap.put("EXAMID", dto.getExamId());
		cmap.put("EXAMTYPE", dto.getType()); // 1表示自动，2表示人工
		cmap.put("EXAMSTATE", ConstParm.DEV_EXAM_STOP);
		JSONObject jObj = new JSONObject(cmap);

		topicSender.send("d2c." + dto.getDeviceId() + ".status", jObj.toJSONString());
		return true;
	}

	@Override
	public boolean saveExamItem(String examId, List<DevExamDetail> examItems) throws Exception {
		DevExamMain devExamMain = this.devExamMainDao.selectByPrimaryKey(examId);
		for (int i = 0; i < examItems.size(); i++) {
			DevExamDetail detail = examItems.get(i);
			detail.setDeviceId(devExamMain.getDeviceId());
			detail.setExamId(devExamMain.getExamId());
			detail.setGuid(UUID.randomUUID().toString());
			this.devExamDetailDao.insertSelective(detail);
		}

		Map<String, Object> cmap = new HashMap<String, Object>();
		cmap.put("EXAMID", devExamMain.getExamId());
		cmap.put("EXAMTYPE", devExamMain.getType()); // 1表示自动，2表示人工
		cmap.put("EXAMSTATE", ConstParm.DEV_EXAM_STOP);
		JSONObject jObj = new JSONObject(cmap);

		topicSender.send("d2c." + devExamMain.getDeviceId() + ".status", jObj.toJSONString());
		return true;
	}

	/**
	 * @name:stopExam
	 * @TODO:人工测试时，移动端通知设备，测试步骤执行完毕，设备收到通知后，长传测试数据。
	 * @date:2017年12月13日 下午4:43:44
	 * @param devTestMain
	 * @return boolean
	 * @throws Exception
	 */
	@Override
	public boolean stopExam(DevExamMain devTestMain) throws Exception {

		Map<String, Object> cmap = new HashMap<String, Object>();
		cmap.put("EXAMID", devTestMain.getExamId());
		cmap.put("EXAMTYPE", devTestMain.getType()); // 1表示自动，2表示人工
		cmap.put("EXAMSTATE", ConstParm.DEV_EXAM_STOP);
		JSONObject jObj = new JSONObject(cmap);
		topicSender.send("c2d." + devTestMain.getDeviceId() + ".commands", jObj.toJSONString());
		return true;
	}

	/**
	 * @name:getExamList
	 * @TODO: 根据设备的mac地址获取检测记录
	 * @date:2017年12月25日 下午1:26:05
	 * @param dto
	 * @return List<DevExamMain>
	 */
	@Override
	public List<DevExamMain> queryExamList(DevExamDto dto) throws Exception {
		DeviceInfo devInfo = this.deviceInfoDao.selectByMAC(dto.getDeviceMac());
		if (devInfo == null) {
			return null;
		}
		return this.devExamMainDao.selectByDeviceId(devInfo.getDeviceId());
	}

	/**
	 * @name:confirmExamManu
	 * @TODO:手工检测，确认检测结果
	 * @date:2017年12月25日 下午2:10:40
	 * @param dto
	 * @return
	 * @throws Exception
	 *             boolean
	 */
	public boolean confirmExamManu(DevExamDto dto) throws Exception {
		DevExamMain examMian = this.devExamMainDao.selectByPrimaryKey(dto.getExamId());
		if (examMian == null) {
			return false;
		}
		examMian.setIsPass(dto.getIsPass());
		examMian.setMemo(dto.getMemo());
		int ret = this.devExamMainDao.updateByPrimaryKeySelective(examMian);
		if (ret != 1) {
			return false;
		}

		for (int i = 0; i < dto.getExamItems().size(); i++) {
			this.devExamDetailDao.updateByPrimaryKeySelective(dto.getExamItems().get(i));
		}
		return true;

	}

}