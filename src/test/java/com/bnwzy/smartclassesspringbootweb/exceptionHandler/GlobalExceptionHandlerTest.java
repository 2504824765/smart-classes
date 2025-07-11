package com.bnwzy.smartclassesspringbootweb.exceptionHandler;

import com.bnwzy.smartclassesspringbootweb.exception.*;
import com.bnwzy.smartclassesspringbootweb.pojo.ResponseMessage;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private GlobalExceptionHandler handler;
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = Mockito.mock(HttpServletRequest.class);
    }

    @Test
    void testDepartmentHasChildren() {
        ResponseMessage msg = handler.DepartmentHasChildren(new DepartmentHasChildrenException("child error"));
        assertEquals(513, msg.getCode());
        assertEquals("<该部门有子部门，不能直接删除！>", msg.getMessage());
    }

    @Test
    void testFileIsNullException() {
        ResponseMessage msg = handler.FileIsNullException(new FileIsNullException("file null"));
        assertEquals(512, msg.getCode());
        assertEquals("<File is null>", msg.getMessage());
    }

    @Test
    void testClassMissionNotFoundException() {
        ResponseMessage msg = handler.classMissionNotFoundException(new ClassMissionNotFoundException("not found"));
        assertEquals(511, msg.getCode());
        assertEquals("<ClassMission not found>", msg.getMessage());
    }

    @Test
    void testIllegalParentDeptException() {
        ResponseMessage msg = handler.illegalParentDeptException(new IllegalParentDeptException("illegal parent"));
        assertEquals(510, msg.getCode());
        assertEquals("<Target dept is not parent dept>", msg.getMessage());
    }

    @Test
    void testHandleClassesNotFoundException() {
        ResponseMessage msg = handler.handleClassesNotFoundException(new ClassesNotFoundException("class not found"));
        assertEquals(509, msg.getCode());
        assertEquals("<Class not found>", msg.getMessage());
    }

    @Test
    void testDepartmentAlreadyExistException() {
        ResponseMessage msg = handler.departmentAlreadyExistException(new DepartmentAlreadyExistException("exist"), request);
        assertEquals(508, msg.getCode());
        assertEquals("<Department already exist>", msg.getMessage());
    }

    @Test
    void testStudentClassesNotFoundException() {
        ResponseMessage msg = handler.StudentClassesNotFoundException(new StudentClassesNotFoundException("not found"), request);
        assertEquals(507, msg.getCode());
        assertEquals("<StudentClasses not found>", msg.getMessage());
    }

    @Test
    void testWrongPasswordException() {
        ResponseMessage msg = handler.wrongPasswordException(new WrongPasswordException("wrong pwd"));
        assertEquals(506, msg.getCode());
        assertEquals("<Wrong Password>", msg.getMessage());
    }

    @Test
    void testTeacherNotFoundException() {
        ResponseMessage msg = handler.teacherNotFoundException(new TeacherNotFoundException("not found"), request);
        assertEquals(505, msg.getCode());
        assertEquals("<Teacher not found>", msg.getMessage());
    }

    @Test
    void testDepartmentNotFoundException() {
        ResponseMessage msg = handler.departmentNotFoundException(new DepartmentNotFoundException("not found"), request);
        assertEquals(504, msg.getCode());
        assertEquals("<Department not found>", msg.getMessage());
    }

    @Test
    void testStudentNotFoundException() {
        ResponseMessage msg = handler.studentNotFountException(new StudentNotFoundException("not found"));
        assertEquals(503, msg.getCode());
        assertEquals("<Student Not Found>", msg.getMessage());
    }

    @Test
    void testUserAlreadyExistException() {
        ResponseMessage msg = handler.userAlreadyExistException(new UserAlreadyExistException("exist"));
        assertEquals(502, msg.getCode());
        assertEquals("<User Already Exist>", msg.getMessage());
    }

    @Test
    void testUserNotFoundException() {
        ResponseMessage msg = handler.userNotFoundException(new UserNotFoundException("not found"));
        assertEquals(501, msg.getCode());
        assertEquals("<User Not Found>", msg.getMessage());
    }

    @Test
    void testHandleException() {
        ResponseMessage msg = handler.handleException(new Exception("unknown error"));
        assertEquals(500, msg.getCode());
        assertEquals("未知错误，请联系管理员", msg.getMessage());
    }
} 