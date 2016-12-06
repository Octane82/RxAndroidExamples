package dkarelin.ru.rxjavaexample_1.rxbus;

import rx.Observable;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

/**
 * Implementation my EventBus with javaRx
 */

public class RxBus {

    public RxBus() {
    }

    private final Subject<Object, Object> bus =
            new SerializedSubject<>(PublishSubject.create());


    public void send(Object o) {
        bus.onNext(o);
    }


    public Observable<Object> toObserverable() {
        return bus;
    }

    public boolean hasObservers() {
        return bus.hasObservers();
    }


}
