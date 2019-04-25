package com.zuluft.mvi.preferences;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;

import com.f2prateek.rx.preferences2.RxSharedPreferences;

import io.reactivex.Observable;


public class PreferencesImpl implements Preferences {

    private final SharedPreferences sharedPreferences;
    private final RxSharedPreferences rxSharedPreferences;
    private final SharedPreferences.Editor editor;


    @SuppressLint("CommitPrefEdits")
    public PreferencesImpl(@NonNull final SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        rxSharedPreferences = RxSharedPreferences.create(sharedPreferences);
        editor = this.sharedPreferences.edit();
    }

    @Override
    public Preferences add(String key, int value) {
        editor.putInt(key, value);
        return this;
    }

    @Override
    public Preferences add(String key, String value) {
        editor.putString(key, value);
        return this;
    }

    @Override
    public Preferences add(String key, boolean value) {
        editor.putBoolean(key, value);
        return this;
    }

    @Override
    public Preferences add(String key, float value) {
        editor.putFloat(key, value);
        return this;
    }

    @Override
    public Preferences remove(String key) {
        editor.remove(key);
        return this;
    }

    @Override
    public Observable<Integer> getObservable(String key, int defaultValue) {
        return rxSharedPreferences.getInteger(key, defaultValue).asObservable();
    }

    @Override
    public Observable<String> getObservable(String key, String defaultValue) {
        return rxSharedPreferences.getString(key, defaultValue).asObservable();
    }

    @Override
    public Observable<Boolean> getObservable(String key, boolean defaultValue) {
        return rxSharedPreferences.getBoolean(key, defaultValue).asObservable();
    }

    @Override
    public Observable<Float> getObservable(String key, float defaultValue) {
        return rxSharedPreferences.getFloat(key, defaultValue).asObservable();
    }

    @Override
    public int get(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    @Override
    public String get(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    @Override
    public boolean get(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    @Override
    public float get(String key, float defaultValue) {
        return sharedPreferences.getFloat(key, defaultValue);
    }

    @Override
    public void commit() {
        editor.commit();
    }

    @Override
    public void commitAsync() {
        editor.apply();
    }

    @Override
    public void clearAllData() {
        editor.clear().commit();
    }
}
