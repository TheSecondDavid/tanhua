package com.zhouhao;

import io.jsonwebtoken.*;
import org.junit.Test;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class JWTTest {
    @Test
    public void testCreateToken() throws InterruptedException {
        Map map = new HashMap();
        map.put("id",1);
        map.put("mobile","13800138000");
        //2、使用JWT的工具类生成token
        long now = System.currentTimeMillis();
        System.out.println(now);
        Date date = new Date();
        date.setTime(now + 30000);

        String token = Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, "itcast") //指定加密算法
                .setClaims(map) //写入数据
                .setExpiration(date) //失效时间
                .compact();
        System.out.println(token);
    }

    @Test
    public void testParseToken() {
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJtb2JpbGUiOiIxMzgwMDEzODAwMCIsImlkIjoxLCJleHAiOjE2OTg1NDgxNjJ9.k2Cw0VaxPgV4__F2XhVO1l8964losIhR56RpTQMnANjYmPPM5DxoYwte9Fs6nygo1cMM-m-lTaapi1Y36UePcQ";
        long now = System.currentTimeMillis();
        System.out.println(now);
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey("itcast")
                    .parseClaimsJws(token)
                    .getBody();
            Object id = claims.get("id");
            Object mobile = claims.get("mobile");
            System.out.println(id + "--" + mobile);
        } catch (ExpiredJwtException e) {
            System.out.println("token已过期");
        } catch (SignatureException e) {
            System.out.println("token不合法");
        }
    }
}
