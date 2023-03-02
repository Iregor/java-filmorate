package ru.yandex.practicum.filmorate.exception;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class IncorrectObjectIdException extends NullPointerException {
    public IncorrectObjectIdException(String s) {
        super(s);
    }
}
