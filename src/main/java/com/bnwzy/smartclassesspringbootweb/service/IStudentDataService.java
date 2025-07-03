package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.StudentData;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentDataCreateDTO;

public interface IStudentDataService {
    StudentData createStudentDate(StudentDataCreateDTO studentDataCreateDTO);

    Boolean deleteStudentData(Long id);

}
