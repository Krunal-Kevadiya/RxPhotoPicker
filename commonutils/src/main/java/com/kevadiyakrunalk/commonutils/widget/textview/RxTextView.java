package com.kevadiyakrunalk.commonutils.widget.textview;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.kevadiyakrunalk.commonutils.internal.Functions;
import com.kevadiyakrunalk.commonutils.internal.Preconditions;

import rx.Observable;
import rx.Subscriber;
import rx.android.MainThreadSubscription;
import rx.functions.Action1;
import rx.functions.Func1;

import static rx.android.MainThreadSubscription.verifyMainThread;

/**
 * Static factory methods for creating {@linkplain Observable observables} and {@linkplain Action1
 * actions}** for {@link TextView}.
 */
public final class RxTextView {
    /**
     * Create an observable of editor actions on {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link TextView.OnEditorActionListener} to
     * observe actions. Only one observable can be used for a view at a time.
     *
     * @param view the view
     * @return the observable
     */
    @CheckResult
    @NonNull
    public static Observable<Integer> editorActions(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return editorActions(view, Functions.FUNC1_ALWAYS_TRUE);
    }

    /**
     * Create an observable of editor actions on {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link TextView.OnEditorActionListener} to
     * observe actions. Only one observable can be used for a view at a time.
     *
     * @param view    the view
     * @param handled Function invoked each occurrence to determine the return value of the                underlying {@link TextView.OnEditorActionListener}.
     * @return the observable
     */
    @CheckResult
    @NonNull
    public static Observable<Integer> editorActions(@NonNull TextView view,
                                                    @NonNull Func1<? super Integer, Boolean> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                verifyMainThread();

                TextView.OnEditorActionListener listener = (v, actionId, event) -> {
                    if (handled.call(actionId)) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(actionId);
                        }
                        return true;
                    }
                    return false;
                };
                view.setOnEditorActionListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        view.setOnEditorActionListener(null);
                    }
                });
            }
        });
    }

    /**
     * Create an observable of editor action events on {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link TextView.OnEditorActionListener} to
     * observe actions. Only one observable can be used for a view at a time.
     *
     * @param view the view
     * @return the observable
     */
    @CheckResult
    @NonNull
    public static Observable<TextViewEditorActionEvent> editorActionEvents(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return editorActionEvents(view, Functions.FUNC1_ALWAYS_TRUE);
    }

    /**
     * Create an observable of editor action events on {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Warning:</em> The created observable uses {@link TextView.OnEditorActionListener} to
     * observe actions. Only one observable can be used for a view at a time.
     *
     * @param view    the view
     * @param handled Function invoked each occurrence to determine the return value of the                underlying {@link TextView.OnEditorActionListener}.
     * @return the observable
     */
    @CheckResult
    @NonNull
    public static Observable<TextViewEditorActionEvent> editorActionEvents(@NonNull TextView view,
                                                                           @NonNull Func1<? super TextViewEditorActionEvent, Boolean> handled) {
        Preconditions.checkNotNull(view, "view == null");
        Preconditions.checkNotNull(handled, "handled == null");
        return Observable.create(new Observable.OnSubscribe<TextViewEditorActionEvent>() {
            @Override
            public void call(Subscriber<? super TextViewEditorActionEvent> subscriber) {
                verifyMainThread();

                TextView.OnEditorActionListener listener = (v, actionId, keyEvent) -> {
                    TextViewEditorActionEvent event = TextViewEditorActionEvent.create(v, actionId, keyEvent);
                    if (handled.call(event)) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(event);
                        }
                        return true;
                    }
                    return false;
                };
                view.setOnEditorActionListener(listener);

                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        view.setOnEditorActionListener(null);
                    }
                });
            }
        });
    }

    /**
     * Create an observable of character sequences for text changes on {@code view}.
     * <p>
     * <em>Warning:</em> Values emitted by this observable are <b>mutable</b> and owned by the host
     * {@code TextView} and thus are <b>not safe</b> to cache or delay reading (such as by observing
     * on a different thread). If you want to cache or delay reading the items emitted then you must
     * map values through a function which calls {@link String#valueOf} or
     * {@link CharSequence#toString() .toString()} to create a copy.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Note:</em> A value will be emitted immediately on subscribe.
     *
     * @param view the view
     * @return the observable
     */
    @CheckResult
    @NonNull
    public static Observable<CharSequence> textChanges(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<CharSequence>() {
            @Override
            public void call(Subscriber<? super CharSequence> subscriber) {
                verifyMainThread();

                final TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(s);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                };
                view.addTextChangedListener(watcher);

                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        view.removeTextChangedListener(watcher);
                    }
                });

                // Emit initial value.
                subscriber.onNext(view.getText());
            }
        });
    }

    /**
     * Create an observable of text change events for {@code view}.
     * <p>
     * <em>Warning:</em> Values emitted by this observable contain a <b>mutable</b>
     * {@link CharSequence} owned by the host {@code TextView} and thus are <b>not safe</b> to cache
     * or delay reading (such as by observing on a different thread). If you want to cache or delay
     * reading the items emitted then you must map values through a function which calls
     * {@link String#valueOf} or {@link CharSequence#toString() .toString()} to create a copy.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Note:</em> A value will be emitted immediately on subscribe.
     *
     * @param view the view
     * @return the observable
     */
    @CheckResult
    @NonNull
    public static Observable<TextViewTextChangeEvent> textChangeEvents(@NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<TextViewTextChangeEvent>() {
            @Override
            public void call(Subscriber<? super TextViewTextChangeEvent> subscriber) {
                verifyMainThread();

                final TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(TextViewTextChangeEvent.create(view, s, start, before, count));
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                };
                view.addTextChangedListener(watcher);

                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        view.removeTextChangedListener(watcher);
                    }
                });

                // Emit initial value.
                subscriber.onNext(TextViewTextChangeEvent.create(view, view.getText(), 0, 0, 0));
            }
        });
    }

    /**
     * Create an observable of before text change events for {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Note:</em> A value will be emitted immediately on subscribe.
     *
     * @param view the view
     * @return the observable
     */
    @CheckResult
    @NonNull
    public static Observable<TextViewBeforeTextChangeEvent> beforeTextChangeEvents(
            @NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<TextViewBeforeTextChangeEvent>() {
            @Override
            public void call(Subscriber<? super TextViewBeforeTextChangeEvent> subscriber) {
                verifyMainThread();

                final TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(TextViewBeforeTextChangeEvent.create(view, s, start, count, after));
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                };
                view.addTextChangedListener(watcher);

                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        view.removeTextChangedListener(watcher);
                    }
                });

                // Emit initial value.
                subscriber.onNext(TextViewBeforeTextChangeEvent.create(view, view.getText(), 0, 0, 0));
            }
        });
    }

    /**
     * Create an observable of after text change events for {@code view}.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     * <p>
     * <em>Note:</em> A value will be emitted immediately on subscribe.
     *
     * @param view the view
     * @return the observable
     */
    @CheckResult
    @NonNull
    public static Observable<TextViewAfterTextChangeEvent> afterTextChangeEvents(
            @NonNull TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return Observable.create(new Observable.OnSubscribe<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(Subscriber<? super TextViewAfterTextChangeEvent> subscriber) {
                verifyMainThread();

                final TextWatcher watcher = new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (!subscriber.isUnsubscribed()) {
                            subscriber.onNext(TextViewAfterTextChangeEvent.create(view, s));
                        }
                    }
                };
                view.addTextChangedListener(watcher);

                subscriber.add(new MainThreadSubscription() {
                    @Override
                    protected void onUnsubscribe() {
                        view.removeTextChangedListener(watcher);
                    }
                });

                // Emit initial value.
                subscriber.onNext(TextViewAfterTextChangeEvent.create(view, view.getEditableText()));
            }
        });
    }

    /**
     * An action which sets the text property of {@code view} with character sequences.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     *
     * @param view the view
     * @return the action 1
     */
    @CheckResult
    @NonNull
    public static Action1<? super CharSequence> text(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<CharSequence>() {
            @Override
            public void call(CharSequence text) {
                view.setText(text);
            }
        };
    }

    /**
     * An action which sets the text property of {@code view} string resource IDs.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     *
     * @param view the view
     * @return the action 1
     */
    @CheckResult
    @NonNull
    public static Action1<? super Integer> textRes(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            @Override
            public void call(Integer textRes) {
                view.setText(textRes);
            }
        };
    }

    /**
     * An action which sets the error property of {@code view} with character sequences.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     *
     * @param view the view
     * @return the action 1
     */
    @CheckResult
    @NonNull
    public static Action1<? super CharSequence> error(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<CharSequence>() {
            @Override
            public void call(CharSequence text) {
                view.setError(text);
            }
        };
    }

    /**
     * An action which sets the error property of {@code view} string resource IDs.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     *
     * @param view the view
     * @return the action 1
     */
    @CheckResult
    @NonNull
    public static Action1<? super Integer> errorRes(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            @Override
            public void call(Integer textRes) {
                view.setError(view.getContext().getResources().getText(textRes));
            }
        };
    }

    /**
     * An action which sets the hint property of {@code view} with character sequences.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     *
     * @param view the view
     * @return the action 1
     */
    @CheckResult
    @NonNull
    public static Action1<? super CharSequence> hint(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<CharSequence>() {
            @Override
            public void call(CharSequence hint) {
                view.setHint(hint);
            }
        };
    }

    /**
     * An action which sets the hint property of {@code view} string resource IDs.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     *
     * @param view the view
     * @return the action 1
     */
    @CheckResult
    @NonNull
    public static Action1<? super Integer> hintRes(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            @Override
            public void call(Integer hintRes) {
                view.setHint(hintRes);
            }
        };
    }

    /**
     * An action which sets the color property of {@code view} with color integer.
     * <p>
     * <em>Warning:</em> The created observable keeps a strong reference to {@code view}. Unsubscribe
     * to free this reference.
     *
     * @param view the view
     * @return the action 1
     */
    @CheckResult
    @NonNull
    public static Action1<? super Integer> color(@NonNull final TextView view) {
        Preconditions.checkNotNull(view, "view == null");
        return new Action1<Integer>() {
            @Override
            public void call(Integer color) {
                view.setTextColor(color);
            }
        };
    }

    private RxTextView() {
        throw new AssertionError("No instances.");
    }
}