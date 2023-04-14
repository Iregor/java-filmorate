package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Builder
public class ReviewMark {

    @NotNull
    private Long reviewId;

    @NotNull
    private Long userId;

    @NotNull
    private boolean isLike;
}
