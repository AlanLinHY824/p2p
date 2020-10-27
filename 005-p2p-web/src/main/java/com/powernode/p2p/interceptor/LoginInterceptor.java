package com.powernode.p2p.interceptor;

import com.powernode.p2p.constants.MyConstants;
import com.powernode.p2p.model.UUser;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author AlanLin
 * @Description
 * @Date 2020/10/15
 */
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UUser userVo = (UUser)request.getSession().getAttribute(MyConstants.USER_SESSION);
        String redictURL = request.getRequestURL().toString()+(request.getQueryString()==null?"":"?"+request.getQueryString());
        System.out.println("拦截器");
        if (userVo==null){
            request.getRequestDispatcher("/loan/page/login?redictURL="+redictURL).forward(request, response);
//            response.sendRedirect(request.getContextPath()+"/loan/page/login");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
