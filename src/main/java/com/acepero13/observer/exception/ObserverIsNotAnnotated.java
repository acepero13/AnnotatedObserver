package com.acepero13.observer.exception;

public final class ObserverIsNotAnnotated extends RuntimeException {
    public ObserverIsNotAnnotated(Object observer) {
        super("This is not a valid Observer. Please add the @Observer annotation to class: " +
                observer.getClass().getSimpleName());
    }
}
