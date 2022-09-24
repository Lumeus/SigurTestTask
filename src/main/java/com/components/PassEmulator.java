package com.components;

import com.model.Employee;
import com.model.Guest;
import com.model.Person;
import com.repositories.EmployeeRepository;
import com.repositories.GuestRepository;
import com.repositories.PersonRepository;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class PassEmulator implements Runnable {

    private final EmployeeRepository employeeRepository;
    private final GuestRepository guestRepository;
    private final PersonRepository personRepository;

    private final ApplicationContext ctx;

    public PassEmulator(EmployeeRepository employeeRepository, GuestRepository guestRepository, PersonRepository personRepository, ApplicationContext ctx){
        this.employeeRepository = employeeRepository;
        this.guestRepository = guestRepository;
        this.personRepository = personRepository;
        this.ctx = ctx;
    }

    public void testCard(UUID card){
        LocalDate today = ((LocalDate[]) ctx.getBean("date"))[0];
        Guest guest = guestRepository.findByCard(card);
        if (guest != null) {
            if (guest.getVisitDate() == null || !guest.getVisitDate().equals(today)) {
                System.out.printf("%s Доступ запрещён гостю %s. Карта: %s\n", today, guest.getId(), card);
            } else {
                System.out.printf("%s Предоставлен доступ гостю %s. Пришёл к %s из отдела: %s. Карта: %s\n", today,
                        guest.getId(), guest.getEmployee().getId(), guest.getEmployee().getDepartment().getName(), card);
            }
        } else {
            Employee employee = employeeRepository.findByCard(card);
            if (employee != null) {
                if (employee.getHireTime().isAfter(today) || (employee.getFiredTime() != null && employee.getFiredTime().isBefore(today))) {
                    System.out.printf("%s Предоставлен доступ сотруднику %s. Отдел: %s. Карта: %s\n",
                            today, employee.getId(), employee.getDepartment().getName(), card);
                } else System.out.printf("%s Доступ запрещен сотруднику %s. Отдел: %s. Карта: %s\n",
                        today, employee.getId(), employee.getDepartment().getName(), card);
            } else System.out.printf("Поднесена неизвестная карта: %s\n", card);
        }
    }

    @Override
    public void run() {
        Random random = new Random();
        LocalDate nextYear = LocalDate.of(2023, 1, 1);
        while (!((LocalDate[])ctx.getBean("date"))[0].equals(nextYear)) {
            if (random.nextInt(4) == 0) {
                testCard(UUID.randomUUID());
            } else {
                List<Person> people = personRepository.findAll();
                if (!people.isEmpty()) testCard(people.get(random.nextInt(people.size())).getCard());
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
