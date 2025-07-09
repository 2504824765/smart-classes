package com.bnwzy.smartclassesspringbootweb.exceptionHandler;

import com.bnwzy.smartclassesspringbootweb.exception.*;
import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(DepartmentHasChildrenException.class)
    public ResponseMessage DepartmentHasChildren(DepartmentHasChildrenException e) {
        logger.error(e.getMessage());
        return new ResponseMessage(513, "<该部门有子部门，不能直接删除！>", null);
    }
    @ExceptionHandler(FileIsNullException.class)
    public ResponseMessage FileIsNullException(FileIsNullException e) {
        logger.error(e.getMessage());
        return new ResponseMessage(512, "<Fill is null>", null);
    }
    @ExceptionHandler(ClassMissionNotFoundException.class)
    public ResponseMessage classMissionNotFoundException(ClassMissionNotFoundException e) {
        logger.error(e.getMessage());
        return new ResponseMessage(511, "<ClassMission not found>", null);
    }

    @ExceptionHandler(IllegalParentDeptException.class)
    public ResponseMessage illegalParentDeptException(Exception e) {
        logger.error(e.getMessage());
        return new ResponseMessage(510, "<Target dept is not parent dept>", null);
    }

    @ExceptionHandler(ClassesNotFoundException.class)
    public ResponseMessage handleClassesNotFoundException(ClassesNotFoundException e) {
        logger.error(e.getMessage());
        return new ResponseMessage(509, "<Class not found>", null);
    }

    @ExceptionHandler(DepartmentAlreadyExistException.class)
    public ResponseMessage departmentAlreadyExistException(Exception e, HttpServletRequest request) {
        logger.error(e.getMessage());
        return new ResponseMessage(508, "<Department already exist>", null);
    }

    @ExceptionHandler(StudentClassesNotFoundException.class)
    public ResponseMessage StudentClassesNotFoundException(Exception e, HttpServletRequest request) {
        logger.error(e.getMessage());
        return new ResponseMessage(507, "<Teacher not found>", null);
    }
    @ExceptionHandler(WrongPasswordException.class)
    public ResponseMessage wrongPasswordException(Exception e) {
        logger.error(e.getMessage(), e);
        return new ResponseMessage(506, "<Wrong Password>", null);
    }

    @ExceptionHandler(TeacherNotFoundException.class)
    public ResponseMessage teacherNotFoundException(Exception e, HttpServletRequest request) {
        logger.error(e.getMessage());
        return new ResponseMessage(505, "<Teacher not found>", null);
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseMessage departmentNotFoundException(Exception e, HttpServletRequest request) {
        logger.error(e.getMessage());
        return new ResponseMessage(504, "<Department not found>", null);
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseMessage studentNotFountException(Exception e) {
        logger.error(e.getMessage());
        return new ResponseMessage(503, "<Student Not Found>", null);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseMessage userAlreadyExistException(Exception e) {
        logger.error(e.getMessage());
        return new ResponseMessage(502, "<User Already Exist>", null);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseMessage userNotFoundException(Exception e) {
        logger.error(e.getMessage());
        return new ResponseMessage(501, "<User Not Found>", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseMessage handleException(Exception e) {
        logger.error(e.getMessage(), e); // 查看日志查看具体异常
        return new ResponseMessage(500, "未知错误，请联系管理员", null);
    }

}
