package com.acepero13.observer.observers;

import com.acepero13.dipatcher.EventDispatcher;
import com.acepero13.observer.Event;
import com.acepero13.observer.Observer;
import com.acepero13.observer.Notify;
import com.acepero13.observer.events.EventA;
import com.acepero13.observer.events.EventB;

import java.util.concurrent.atomic.AtomicBoolean;

@Observer
public final class EventAObserver {

    public final AtomicBoolean eventAFired = new AtomicBoolean(false);
    public final AtomicBoolean eventBFired = new AtomicBoolean(false);
    public final AtomicBoolean aOrBFired = new AtomicBoolean(false);
    public final AtomicBoolean allFired = new AtomicBoolean(false);

    public EventAObserver() {
        EventDispatcher.getInstance().register(this);
    }

    @Notify(when = {EventA.class})
    public void update(EventA event) {
        eventAFired.set(true);
    }

    @Notify(when = {EventB.class})
    public void update(EventB event) {
        eventBFired.set(true);
    }

    @Notify(when = {EventA.class, EventB.class})
    public void update(Event event) {
        aOrBFired.set(true);
    }

    @Notify()
    public void updateAll(Event event) {
        allFired.set(true);
    }

    public void methodThatShouldNeverBeCalled() {
        throw new RuntimeException("Should not be called");
    }
}
