package com.example.dminfo.model.observer;

public interface Sujeito {
    void add(Observer observer);
    void remover(Observer observer);
    void notificar();
}