package com.example.springboot_mogodb_demo.repo;

import com.example.springboot_mogodb_demo.entity.Employee;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepo extends MongoRepository<Employee, String> {
}
