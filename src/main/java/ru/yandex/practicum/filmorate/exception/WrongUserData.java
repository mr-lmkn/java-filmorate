package ru.yandex.practicum.filmorate.exception;

public class WrongUserData extends Throwable {
    public WrongUserData(String message) {
        super(message);
    }
}
