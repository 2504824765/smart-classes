package com.bnwzy.smartclassesspringbootweb.repository;

import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByUsername(String username);

    List<Teacher> findByDepartment(Department department);
}
