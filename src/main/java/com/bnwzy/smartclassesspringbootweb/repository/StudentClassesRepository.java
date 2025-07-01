package com.bnwzy.smartclassesspringbootweb.repository;

import com.bnwzy.smartclassesspringbootweb.pojo.Classes;
import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.StudentClasses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentClassesRepository extends JpaRepository<StudentClasses,Long> {

    List<StudentClasses> findByClasses(Classes classes);
}
