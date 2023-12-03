package com.zhouhao.interceptor;

import com.zhouhao.entity.User;
import com.zhouhao.utils.JwtUtils;
import com.zhouhao.utils.UserHolder;
import io.jsonwebtoken.Claims;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TokenInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println(request.getContextPath());
        System.out.println(request.getRequestURI());
        System.out.println(request.getRequestURL());
        System.out.println(request.getServletPath());

        String authorization = request.getHeader("Authorization");
        boolean b = JwtUtils.verifyToken(authorization);
        if(!b){
            response.setStatus(401);
            return false;
        }

        Claims claims = JwtUtils.getClaims(authorization);
        String id = (String)claims.get("id");
        String mobile = (String) claims.get("mobile");

        User user = new User();
        user.setId(id);
        user.setMobile(mobile);
        UserHolder.set(user);
        return true;
    }
}
