package com.kevadiyakrunalk.commonutils.widget.view;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.kevadiyakrunalk.commonutils.internal.Functions;
import com.kevadiyakrunalk.commonutils.internal.Preconditions;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

import static android.os.Build.VERSION_CODES.M;
import static rx.android.MainThreadSubscription.verifyMainThread;

/**
 * Static factory methods for creating {@linkplain Observable observables} and {@linkplain Action1
 * actions} for {@link View}.
 */
public final class RxView {
    /**
     * Create an observable which emits on {@code view} attach events. The emitted value is
     * unspecified and should only be used as notification.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> attaches(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        boolean callOnAttach = true;
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                verifyMainThread();

                final View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
                    @Override public void onViewAttachedToWindow(@NonNull final View v) {
                        if (callOnAttach && !subscriber.isUnsubscribed()) {
                            subscriber.onNext(null);
                        }
                    }

                    @Override public void onViewDetachedFromWindow(@NonNull final View v) {
                        if (!callOnAttach && !subscriber.isUnsubscribed()) {
                            subscriber.onNext(null);
                        }
                    }
                };
                view.addOnAttachStateChangeListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.removeOnAttachStateChangeListener(listener);
                    }
                });
            }
        });
    }

    /**
     * Create an observable of attach and detach events on {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult @NonNull
    public static Observable<ViewAttachEvent> attachEvents(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<ViewAttachEvent>() {
            @Override
            public void call(Subscriber<? super ViewAttachEvent> subscriber) {
                verifyMainThread();

                final View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
                    @Override public void onViewAttachedToWindow(@NonNull final View v) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(ViewAttachEvent.create(view, ViewAttachEvent.Kind.ATTACH));
                        }
                    }

                    @Override public void onViewDetachedFromWindow(@NonNull final View v) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(ViewAttachEvent.create(view, ViewAttachEvent.Kind.DETACH));
                        }
                    }
                };
                view.addOnAttachStateChangeListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.removeOnAttachStateChangeListener(listener);
                    }
                });
            }
        });
    }

    /**
     * Create an observable which emits on {@code view} detach events. The emitted value is
     * unspecified and should only be used as notification.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> detaches(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        boolean callOnAttach = false;
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                verifyMainThread();

                final View.OnAttachStateChangeListener listener = new View.OnAttachStateChangeListener() {
                    @Override public void onViewAttachedToWindow(@NonNull final View v) {
                        if (callOnAttach && !subscriber.isUnsubscribed()) {
                            subscriber.onNext(null);
                        }
                    }

                    @Override public void onViewDetachedFromWindow(@NonNull final View v) {
                        if (!callOnAttach && !subscriber.isUnsubscribed()) {
                            subscriber.onNext(null);
                        }
                    }
                };
                view.addOnAttachStateChangeListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.removeOnAttachStateChangeListener(listener);
                    }
                });
            }
        });
    }

    /**
     * Create an observable which emits on {@code view} click events. The emitted value is
     * unspecified and should only be used as notification.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link View#setOnClickListener} to observe
     * clicks. Only one observable can be used for a view at a time.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> clicks(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                verifyMainThread();

                View.OnClickListener listener = v -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(null);
                    }
                };
                view.setOnClickListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.setOnClickListener(null);
                    }
                });
            }
        });
    }

    /**
     * Create an observable of {@link DragEvent} for drags on {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link View#setOnDragListener} to observe
     * drags. Only one observable can be used for a view at a time.
     */
    @CheckResult
    @NonNull
    public static Observable<DragEvent> drags(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<DragEvent>() {
            @Override
            public void call(Subscriber<? super DragEvent> subscriber) {
                verifyMainThread();

                View.OnDragListener listener = (v, event) -> {
                    if (Functions.FUNC1_ALWAYS_TRUE.call(event)) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(event);
                        }
                        return true;
                    }
                    return false;
                };
                view.setOnDragListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.setOnDragListener(null);
                    }
                });
            }
        });
    }

    /**
     * Create an observable of {@link DragEvent} for {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link View#setOnDragListener} to observe
     * drags. Only one observable can be used for a view at a time.
     *
     * @param handled Function invoked with each value to determine the return value of the
     *                underlying {@link View.OnDragListener}.
     */
    @CheckResult
    @NonNull
    public static Observable<DragEvent> drags(@NonNull View view,
                                              @NonNull Func1<? super DragEvent, Boolean> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new Observable.OnSubscribe<DragEvent>() {
            @Override
            public void call(Subscriber<? super DragEvent> subscriber) {
                verifyMainThread();

                View.OnDragListener listener = (v, event) -> {
                    if (handled.call(event)) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(event);
                        }
                        return true;
                    }
                    return false;
                };
                view.setOnDragListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.setOnDragListener(null);
                    }
                });
            }
        });
    }

    /**
     * Create an observable for draws on {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link ViewTreeObserver#addOnDrawListener} to
     * observe draws. Multiple observables can be used for a view at a time.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> draws(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                verifyMainThread();

                final ViewTreeObserver.OnDrawListener listener = () -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(null);
                    }
                };

                view.getViewTreeObserver().addOnDrawListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.getViewTreeObserver().removeOnDrawListener(listener);
                    }
                });
            }
        });
    }

    /**
     * Create an observable of booleans representing the focus of {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link View#setOnFocusChangeListener} to observe
     * focus change. Only one observable can be used for a view at a time.
     * <p>
     * <em>Note:</em> A value will be emitted immediately on subscribe.
     */
    @CheckResult
    @NonNull
    public static Observable<Boolean> focusChanges(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {
                verifyMainThread();

                View.OnFocusChangeListener listener = (v, hasFocus) -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(hasFocus);
                    }
                };
                view.setOnFocusChangeListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.setOnFocusChangeListener(null);
                    }
                });

                // Emit initial value.
                subscriber.onNext(view.hasFocus());
            }
        });
    }

    /**
     * Create an observable which emits on {@code view} globalLayout events. The emitted value is
     * unspecified and should only be used as notification.
     * <p></p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link
     * ViewTreeObserver#addOnGlobalLayoutListener} to observe global layouts. Multiple observables
     * can be used for a view at a time.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> globalLayouts(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                verifyMainThread();

                final ViewTreeObserver.OnGlobalLayoutListener listener = () -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(null);
                    }
                };

                view.getViewTreeObserver().addOnGlobalLayoutListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(listener);
                        } else {
                            //noinspection deprecation
                            view.getViewTreeObserver().removeGlobalOnLayoutListener(listener);
                        }
                    }
                });
            }
        });
    }

    /**
     * Create an observable of hover events for {@code view}.
     * <p>
     * <em>Warning:</em> Values emitted by this observable are <b>mutable</b> and part of a shared
     * object pool and thus are <b>not safe</b> to cache or delay reading (such as by observing
     * on a different thread). If you want to cache or delay reading the items emitted then you must
     * map values through a function which calls {@link MotionEvent#obtain(MotionEvent)} or
     * {@link MotionEvent#obtainNoHistory(MotionEvent)} to create a copy.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link View#setOnHoverListener} to observe
     * touches. Only one observable can be used for a view at a time.
     */
    @CheckResult
    @NonNull
    public static Observable<MotionEvent> hovers(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return hovers(view, Functions.FUNC1_ALWAYS_TRUE);
    }

    /**
     * Create an observable of hover events for {@code view}.
     * <p>
     * <em>Warning:</em> Values emitted by this observable are <b>mutable</b> and part of a shared
     * object pool and thus are <b>not safe</b> to cache or delay reading (such as by observing
     * on a different thread). If you want to cache or delay reading the items emitted then you must
     * map values through a function which calls {@link MotionEvent#obtain(MotionEvent)} or
     * {@link MotionEvent#obtainNoHistory(MotionEvent)} to create a copy.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link View#setOnHoverListener} to observe
     * touches. Only one observable can be used for a view at a time.
     *
     * @param handled Function invoked with each value to determine the return value of the
     *                underlying {@link View.OnHoverListener}.
     */
    @CheckResult
    @NonNull
    public static Observable<MotionEvent> hovers(@NonNull View view,
                                                 @NonNull Func1<? super MotionEvent, Boolean> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new Observable.OnSubscribe<MotionEvent>() {
            @Override
            public void call(Subscriber<? super MotionEvent> subscriber) {
                verifyMainThread();

                View.OnHoverListener listener = (v, event) -> {
                    if (handled.call(event)) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(event);
                        }
                        return true;
                    }
                    return false;
                };
                view.setOnHoverListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.setOnHoverListener(null);
                    }
                });
            }
        });
    }

    /**
     * Create an observable which emits on {@code view} layout changes. The emitted value is
     * unspecified and should only be used as notification.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> layoutChanges(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                verifyMainThread();

                final View.OnLayoutChangeListener listener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(null);
                    }
                };
                view.addOnLayoutChangeListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.removeOnLayoutChangeListener(listener);
                    }
                });
            }
        });
    }

    /**
     * Create an observable of layout-change events for {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult @NonNull
    public static Observable<ViewLayoutChangeEvent> layoutChangeEvents(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<ViewLayoutChangeEvent>() {
            @Override
            public void call(Subscriber<? super ViewLayoutChangeEvent> subscriber) {
                verifyMainThread();

                final View.OnLayoutChangeListener listener = (v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(ViewLayoutChangeEvent.create(view, left, top, right, bottom,
                                oldLeft, oldTop, oldRight, oldBottom));
                    }
                };
                view.addOnLayoutChangeListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.removeOnLayoutChangeListener(listener);
                    }
                });
            }
        });
    }

    /**
     * Create an observable which emits on {@code view} long-click events. The emitted value is
     * unspecified and should only be used as notification.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link View#setOnLongClickListener} to observe
     * long clicks. Only one observable can be used for a view at a time.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> longClicks(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                verifyMainThread();

                View.OnLongClickListener listener = v -> {
                    if (Functions.FUNC0_ALWAYS_TRUE.call()) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(null);
                        }
                        return true;
                    }
                    return false;
                };
                view.setOnLongClickListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.setOnLongClickListener(null);
                    }
                });
            }
        });
    }

    /**
     * Create an observable which emits on {@code view} long-click events. The emitted value is
     * unspecified and should only be used as notification.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link View#setOnLongClickListener} to observe
     * long clicks. Only one observable can be used for a view at a time.
     *
     * @param handled Function invoked each occurrence to determine the return value of the
     *                underlying {@link View.OnLongClickListener}.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> longClicks(@NonNull View view, @NonNull Func0<Boolean> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                verifyMainThread();

                View.OnLongClickListener listener = v -> {
                    if (handled.call()) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(null);
                        }
                        return true;
                    }
                    return false;
                };
                view.setOnLongClickListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.setOnLongClickListener(null);
                    }
                });
            }
        });
    }

    /**
     * Create an observable for pre-draws on {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link ViewTreeObserver#addOnPreDrawListener} to
     * observe pre-draws. Multiple observables can be used for a view at a time.
     */
    @CheckResult
    @NonNull
    public static Observable<Void> preDraws(@NonNull View view,
                                            @NonNull Func0<Boolean> proceedDrawingPass) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(proceedDrawingPass, "proceedDrawingPass == null");
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                verifyMainThread();

                final ViewTreeObserver.OnPreDrawListener listener = () -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(null);
                        return proceedDrawingPass.call();
                    }
                    return true;
                };

                view.getViewTreeObserver().addOnPreDrawListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.getViewTreeObserver().removeOnPreDrawListener(listener);
                    }
                });
            }
        });
    }

    /**
     * Create an observable of scroll-change events for {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @TargetApi(M)
    @CheckResult @NonNull
    public static Observable<ViewScrollChangeEvent> scrollChangeEvents(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<ViewScrollChangeEvent>() {
            @Override
            public void call(Subscriber<? super ViewScrollChangeEvent> subscriber) {
                verifyMainThread();

                final View.OnScrollChangeListener listener = (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    if (!subscriber.isUnsubscribed()) {
                        subscriber.onNext(
                                ViewScrollChangeEvent.create(view, scrollX, scrollY, oldScrollX, oldScrollY));
                    }
                };
                view.setOnScrollChangeListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.setOnScrollChangeListener(null);
                    }
                });
            }
        });
    }

    /**
     * Create an observable of integers representing a new system UI visibility for {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses
     * {@link View#setOnSystemUiVisibilityChangeListener} to observe system UI visibility changes.
     * Only one observable can be used for a view at a time.
     */
    @CheckResult
    @NonNull
    public static Observable<Integer> systemUiVisibilityChanges(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                verifyMainThread();

                final View.OnSystemUiVisibilityChangeListener listener =
                        visibility -> {
                            if (!subscriber.isUnsubscribed()) {
                                subscriber.onNext(visibility);
                            }
                        };
                view.setOnSystemUiVisibilityChangeListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override protected void onUnsubscribe() {
                        view.setOnSystemUiVisibilityChangeListener(null);
                    }
                });
            }
        });
    }

    /**
     * Create an observable of touch events for {@code view}.
     * <p>
     * <em>Warning:</em> Values emitted by this observable are <b>mutable</b> and part of a shared
     * object pool and thus are <b>not safe</b> to cache or delay reading (such as by observing
     * on a different thread). If you want to cache or delay reading the items emitted then you must
     * map values through a function which calls {@link MotionEvent#obtain(MotionEvent)} or
     * {@link MotionEvent#obtainNoHistory(MotionEvent)} to create a copy.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link View#setOnTouchListener} to observe
     * touches. Only one observable can be used for a view at a time.
     */
    @CheckResult
    @NonNull
    public static Observable<MotionEvent> touches(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return touches(view, Functions.FUNC1_ALWAYS_TRUE);
    }

    /**
     * Create an observable of touch events for {@code view}.
     * <p>
     * <em>Warning:</em> Values emitted by this observable are <b>mutable</b> and part of a shared
     * object pool and thus are <b>not safe</b> to cache or delay reading (such as by observing
     * on a different thread). If you want to cache or delay reading the items emitted then you must
     * map values through a function which calls {@link MotionEvent#obtain(MotionEvent)} or
     * {@link MotionEvent#obtainNoHistory(MotionEvent)} to create a copy.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link View#setOnTouchListener} to observe
     * touches. Only one observable can be used for a view at a time.
     *
     * @param handled Function invoked with each value to determine the return value of the
     *                underlying {@link View.OnTouchListener}.
     */
    @CheckResult
    @NonNull
    public static Observable<MotionEvent> touches(@NonNull View view,
                                                  @NonNull Func1<? super MotionEvent, Boolean> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");

        return Observable.create(new Observable.OnSubscribe<MotionEvent>() {
            @Override
            public void call(Subscriber<? super MotionEvent> subscriber) {
                verifyMainThread();

                View.OnTouchListener listener = (v, event) -> {
                    if (handled.call(event)) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(event);
                        }
                        return true;
                    }
                    return false;
                };
                view.setOnTouchListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        view.setOnTouchListener(null);
                    }
                });
            }
        });
    }

    /**
     * An action which sets the activated property of {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Action1<? super Boolean> activated(@NonNull final View view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            @Override
            public void call(Boolean value) {
                view.setActivated(value);
            }
        };
    }

    /**
     * An action which sets the clickable property of {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Action1<? super Boolean> clickable(@NonNull final View view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            @Override
            public void call(Boolean value) {
                view.setClickable(value);
            }
        };
    }

    /**
     * An action which sets the enabled property of {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Action1<? super Boolean> enabled(@NonNull final View view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            @Override
            public void call(Boolean value) {
                view.setEnabled(value);
            }
        };
    }

    /**
     * An action which sets the pressed property of {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Action1<? super Boolean> pressed(@NonNull final View view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            @Override
            public void call(Boolean value) {
                view.setPressed(value);
            }
        };
    }

    /**
     * An action which sets the selected property of {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Action1<? super Boolean> selected(@NonNull final View view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Boolean>() {
            @Override
            public void call(Boolean value) {
                view.setSelected(value);
            }
        };
    }

    /**
     * An action which sets the visibility property of {@code view}. {@code false} values use
     * {@code View.GONE}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     */
    @CheckResult
    @NonNull
    public static Action1<? super Boolean> visibility(@NonNull View view) {
        Preconditions.checkNotNull(view, "view == null");
        return visibility(view, View.GONE);
    }

    /**
     * An action which sets the visibility property of {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     *
     * @param visibilityWhenFalse Visibility to set on a {@code false} value ({@code View.INVISIBLE}
     *                            or {@code View.GONE}).
     */
    @CheckResult
    @NonNull
    public static Action1<? super Boolean> visibility(@NonNull final View view,
                                                      final int visibilityWhenFalse) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkArgument(visibilityWhenFalse != View.VISIBLE,
                "Setting visibility to VISIBLE when false would have no effect.");
        Preconditions.checkArgument(visibilityWhenFalse == View.INVISIBLE || visibilityWhenFalse == View.GONE,
                "Must set visibility to INVISIBLE or GONE when false.");
        return new Action1<Boolean>() {
            @Override
            public void call(Boolean value) {
                view.setVisibility(value ? View.VISIBLE : visibilityWhenFalse);
            }
        };
    }

    private RxView() {
        throw new AssertionError("No instances.");
    }
}