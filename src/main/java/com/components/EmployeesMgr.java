package com.components;

import com.model.Department;
import com.model.Employee;
import com.model.Person;
import com.repositories.DepartmentRepository;
import com.repositories.EmployeeRepository;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
public class EmployeesMgr implements Runnable {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    private final ApplicationContext ctx;

    public EmployeesMgr(EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, ApplicationContext ctx){
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.ctx = ctx;
    }

    @Override
    public void run() {
        Random r = new Random();
        LocalDate nextYear = LocalDate.of(2023, 1, 1);
        LocalDate[] date = (LocalDate[]) ctx.getBean("date");
        for(LocalDate today = LocalDate.of(2022, 1, 1); today.isBefore(nextYear); today = today.plusDays(1)){
            date[0] = today;
            Employee emp = new Employee();
            emp.setType(Person.Type.EMPLOYEE);
            emp.setHireTime(today.plusDays(r.nextInt(366 - today.getDayOfYear())));
            List<Department> departments = departmentRepository.findAll();
            emp.setDepartment(departments.get(r.nextInt(departments.size())));
            System.out.printf("%s. Сотрудник %s нанят %s. Отдел: %s.\n",
                    today, emp.getId(), emp.getHireTime(), emp.getDepartment().getName());
            employeeRepository.saveAndFlush(emp);
            if (today.getDayOfYear()%5 == 0) {
                List<Employee> employees = employeeRepository.findByFiredTime(null);
                int n = 1 + r.nextInt(2);
                for (int i = 0; i < n; i++){
                    if (employees.isEmpty()) break;
                    emp = employees.remove(r.nextInt(employees.size()));
                    LocalDate d = emp.getHireTime().isBefore(today) ? today : emp.getHireTime();
                    emp.setFiredTime(d.plusDays(r.nextInt(366 - d.getDayOfYear())));
                    System.out.printf("%s. Сотрудник %s уволен %s. Отдел: %s. Проработал: %s.\n",
                            today, emp.getId(), emp.getFiredTime(), emp.getDepartment().getName(),
                            emp.getHireTime().until(emp.getFiredTime()).getDays());
                    employeeRepository.saveAndFlush(emp);
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }
        date[0] = nextYear;
    }
}
