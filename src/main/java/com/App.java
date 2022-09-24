package com;

import com.components.EmployeesMgr;
import com.components.GuestsMgr;
import com.components.PassEmulator;
import com.model.Department;
import com.repositories.DepartmentRepository;
import com.repositories.EmployeeRepository;
import com.repositories.GuestRepository;
import com.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class App implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Autowired
    PersonRepository personRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    GuestRepository guestRepository;

    @Autowired
    DepartmentRepository departmentRepository;

    @Autowired
    ApplicationContext ctx;

    @Override
    public void run(String... args) {
        EmployeesMgr empMgr = new EmployeesMgr(employeeRepository, departmentRepository, ctx);
        PassEmulator passEmulator = new PassEmulator(employeeRepository, guestRepository, personRepository, ctx);
        GuestsMgr.setCtx(ctx);
        Department d;
        for (int i = 1; i < 11; i++) {
            d = new Department();
            d.setName("Department_"+i);
            departmentRepository.save(d);
        }
        departmentRepository.flush();
        new Thread(empMgr).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            return;
        }
        new Thread(passEmulator).start();
        try {
            Thread.sleep(36600);
        } catch (InterruptedException e) {}
    }
}
