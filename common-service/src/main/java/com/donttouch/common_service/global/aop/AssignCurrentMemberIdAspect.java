package com.donttouch.common_service.global.aop;

import com.donttouch.common_service.global.exception.*;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;

@Aspect
@Component
public class AssignCurrentMemberIdAspect {

    @Before("@annotation(com.donttouch.common_service.global.aop.AssignCurrentMemberId)")
    public void assignMemberId(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs())
                .forEach(arg -> getMethod(arg.getClass(), "setMemberId")
                        .ifPresent(
                                setMemberId -> invokeMethod(arg, setMemberId, getCurrentMemberId())));
    }

    private void invokeMethod(Object arg, Method method, Long currentMemberId) {
        try {
            method.invoke(arg, currentMemberId);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<Method> getMethod(Class<?> clazz, String methodName) {
        try {
            return Optional.of(clazz.getMethod(methodName, Long.class));
        } catch (NoSuchMethodException e) {
            return Optional.empty();
        }
    }

    private Long getCurrentMemberId() {
        return getCurrentMemberIdCheck()
                .orElseThrow(AccessDeniedException::new);
    }

    private Optional<Long> getCurrentMemberIdCheck() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Object principal = authentication.getPrincipal();

        if (authentication == null) {
            return Optional.empty();
        }


        if (principal instanceof String) {
            throw new AuthenticationEntryPointException();
        }

        throw new AuthenticationEntryPointException();
    }
}
