package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.StudentData;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentDataCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentDataUpdateDTO;

public interface IStudentDataService {
    StudentData createStudentDate(StudentDataCreateDTO studentDataCreateDTO);

    Boolean deleteStudentData(Long id);

    StudentData updateStudentData(StudentDataUpdateDTO studentDataUpdateDTO);

    StudentData getStudentDataById(Long id);
}
