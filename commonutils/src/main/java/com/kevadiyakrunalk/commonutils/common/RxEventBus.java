package com.kevadiyakrunalk.commonutils.common;

import android.os.Handler;
import android.os.Looper;

import rx.Subscription;
import rx.functions.Action1;
import rx.internal.util.SubscriptionList;
import rx.subjects.PublishSubject;

public class RxEventBus {

    private static RxEventBus instance;
    private final PublishSubject<Object> eventBus = PublishSubject.create();
    private SubscriptionList subscriptionList;
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    private RxEventBus() {
        subscriptionList = new SubscriptionList();
    }

    public static RxEventBus getInstance() {
        if (instance == null) {
            synchronized (RxEventBus.class) {
                instance = new RxEventBus();
            }
        }
        return instance;
    }

    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            eventBus.onNext(event);
        } else {
            mainThread.post(() -> eventBus.onNext(event));
        }
    }

    public <T> Subscription register(final Class<T> eventClass, final Action1<T> onNext) {
        Subscription subs = eventBus.filter(event -> event.getClass().equals(eventClass))
                .map(object -> (T) object)
                .subscribe(onNext);

        subscriptionList.add(subs);
        return subs;
    }

    public void unregister(Subscription subscription) {
        subscriptionList.remove(subscription);
    }

    public void unregisterAll() {
        subscriptionList.unsubscribe();
    }
}
