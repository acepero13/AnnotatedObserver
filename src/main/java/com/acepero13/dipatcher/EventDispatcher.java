package com.acepero13.dipatcher;

import com.acepero13.observer.Event;
import com.acepero13.observer.Observer;
import com.acepero13.observer.Notify;
import com.acepero13.observer.exception.ObserverIsNotAnnotated;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Notifies its registered observers when a new event is dispatched
 */
public final class EventDispatcher {
    private static final EventDispatcher instance = new EventDispatcher();
    private final List<Object> observers = new ArrayList<>();

    private EventDispatcher() {
    }

    /**
     * Returns singleton instance
     *
     * @return singleton instance of the {@code EventDispatcher}
     */
    public static EventDispatcher getInstance() {
        return instance;
    }

    /**
     * Registers a new {@link Observer}
     *
     * @param observer instance of an object annotated with {@link Observer}
     * @throws ObserverIsNotAnnotated in case the observer we want to register is not correctly annotated
     */
    public void register(@NotNull Object observer) {
        if (observer.getClass().isAnnotationPresent(Observer.class)) {
            this.observers.add(observer);
        } else {
            throw new ObserverIsNotAnnotated(observer);
        }
    }

    /**
     * Shuts down the {@code EventDispatcher}
     *
     * @implNote unregisters all registered observers
     */
    public void shutDown() {
        this.observers.clear();
    }

    /**
     * Dispatches an event. This notifies every class annotated with {@link Observer}
     *
     * @param event an instance of the event we want to trigger
     */
    public void dispatch(Event event) {
        observers.stream()
                .map(o -> new AnnotationProcessor(o, event))
                .forEach(AnnotationProcessor::notifyObserver);
    }

    private static class AnnotationProcessor {

        private final Object observer;
        private final Event event;
        private final List<Method> methods;

        public AnnotationProcessor(Object observer, Event event) {
            this.observer = observer;
            this.event = event;
            this.methods = executableMethods();
        }

        public void notifyObserver() {
            methods.forEach(this::tryToExecute);
        }

        private void tryToExecute(Method method) {
            method.setAccessible(true);
            try {
                method.invoke(observer, event);
            } catch (IllegalAccessException | InvocationTargetException e) { // TODO: LOG
                e.printStackTrace();
            }
        }

        public List<Method> executableMethods() {
            return Arrays.stream(observer.getClass().getDeclaredMethods())
                    .filter(m -> m.isAnnotationPresent(Notify.class))
                    .filter(m -> methodAcceptsEvent(m, event))
                    .collect(Collectors.toList());

        }

        private boolean methodAcceptsEvent(Method method, Event event) {
            var annotations = method.getAnnotation(Notify.class).when();
            return Arrays.stream(annotations).anyMatch(a -> a.isInstance(event));
        }
    }
}
