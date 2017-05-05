package com.example.askoura.rxjavaexample;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class RxJavaSimpleActivity extends AppCompatActivity {

    CompositeDisposable disposable = new CompositeDisposable();
    public int value = 0;

    final Observable<Integer> serverDownloadObservable = Observable.create(emitter -> {
        for (int i = 0; i <= 10; i++) {
            SystemClock.sleep(500); // simulate delay
            emitter.onNext(i);
        }
        emitter.onComplete();
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjavasimple);
        View view = findViewById(R.id.button);
        view.setOnClickListener(v -> {
            // if not disabled you can subscribe multiple times and it is quite mess
            v.setEnabled(false);
            Disposable subscribe = serverDownloadObservable.
                    observeOn(AndroidSchedulers.mainThread()).
                    doOnComplete(() -> v.setEnabled(true)).
                    doOnDispose(() -> v.setEnabled(true)).
                    // UI becomes unresponsive if I subscribe on UI thread
                            subscribeOn(Schedulers.io()).
                            subscribe(integer -> {
                                updateTheUserInterface(integer);
                            });
            // a disposed compositeDisposable will not add a new disposable
            // there is also no way to unDispose, must create a new one
            if (disposable.isDisposed()) {
                disposable = new CompositeDisposable();
            }
            disposable.add(subscribe);
        });
    }

    private void updateTheUserInterface(int integer) {
        TextView view = (TextView) findViewById(R.id.resultView);
        view.setText(String.valueOf(integer));
    }

    @Override
    protected void onStop() {
        super.onStop();
        dispose();
    }

    public void onClick(View view) {
        Toast.makeText(this, "YESS!!", Toast.LENGTH_SHORT).show();
    }

    private void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void stop(View view) {
        dispose();
    }
}