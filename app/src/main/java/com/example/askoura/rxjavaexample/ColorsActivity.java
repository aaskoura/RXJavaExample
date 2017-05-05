package com.example.askoura.rxjavaexample;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ColorsActivity extends AppCompatActivity {

    RecyclerView colorListView;
    SimpleStringAdapter simpleStringAdapter;
    private Disposable disposable;
    private final Observable<String> serverDownloadObservable = Observable.create(emitter -> {
        List<String> list = getColorList();
        for (int i = 0; i < list.size(); i++) {
            SystemClock.sleep(500); // simulate delay
            Log.v("", "emitting: " + i);
            emitter.onNext(list.get(i));
        }
        emitter.onComplete();
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureLayout();
        createObservable();
    }


    private void createObservable() {
        Observable<List<String>> listObservable = Observable.just(getColorList());
        disposable = listObservable.subscribe(colors -> simpleStringAdapter.setStrings(colors));

    }

    private void configureLayout() {
        setContentView(R.layout.activity_colors);
        colorListView = (RecyclerView) findViewById(R.id.color_list);
        colorListView.setLayoutManager(new LinearLayoutManager(this));
        simpleStringAdapter = new SimpleStringAdapter(this);
        colorListView.setAdapter(simpleStringAdapter);
    }

    private static List<String> getColorList() {
        ArrayList<String> colors = new ArrayList<>();
        for (int i = 0; i <= 15; i++) {
            colors.add("item " + i);
        }
        return colors;
    }

    @Override
    protected void onStop() {
        super.onStop();
        dispose();

    }

    public void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void populate(View view) {
        disposable = serverDownloadObservable.
                observeOn(AndroidSchedulers.mainThread()).
                doOnComplete(() -> view.setEnabled(true)).
                doOnDispose(() -> view.setEnabled(true)).
                // UI becomes unresponsive if I subscribe on UI thread
                        subscribeOn(Schedulers.io()).
                        subscribe(color -> {
                            simpleStringAdapter.addAtEnd(color);
                        });

    }

    public void clearList(View view) {
        simpleStringAdapter.clear();
    }

    public void dispose(View view) {
        // when a disposable is disposed, its observer will keep on working till complete
        dispose();
    }
}