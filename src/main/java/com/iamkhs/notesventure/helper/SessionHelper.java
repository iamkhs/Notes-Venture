package com.iamkhs.notesventure.helper;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SessionHelper {

    public void removeSession(){
        HttpSession requestAttributes = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
        requestAttributes.removeAttribute("message");
    }
}
