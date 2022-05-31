package com.kqinfo.universal.log.aspect;

import com.kqinfo.universal.ip.BrowserUtil;
import com.kqinfo.universal.ip.IpGeoInfo;
import com.kqinfo.universal.ip.IpGeoUtil;
import com.kqinfo.universal.ip.IpUtil;
import com.kqinfo.universal.log.annotation.LogRecord;
import com.kqinfo.universal.log.domain.OperateLog;
import com.kqinfo.universal.log.domain.Operator;
import com.kqinfo.universal.log.parser.LogRecordExpressionEvaluator;
import com.kqinfo.universal.log.service.LogRecordService;
import com.kqinfo.universal.log.service.OperatorService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.TypedValue;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Zijian Liao
 * @since 1.11.0
 */
@Slf4j
@Aspect
public class LogAspect {

    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    private static final LogRecordExpressionEvaluator parser = new LogRecordExpressionEvaluator();

    @Resource
    private OperatorService operatorService;

    @Resource
    private LogRecordService logRecordService;

    @Around(value = "@annotation(logRecord)")
    public Object log(ProceedingJoinPoint joinPoint, LogRecord logRecord) throws Throwable {
        final Object proceed = joinPoint.proceed();
        // 记录日志
        try {
            doLog(joinPoint, logRecord);
        } catch (Exception e) {
            String message = e.getMessage();
            log.error("操作日志记录失败：{}", e.getMessage(), e);
            OperateLog operateLog = new OperateLog();
            operateLog.setUserId("systemerror")
                    .setUsername("systemerror")
                    .setContent(message.length() > 255 ? message.substring(0, 255) : message)
                    .setBusinessNo("systemerror")
                    .setCategory("systemerror")
                    .setOperateTime(LocalDateTime.now());
            logRecordService.record(operateLog);
        }
        return proceed;
    }

    private void doLog(ProceedingJoinPoint joinPoint, LogRecord logRecord) {
        OperateLog operateLog = new OperateLog();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Operator operator = this.getOperator(joinPoint, request, logRecord);
        operateLog.setUserId(operator.getUserId());
        operateLog.setUsername(operator.getUsername());
        String content = getContent(joinPoint, logRecord);
        operateLog.setContent(content);
        operateLog.setCategory(logRecord.category());
        operateLog.setBusinessNo(getBusinessNo(joinPoint, logRecord));
        operateLog.setOperateTime(LocalDateTime.now());
        String ipAddress = IpUtil.getIpAddress(request);
        operateLog.setIpAddress(ipAddress);
        IpGeoInfo geo = IpGeoUtil.getGeo(ipAddress);
        if (geo != null) {
            operateLog.setGeo(geo.getCountry() + geo.getRegion() + geo.getCity() + geo.getIsp());
        }
        String browser = BrowserUtil.getBrowser(request);
        operateLog.setBrowser(browser);
        operateLog.setParam(this.getParam(joinPoint, logRecord));
        logRecordService.record(operateLog);
    }

    private String getParam(ProceedingJoinPoint joinPoint, LogRecord logRecord) {
        String param = logRecord.param();
        if (param.contains("#")) {
            return parseExpression(joinPoint, param);
        }
        return param;
    }


    private Operator getOperator(ProceedingJoinPoint joinPoint, HttpServletRequest request, LogRecord logRecord) {
        String operator = logRecord.operator();
        if (StringUtils.hasText(operator)) {
            if (operator.contains("#")) {
                String operatorResult = parseExpression(joinPoint, operator);
                return new Operator(operatorResult, operatorResult);
            }
            return new Operator(operator, operator);
        }
        return operatorService.getOperator(request);
    }

    private String getBusinessNo(ProceedingJoinPoint joinPoint, LogRecord logRecord) {
        String businessNo = logRecord.businessNo();
        // 判断是否需要spel表达式解析
        if (businessNo.contains("#")) {
            return parseExpression(joinPoint, businessNo);
        }
        return businessNo;
    }

    private String getContent(ProceedingJoinPoint joinPoint, LogRecord logRecord) {
        final String template = logRecord.template();
        if (template.contains("#")) {
            final Method method = resolveMethod(joinPoint);
            EvaluationContext context = new MethodBasedEvaluationContext(TypedValue.NULL, method, joinPoint.getArgs(), parameterNameDiscoverer);
            final Expression expr = parser.getTemplateExpression(template);
            return expr.getValue(context, String.class);
        }
        return template;
    }

    private String parseExpression(ProceedingJoinPoint joinPoint, String expression) {
        final Method method = resolveMethod(joinPoint);
        EvaluationContext context = new MethodBasedEvaluationContext(TypedValue.NULL, method, joinPoint.getArgs(), parameterNameDiscoverer);
        Expression expr = parser.getExpression(new AnnotatedElementKey(method, joinPoint.getTarget().getClass()), expression);
        return expr.getValue(context, String.class);
    }

    private Method resolveMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();

        return getDeclaredMethodFor(targetClass, signature.getName(),
                signature.getMethod().getParameterTypes());
    }

    private Method getDeclaredMethodFor(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                return getDeclaredMethodFor(superClass, name, parameterTypes);
            }
        }
        throw new IllegalStateException("Cannot resolve target method: " + name);
    }
}
