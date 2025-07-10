package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentMission;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.GetStudentMissionByStudentIdAndClassMissionIdDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentMissionUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentsAllClassMissionGetDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IStudentMissionService {
    StudentMission createStudentMission(StudentMissionCreateDTO studentMissionCreateDTO);

    Boolean deleteStudentMission(Long id);

    StudentMission updateStudentMission(StudentMissionUpdateDTO studentMissionUpdateDTO);

    StudentMission getStudentMissionById(Long id);

    List<StudentMission> getStudentsAllClassMission(StudentsAllClassMissionGetDTO studentsAllClassMissionGetDTO);

    List<StudentMission> getStudentMissionsByStudentId(Long id);

    List<StudentMission> getAllStudentMissionsOfClassMission(Long id);

    StudentMission getStudentMissionByStudentIdAndClassMissionId(GetStudentMissionByStudentIdAndClassMissionIdDTO getStudentMissionByStudentIdAndClassMissionIdDTO);
}
