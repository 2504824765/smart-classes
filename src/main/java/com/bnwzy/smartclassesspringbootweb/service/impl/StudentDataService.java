package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.StudentDataNotFoundException;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentData;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentDataCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.StudentDataUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.StudentDataRepository;
import com.bnwzy.smartclassesspringbootweb.service.IStudentDataService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StudentDataService implements IStudentDataService {
    @Autowired
    private StudentDataRepository studentDataRepository;

    @Override
    public StudentData createStudentDate(StudentDataCreateDTO studentDataCreateDTO) {
        StudentData studentData = new StudentData();
        BeanUtils.copyProperties(studentDataCreateDTO, studentData);
        return studentDataRepository.save(studentData);
    }

    @Override
    public Boolean deleteStudentData(Long id) {
        if (studentDataRepository.findById(id).isPresent()) {
            studentDataRepository.deleteById(id);
            return true;
        } else {
            throw new StudentDataNotFoundException("<StudentData not found>");
        }
    }

    @Override
    public StudentData updateStudentData(StudentDataUpdateDTO studentDataUpdateDTO) {
        if (studentDataRepository.findById(studentDataUpdateDTO.getId()).isEmpty()) {
            throw new StudentDataNotFoundException("<StudentData not found>");
        } else {
            StudentData studentData = studentDataRepository.findById(studentDataUpdateDTO.getId()).get();
            studentData.setConceptUnderstanding(studentDataUpdateDTO.getConceptUnderstanding());
            studentData.setExpressionNorms(studentDataUpdateDTO.getExpressionNorms());
            studentData.setProblemSolving(studentDataUpdateDTO.getProblemSolving());
            studentData.setLogicalReasoning(studentDataUpdateDTO.getLogicalReasoning());
            studentData.setInnovativeThinking(studentDataUpdateDTO.getInnovativeThinking());
            return studentDataRepository.save(studentData);
        }
    }

    @Override
    public StudentData getStudentDataById(Long id) {
        if (studentDataRepository.findById(id).isPresent()) {
            return studentDataRepository.findById(id).get();
        } else {
            throw new StudentDataNotFoundException("<StudentData not found>");
        }
    }
}
