package com.mvc.crud.Interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.mvc.crud.model.Friend;
import com.mvc.crud.repository.FriendRepository;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AutoLoginInterceptor implements HandlerInterceptor {

    @Autowired
    FriendRepository friendRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Friend current = (Friend) session.getAttribute("currentFriend");

        if (current == null) {
            if (request.getCookies() != null) {
                for (Cookie c : request.getCookies()) {
                    if ("userMobile".equals(c.getName())) {
                        Friend friend = friendRepository.findByMobile(c.getValue());
                        if (friend != null) {
                            session.setAttribute("currentFriend", friend);
                        }
                    }
                }
            }
        }

        return true;
    }
}

