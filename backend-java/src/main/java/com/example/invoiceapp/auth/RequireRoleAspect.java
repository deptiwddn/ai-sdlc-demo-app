package com.example.invoiceapp.auth;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Method;
import java.util.Arrays;

@Aspect
@Component
public class RequireRoleAspect {
    @Around("@within(com.example.invoiceapp.auth.RequireRole) || @annotation(com.example.invoiceapp.auth.RequireRole)")
    public Object enforceRole(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();
        UserSession session = (UserSession) request.getAttribute("session");
        RequireRole annotation = resolveAnnotation(joinPoint);

        if (session == null || annotation == null || !Arrays.asList(annotation.value()).contains(session.role())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        return joinPoint.proceed();
    }

    private RequireRole resolveAnnotation(ProceedingJoinPoint joinPoint) {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Method method;
        try {
            method = targetClass.getMethod(joinPoint.getSignature().getName(),
                    Arrays.stream(joinPoint.getArgs())
                            .map(arg -> arg == null ? Object.class : arg.getClass())
                            .toArray(Class<?>[]::new));
            RequireRole methodAnnotation = method.getAnnotation(RequireRole.class);
            if (methodAnnotation != null) {
                return methodAnnotation;
            }
        } catch (NoSuchMethodException ignored) {
        }

        return targetClass.getAnnotation(RequireRole.class);
    }
}
