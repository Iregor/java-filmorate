package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Data
public class Review {

    @Positive(message = "Идентификатор не может быть отрицательным.")
    private Long id;

    @NotBlank
    private String content;

    @NotNull(message = "Тип отзыва не может быть null.")
    private boolean isPositive;

    @Positive(message = "Идентификатор не может быть отрицательным.")
    private Long userId;

    @Positive(message = "Идентификатор не может быть отрицательным.")
    private Long filmId;

    //ограничение не добавлять (может быть null)
    private Long useful;
}
