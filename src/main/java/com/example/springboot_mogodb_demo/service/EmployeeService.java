package com.example.springboot_mogodb_demo.service;

import com.example.springboot_mogodb_demo.entity.Employee;
import com.example.springboot_mogodb_demo.repo.EmployeeRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepo employeeRepo;

    public Employee createEmployee(Employee employee) {
        return employeeRepo.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepo.findAll();
    }

    public Optional<Employee> getEmployeeById(String id) {
        return employeeRepo.findById(id);
    }

    public Employee updateEmployee(String id, Employee updatedEmployee) {
        return employeeRepo.findById(id).map(existing -> {
            existing.setName(updatedEmployee.getName());
            existing.setEmail(updatedEmployee.getEmail());
            existing.setGender(updatedEmployee.getGender());
            existing.setDepartment(updatedEmployee.getDepartment());
            existing.setProjects(updatedEmployee.getProjects());

            return employeeRepo.save(existing);
        }).orElseThrow(() -> new RuntimeException("Employee not found with id: " + id));
    }

    public void deleteEmployee(String id) {
        employeeRepo.deleteById(id);
    }

}
