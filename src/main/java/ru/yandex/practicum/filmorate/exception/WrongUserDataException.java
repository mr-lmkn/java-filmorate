package ru.yandex.practicum.filmorate.exception;

public class WrongUserDataException extends Throwable {
    public WrongUserDataException(String message) {
        super(message);
    }
}
