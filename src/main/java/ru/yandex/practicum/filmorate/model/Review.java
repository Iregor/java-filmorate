package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Review {

    private Long reviewId;

    @NotBlank
    private String content;

    @JsonProperty("isPositive")
    @NotNull(message = "Тип отзыва не может быть null.")
    private Boolean positive;

    @NotNull
    private Long userId;

    @NotNull
    private Long filmId;

    private Long useful;
}
