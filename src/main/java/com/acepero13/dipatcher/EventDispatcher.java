package com.acepero13.dipatcher;

import com.acepero13.observer.Event;
import com.acepero13.observer.Observer;
import com.acepero13.observer.OnEvent;
import com.acepero13.observer.exception.ObserverIsNotAnnotated;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public final class EventDispatcher {
    private static final EventDispatcher instance = new EventDispatcher();
    private final List<Object> observers = new ArrayList<>();
    private final HashMap<Class<? extends Event>, Object> observersEventMap = new HashMap<>();

    private EventDispatcher() {
    }

    public static EventDispatcher getInstance() {
        return instance;
    }

    public void register(Object observer) {
        if (observer.getClass().isAnnotationPresent(Observer.class)) {
            this.observers.add(observer);
        } else {
            throw new ObserverIsNotAnnotated();
        }
    }

    public void shutDown() {
        this.observers.clear();
    }

    public void dispatch(Event event) {
        observers
                .forEach(o -> notifyObserver(o, event));
    }

    private void notifyObserver(Object obj, Event event) {
        Class<?> clazz = obj.getClass();
        for(Method method: clazz.getDeclaredMethods()) {
            if(method.isAnnotationPresent(OnEvent.class) && methodAcceptsEvent(method, event)) {
                method.setAccessible(true);
                try {
                    method.invoke(obj, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean methodAcceptsEvent(Method method, Event event) {
        var annotations = method.getAnnotation(OnEvent.class).notifyOn();
        return Arrays.stream(annotations).anyMatch(a -> a.isInstance(event));
    }
}
