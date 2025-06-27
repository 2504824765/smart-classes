package com.bnwzy.smartclassesspringbootweb.service;

import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassesCreateDTO;
import com.bnwzy.smartclassesspringbootweb.pojo.dto.ClassesUpdateDTO;
import org.springframework.stereotype.Service;

@Service
public interface IClassesService {
    Classes addClass(ClassesCreateDTO classesCreateDTO);

    boolean deleteClass(Long id);

    Classes updateClass(ClassesUpdateDTO classesUpdateDTO);
}
