package yixian.website.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BookIdsRequest(@NotEmpty(message = "參數不得為Null") List<Integer> ids) {
}
