package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReviewMark {

    @NotNull
    Long reviewId;
    @NotNull
    Long userId;
    @NotNull
    boolean isLike;
}
