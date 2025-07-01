package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.StudentClasses;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentClassesCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentClassesUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionCreateDTO;

import java.util.List;

public interface IStudentClassesService {
    StudentClasses addClassRecord(StudentClassesCreateDTO studentClassesCreateDTO);

    boolean deleteClassRecordById(Long id);

    StudentClasses updateClassRecord(StudentClassesUpdateDTO studentClassesUpdateDTO);

    StudentClasses getStudentClassesById(Long id);

    List<StudentClasses> getAllStudentClassesByStudentId(Long sid);

    List<StudentClasses> getAllStudentClassesByClassesId(Long cid);
}