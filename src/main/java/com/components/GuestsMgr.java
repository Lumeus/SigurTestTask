package com.components;

import com.model.Employee;
import com.model.Guest;
import com.model.Person;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.context.ApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

public class GuestsMgr implements PostInsertEventListener, PostUpdateEventListener/*, SaveOrUpdateEventListener*/ {

    private static ApplicationContext ctx;

    private static final Random random = new Random();

    public static final GuestsMgr INSTANCE = new GuestsMgr();

    public static void setCtx(ApplicationContext ctx) {
        GuestsMgr.ctx = ctx;
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        final Object entity = event.getEntity();

        if (entity instanceof Employee && random.nextBoolean()) {
            Employee emp = (Employee) entity;
            Guest guest = new Guest();
            guest.setType(Person.Type.GUEST);
            guest.setEmployee(emp);
            LocalDate visitDate = emp.getHireTime().plusDays(random.nextInt(183));
            guest.setVisitDate(visitDate);
            event.getSession().createNativeQuery("INSERT into person (id, card, type) values (:id, :card, :type)")
                    .setParameter("id", guest.getId())
                    .setParameter("card", guest.getCard())
                    .setParameter("type", guest.getType().ordinal())
                    .setHibernateFlushMode(FlushMode.MANUAL)
                    .executeUpdate();
            event.getSession().createNativeQuery("INSERT into guest (id, visit_date, employee_id) values (:id, :date, :emp)")
                    .setParameter("id", guest.getId())
                    .setParameter("date", guest.getVisitDate())
                    .setParameter("emp", guest.getEmployee().getId())
                    .setHibernateFlushMode(FlushMode.MANUAL)
                    .executeUpdate();
//            ctx.refresh();
            System.out.printf(
                    "Гостю %s назначена встреча сотруднику %s. Отдел: %s. Дата: %s. До встречи осталось: %s\n",
                    guest.getId(), emp.getId(), emp.getDepartment().getName(), visitDate,
                    ((LocalDate[])ctx.getBean("date"))[0].until(visitDate).getDays());
        }
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister entityPersister) {
        return false;
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
//    public void onSaveOrUpdate(SaveOrUpdateEvent event) {
        final Object entity = event.getEntity();

        if (entity instanceof Employee) {
            Employee emp = (Employee) entity;
            if (emp.getFiredTime() != null) {
                Session session = event.getSession().getSessionFactory().openSession();
                List<Guest> guests = session
                        .createNativeQuery("SELECT * from guest left join person on guest.id = person.id " +
                                "WHERE employee_id = :emp", Guest.class)
                        .setParameter("emp", emp.getId())
                        .getResultList();
                session.close();
                event.getSession().createNativeQuery("UPDATE guest SET visit_date = null " +
                        "WHERE employee_id = :emp AND (visit_date - :date) >= 0")
                        .setParameter("emp", emp.getId())
                        .setParameter("date", emp.getFiredTime())
                        .setHibernateFlushMode(FlushMode.MANUAL)
                        .executeUpdate();
//                List<Guest> guests = guestRepository.findByEmployee(emp);
//                for (Guest guest: guests) {
//                    if (emp.getFiredTime().isBefore(guest.getVisitDate())) {
//                        LocalDate visitDate = guest.getVisitDate();
//                        guest.setVisitDate(null);
//                        guestRepository.saveAndFlush(guest);
                for (Guest guest: guests)
                        System.out.printf(
                                "Встреча гостя %s с сотрудником %s отменена. Отдел: %s. Дата встречи: %s, дата увольнения сотрудника: %s\n",
                                guest.getId(), emp.getId(), emp.getDepartment().getName(), guest.getVisitDate(), emp.getFiredTime());
//                    }
//                }
            }
        }
    }
}
