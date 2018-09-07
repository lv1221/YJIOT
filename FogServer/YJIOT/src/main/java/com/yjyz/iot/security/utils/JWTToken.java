package com.yjyz.iot.security.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.TreeMap;

import com.alibaba.fastjson.JSONObject;

import io.jsonwebtoken.Claims;

public class JWTToken {

	private long orig_iat;
	private int user_id;
	private String deviceid;
	private long exp;

	public long getOrig_iat() {
		return orig_iat;
	}

	public void setOrig_iat(long orig_iat) {
		this.orig_iat = orig_iat;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public String getDeviceid() {
		return deviceid;
	}

	public void setDeviceid(String deviceid) {
		this.deviceid = deviceid;
	}

	public long getExp() {
		return exp;
	}

	public void setExp(long exp) {
		this.exp = exp;
	}

	public JWTToken(long orig_iat, int user_id, String deviceid, long exp) {
		this.orig_iat = orig_iat;
		this.user_id = user_id;
		this.deviceid = deviceid;
		this.exp = exp;
	}

	public JWTToken(int user_id, String deviceid) {

		this.user_id = user_id;
		this.deviceid = deviceid;
	}

	public JWTToken() {
	}

	public JSONObject toJSONObject() {
		Field[] fields = this.getClass().getDeclaredFields();
		if (fields.length == 0) {
			return null;
		}
		TreeMap<String, Object> map = new TreeMap<String, Object>();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			int modifier = field.getModifiers();
			if (Modifier.isStatic(modifier) || Modifier.isFinal(modifier)) {
				continue;
			}
			field.setAccessible(true);
			String name = field.getName();
			Object value = null;
			try {
				value = field.get(this);
			} catch (IllegalArgumentException e) {
				continue;
			} catch (IllegalAccessException e) {
				continue;
			}
			map.put(name, value);
		}
		JSONObject object = new JSONObject(map);
		return object;
	}

	public void toSingToken(Claims claims) {
		this.setUser_id((int) claims.get("user_id"));
		this.setOrig_iat((long) claims.get("orig_iat"));
		this.setDeviceid((String) claims.get("deviceid"));
		this.setExp((long) claims.get("exp"));
	}

}