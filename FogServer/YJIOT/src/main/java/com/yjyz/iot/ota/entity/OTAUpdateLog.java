package com.yjyz.iot.ota.entity;

import java.util.Date;

public class OTAUpdateLog {
	private String id;

	private String deviceId;

	private String software;

	private String modulename;

	private Date updateTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id == null ? null : id.trim();
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId == null ? null : deviceId.trim();
	}

	public String getSoftware() {
		return software;
	}

	public void setSoftware(String software) {
		this.software = software == null ? null : software.trim();
	}

	public String getModulename() {
		return modulename;
	}

	public void setModulename(String modulename) {
		this.modulename = modulename == null ? null : modulename.trim();
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}