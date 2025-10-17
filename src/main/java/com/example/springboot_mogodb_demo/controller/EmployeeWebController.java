package com.example.springboot_mogodb_demo.controller;

import com.example.springboot_mogodb_demo.entity.Employee;
import com.example.springboot_mogodb_demo.entity.Project;
import com.example.springboot_mogodb_demo.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employees")
public class EmployeeWebController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String listEmployees(Model model,
                                @ModelAttribute("successMessage") String successMessage,
                                @ModelAttribute("errorMessage") String errorMessage) {
        model.addAttribute("employees", employeeService.getAllEmployees());
        model.addAttribute("successMessage", successMessage);
        model.addAttribute("errorMessage", errorMessage);

        return "employees";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("projectNamesString", "");
        model.addAttribute("projectAllocationsString", "");

        return "employee-form";
    }

    @PostMapping
    public String createEmployee(@ModelAttribute Employee employee,
                                 @RequestParam(value = "projectNames", required = false) String projectNames,
                                 @RequestParam(value = "projectAllocations", required = false) String projectAllocations,
                                 RedirectAttributes redirectAttributes) {
        populateProjectsFromInputs(employee, projectNames, projectAllocations);
        try {
            employeeService.createEmployee(employee);
            redirectAttributes.addFlashAttribute("successMessage", "Employee created successfully!!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to create employee: " + e.getMessage());
        }

        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Employee> optionalEmployee = employeeService.getEmployeeById(id);
        if (optionalEmployee.isPresent()) {
            Employee employee = optionalEmployee.get();
            model.addAttribute("employee", employee);
            model.addAttribute("projectNamesString", joinProjectNames(employee.getProjects()));
            model.addAttribute("projectAllocationsString", joinProjectAllocations(employee.getProjects()));

            return "employee-form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Employee not found with ID: " + id);
            return "redirect:/employees";
        }
    }

    @PostMapping("/update/{id}")
    public String updateEmployee(@PathVariable String id,
                                 @ModelAttribute Employee employee,
                                 @RequestParam(value = "projectNames", required = false) String projectNames,
                                 @RequestParam(value = "projectAllocations", required = false) String projectAllocations,
                                 RedirectAttributes redirectAttributes) {
        populateProjectsFromInputs(employee, projectNames, projectAllocations);

        try {
            employeeService.updateEmployee(id, employee);
            redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully!!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to update employee: " + e.getMessage());
        }

        return "redirect:/employees";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully!!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Failed to delete employee: " + e.getMessage());
        }

        return "redirect:/employees";
    }

    private void populateProjectsFromInputs(Employee employee, String projectNames, String projectAllocations) {
        if (projectNames == null || projectNames.trim().isEmpty()) {
            employee.setProjects(new ArrayList<>());
            return;
        }

        List<String> names = List.of(projectNames.split("\\s*, \\s*"));
        List<String> allocs = (projectAllocations == null || projectAllocations.trim().isEmpty())
                ? new ArrayList<>()
                : List.of(projectAllocations.split("\\s*, \\s*"));

        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            Project project = new Project();
            project.setName(names.get(i));
            if (i < allocs.size()) {
                try {
                    project.setAllocation(Integer.parseInt(allocs.get(i)));
                } catch (NumberFormatException e) {
                    project.setAllocation(0);
                }
            } else {
                project.setAllocation(0);
            }

            projects.add(project);
        }

        employee.setProjects(projects);
    }

    private String joinProjectNames(List<Project> projects) {
        if (projects== null || projects.isEmpty()) return "";

        return projects.stream()
                .map(Project::getName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", "));
    }

    private String joinProjectAllocations(List<Project> projects) {
        if (projects== null || projects.isEmpty()) return "";

        return projects.stream()
                .map(p -> String.valueOf(p.getAllocation()))
                .collect(Collectors.joining(", "));
    }

}
