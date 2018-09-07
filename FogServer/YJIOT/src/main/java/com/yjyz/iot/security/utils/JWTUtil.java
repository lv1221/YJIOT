package com.yjyz.iot.security.utils;

import java.util.UUID;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil {

	@Value("${cfg.jwt.id}")
	private String JWT_ID;
	@Value("${cfg.jwt.secret}")
	private String JWT_SECRET;
	@Value("${cfg.jwt.ttl}")
	private long JWT_TTL;

	public SecretKey generalKey() {
		byte[] encodedKey = Base64.decodeBase64(JWT_SECRET);
		SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
		return key;
	}

	public String createJWT(JWTToken token, long delayDayCount) {
		// 定义开始时间
		long nowMillis = System.currentTimeMillis();
		token.setOrig_iat(nowMillis);
		token.setExp(nowMillis + delayDayCount * JWT_TTL);
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		SecretKey key = generalKey();
		// 构建一个Token
		JwtBuilder builder = Jwts.builder();
		builder.setHeaderParam("typ", JWT_ID);
		builder.setClaims(token.toJSONObject());
		builder.signWith(signatureAlgorithm, key);
		return builder.compact();
	}

	public JWTToken parseToken(String jwtStr) throws Exception {
		SecretKey key = generalKey();
		Claims claims = Jwts.parser().setSigningKey(key).parseClaimsJws(jwtStr).getBody();
		JWTToken token = new JWTToken();
		token.toSingToken(claims);
		return token;
	}

	public static synchronized String getUID() {
		UUID uuid = UUID.randomUUID();
		return uuid.toString().replace("-", "");
	}

	public static void main(String[] args) {
		String type[] = { "max", "min", "avg" };
		System.out.println(ArrayUtils.contains(type, "Min".toLowerCase()));

	}

}