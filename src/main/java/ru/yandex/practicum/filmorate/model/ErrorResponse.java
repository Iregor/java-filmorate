package ru.yandex.practicum.filmorate.model;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class ErrorResponse {
    String error;
    String description;
}