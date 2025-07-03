package com.bnwzy.smartclassesspringbootweb.repository;

import com.bnwzy.smartclassesspringbootweb.pojo.Department;
import com.bnwzy.smartclassesspringbootweb.pojo.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);

    List<Student> findByDepartment(Department department);
}
