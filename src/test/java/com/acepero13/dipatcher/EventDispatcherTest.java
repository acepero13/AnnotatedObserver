package com.acepero13.dipatcher;

import com.acepero13.observer.events.EventA;
import com.acepero13.observer.exception.ObserverIsNotAnnotated;
import com.acepero13.observer.observers.EventAObserver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventDispatcherTest {

    private final EventAObserver observer = new EventAObserver();

    @AfterEach
    public void tearDown() {
        EventDispatcher.getInstance().shutDown();
    }

    @Test
    public void cannotAddNonAnnotatedObserver() {
        assertThrows(ObserverIsNotAnnotated.class, () -> EventDispatcher.getInstance().register(this));
    }


    @Test
    public void notifiesEventA() {
        EventDispatcher.getInstance().dispatch(new EventA());
        assertTrue(observer.eventAFired.get());
        assertFalse(observer.eventBFired.get());
    }

    @Test
    public void notifiesEventThatExpectsAOrB() {
        EventDispatcher.getInstance().dispatch(new EventA());
        assertTrue(observer.eventAFired.get());
        assertTrue(observer.aOrBFired.get());
        assertFalse(observer.eventBFired.get());
    }

    @Test
    public void notifiesAllWhenInterestedInAllEvents() {
        EventDispatcher.getInstance().dispatch(new EventA());

        EventDispatcher.getInstance().dispatch(new EventA());
        assertTrue(observer.eventAFired.get());
        assertTrue(observer.allFired.get());
        assertTrue(observer.aOrBFired.get());
        assertFalse(observer.eventBFired.get());
    }

}