package com.yjyz.iot.security.utils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class ClientJwtUtil {

	@Value("${cfg.client.jwt.id}")
	private String JWT_ID;
	@Value("${cfg.client.jwt.secret}")
	private String JWT_SECRET;
	@Value("${cfg.client.jwt.ttl}")
	private long JWT_TTL;

	public SecretKey generalKey() {
		byte[] encodedKey = Base64.decodeBase64(JWT_SECRET);
		SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
		return key;
	}

	public String createJWT(ClientJwtToken token) {
		// 定义开始时间
		long nowMillis = System.currentTimeMillis();
		token.setOrig_iat(nowMillis);
		token.setExp(nowMillis + JWT_TTL);
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		SecretKey key = generalKey();
		// 构建一个Token
		JwtBuilder builder = Jwts.builder();
		builder.setHeaderParam("typ", JWT_ID);
		builder.setClaims(token.toJSONObject());
		builder.signWith(signatureAlgorithm, key);
		return builder.compact();
	}

	public ClientJwtToken parseToken(String jwtStr) throws Exception {
		SecretKey key = generalKey();
		Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtStr).getBody();
		ClientJwtToken token = new ClientJwtToken();
		token.toSingToken(claims);
		if (token.getExp() < System.currentTimeMillis()) {
			throw new Exception();
		}
		return token;
	}

	public static void main(String[] args) {
		byte[] encodedKey = Base64.encodeBase64("c5fbe3aa-b3a4-11e7-9baf00163e120d98".getBytes());
		System.out.println(new String(encodedKey));
	}

}