package com.yjyz.iot.security.service;

import java.util.List;

import com.yjyz.iot.security.entity.PowAppInfo;
import com.yjyz.iot.security.entity.PowResource;
import com.yjyz.iot.security.entity.PowRole;
import com.yjyz.iot.security.entity.PowRoleResourceKey;
import com.yjyz.iot.security.entity.PowSysUser;
import com.yjyz.iot.security.entity.PowUserRoleKey;

public interface IPowerService {

	public List<PowAppInfo> login(PowSysUser powSysUser) throws Exception;

	PowAppInfo addApp(PowAppInfo powAppInfo) throws Exception;

	boolean delApp(PowAppInfo powAppInfo) throws Exception;

	boolean updApp(PowAppInfo powAppInfo) throws Exception;

	PowAppInfo selApp(PowAppInfo powAppInfo) throws Exception;

	List<PowAppInfo> selAppAll() throws Exception;

	PowRole addRole(PowRole powRole) throws Exception;

	boolean delRole(PowRole powRole) throws Exception;

	boolean updRole(PowRole powRole) throws Exception;

	PowRole selRole(PowRole powRole) throws Exception;

	List<PowRole> selRoleByApp(String appId) throws Exception;

	boolean addSysUser(PowSysUser powSysUser) throws Exception;

	boolean delSysUser(PowSysUser powSysUser) throws Exception;

	boolean updSysUser(PowSysUser powSysUser) throws Exception;

	PowSysUser selSysUser(PowSysUser powSysUser) throws Exception;

	List<PowSysUser> selSysUserByApp(String appId) throws Exception;

	PowResource addResource(PowResource powResource) throws Exception;

	boolean delResource(PowResource powResource) throws Exception;

	boolean updResource(PowResource powResource) throws Exception;

	PowResource selResource(PowResource powResource) throws Exception;

	List<PowResource> selResourceByApp(String appId) throws Exception;

	boolean addRoleResource(PowRoleResourceKey powRoleResource) throws Exception;

	boolean delRoleResource(PowRoleResourceKey powRoleResource) throws Exception;

	List<PowResource> selRoleResource(PowRole powRole) throws Exception;

	List<PowRole> selResourceRole(PowResource  powResource) throws Exception;

	boolean addUserRole(PowUserRoleKey powUserRole) throws Exception;

	boolean delUserRole(PowUserRoleKey powUserRole) throws Exception;

	List<PowRole> selUserRole(PowUserRoleKey powUserRole) throws Exception;

	List<PowSysUser> selRoleUser(PowUserRoleKey powUserRole) throws Exception;

	List<PowResource> selUserResource(PowSysUser powSysUser) throws Exception;

	List<PowSysUser> selResourceUser(PowResource powResource) throws Exception;

}