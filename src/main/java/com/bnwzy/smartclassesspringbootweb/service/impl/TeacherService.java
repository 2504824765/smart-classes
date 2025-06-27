package com.bnwzy.smartclassesspringbootweb.service.impl;

import com.bnwzy.smartclassesspringbootweb.exception.DepartmentNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.TeacherNotFoundException;
import com.bnwzy.smartclassesspringbootweb.exception.UserAlreadyExistException;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.TeacherCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.TeacherUpdateDTO;
import com.bnwzy.smartclassesspringbootweb.repository.DepartmentRepository;
import com.bnwzy.smartclassesspringbootweb.repository.TeacherRepository;
import com.bnwzy.smartclassesspringbootweb.service.ITeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeacherService implements ITeacherService {

    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Override
    public Teacher addTeacher(TeacherCreateDTO teacherCreateDTO) {
        if(!teacherRepository.findByUsername(teacherCreateDTO.getUsername()).isPresent()){
            Teacher teacher = new Teacher();
            teacher.setUsername(teacherCreateDTO.getUsername());
            teacher.setName(teacherCreateDTO.getName());
            teacher.setGender(teacherCreateDTO.getGender());
            if(departmentRepository.findById(teacherCreateDTO.getDepartmentId()).isPresent()){
                teacher.setDepartment(departmentRepository.findById(teacherCreateDTO.getDepartmentId()).get());
            }else{
                throw new DepartmentNotFoundException("Department not found");
            }
            return teacherRepository.save(teacher);
        }else{
            throw new UserAlreadyExistException("User already exist");
        }
    }

    @Override
    public Teacher updateTeacher(TeacherUpdateDTO teacherUpdateDTO) {
        if(teacherRepository.existsById(teacherUpdateDTO.getId())){
            Teacher teacher=teacherRepository.findById(teacherUpdateDTO.getId()).get();
            teacher.setName(teacherUpdateDTO.getName());
            teacher.setGender(teacherUpdateDTO.getGender());
            if(departmentRepository.findById(teacherUpdateDTO.getDepartmentId()).isPresent()){
                teacher.setDepartment(departmentRepository.findById(teacherUpdateDTO.getDepartmentId()).get());
                teacherRepository.save(teacher);
                return teacher;
            }
            else{
                throw new DepartmentNotFoundException("Department not found");
            }
        }else{
            throw new TeacherNotFoundException("Teacher not found");
        }
    }

    @Override
    public boolean deleteById(Long id) {
        if(teacherRepository.existsById(id)){
            teacherRepository.deleteById(id);
            return true;
        }else{
            throw new TeacherNotFoundException("Teacher not found");
        }
    }

    @Override
    public Teacher getTeacherById(Long id) {
        if (teacherRepository.existsById(id)) {
            return teacherRepository.findById(id).get();
        }else{
            throw new TeacherNotFoundException("Teacher not fount");
        }
    }

    @Override
    public Teacher getTeacherByUsername(String username) {
        if(teacherRepository.findByUsername(username).isPresent()){
            return teacherRepository.findByUsername(username).get();
        }else{
            throw new TeacherNotFoundException("Teacher not found");
        }
    }

    @Override
    public List<Teacher> getAllTeacher() {
        List<Teacher> teacherList=new ArrayList<>();
        for (Teacher teacher : teacherRepository.findAll()) {
            teacherList.add(teacher);
        }
        return teacherList;
    }
}
