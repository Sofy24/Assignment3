package com.example.gardenapp;

public interface ExtendedRunnable extends Runnable {
    void write(byte[] bytes);
    void cancel();
}
