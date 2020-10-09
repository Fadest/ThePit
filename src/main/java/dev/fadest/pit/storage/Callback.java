package dev.fadest.pit.storage;

public abstract class Callback<T> {
    public abstract void onComplete(T result);
}