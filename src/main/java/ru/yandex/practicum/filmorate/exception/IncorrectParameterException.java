package ru.yandex.practicum.filmorate.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IncorrectParameterException extends RuntimeException {
    private final String parameter;

    public String getParameter() {
        return parameter;
    }
}