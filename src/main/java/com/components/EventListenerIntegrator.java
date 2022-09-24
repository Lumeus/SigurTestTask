package com.components;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.jpa.boot.spi.IntegratorProvider;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

import java.util.ArrayList;
import java.util.List;

public class EventListenerIntegrator implements Integrator, IntegratorProvider {

    public static final EventListenerIntegrator INSTANCE = new EventListenerIntegrator();

    @Override
    public void integrate(Metadata metadata,
                          SessionFactoryImplementor sessionFactoryImplementor,
                          SessionFactoryServiceRegistry serviceRegistry) {
        final EventListenerRegistry eventListenerRegistry =
                serviceRegistry.getService(EventListenerRegistry.class);

        eventListenerRegistry.appendListeners(
                EventType.POST_INSERT,
                GuestsMgr.INSTANCE
        );

        eventListenerRegistry.appendListeners(
                EventType.POST_UPDATE,
                GuestsMgr.INSTANCE
        );
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory,
                             SessionFactoryServiceRegistry serviceRegistry) {

    }

    @Override
    public List<Integrator> getIntegrators() {
        List<Integrator> l = new ArrayList<>();
        l.add(INSTANCE);
        return l;
    }
}
