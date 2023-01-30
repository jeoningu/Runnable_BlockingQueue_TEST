package com.example.runnable_blockingqueue_test.aop;

import com.example.runnable_blockingqueue_test.model.RequestLog;
import com.example.runnable_blockingqueue_test.process.RequestLogProducer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Aspect
@Component
public class RequestLogAspect {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RequestLogProducer requestLogProducer;

    @Around("@annotation(com.example.runnable_blockingqueue_test.annotation.RequestLogAnnotation)")
    public Object historyRun(ProceedingJoinPoint joinPoint) throws Throwable {
        //logger.debug("historyRun");

        // Controllor에 들어온 요청에서 적재할 데이터 추출 - 임시로 URI, Parameter 추출해봄
        RequestLog requestLog = new RequestLog();
        Object[] args = joinPoint.getArgs();
        for (Object object : args) {
            if(object instanceof HttpServletRequest) {
                Map<String, String[]> parameterMap = ((HttpServletRequest) object).getParameterMap();
                String str = "";
                int i = 0;
                for (Map.Entry entry : parameterMap.entrySet()) {
                    str += entry.getKey() + "=" + ((String[]) entry.getValue())[0]; // TODO : 임시로 [0] 처리함. 수정 필요할 수도?
                    i++;
                    if (i != parameterMap.size()) {
                        str += ",";
                    }
                }
                //str += "";

                String requestURI = ((HttpServletRequest) object).getRequestURI();

                //logger.debug("parameterMapStr= {}",str);
                //logger.debug("requestURI = {}",requestURI);
                requestLog.setParameterMapStr(str);
                requestLog.setRequestURI(requestURI);
            }
        }
        //메소드 실행
        Object result = joinPoint.proceed();

        requestLogProducer.queueAddRequest(requestLog);

        return result;
    }
}
