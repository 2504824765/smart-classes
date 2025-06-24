package com.bnwzy.smartclassesspringbootweb.exceptionHandler;

import com.bnwzy.smartclassesspringbootweb.exception.StudentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseMessage studentNotFountException(Exception e) {
        logger.error(e.getMessage());
        return new ResponseMessage(501, "<Student Not Found>", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseMessage handleException(Exception e) {
        logger.error(e.getMessage(), e); // 查看日志查看具体异常
        return new ResponseMessage(500, "Unknown exception", null);
    }
}
