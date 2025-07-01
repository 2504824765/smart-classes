package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.StudentMission;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionUpdateDTO;
import org.springframework.stereotype.Service;

@Service
public interface IStudentMissionService {
    StudentMission createStudentMission(StudentMissionCreateDTO studentMissionCreateDTO);

    Boolean deleteStudentMission(Long id);

    StudentMission updateStudentMission(StudentMissionUpdateDTO studentMissionUpdateDTO);
}
