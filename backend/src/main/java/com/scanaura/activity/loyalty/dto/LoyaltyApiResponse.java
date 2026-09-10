package com.scanaura.activity.loyalty.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoyaltyApiResponse<T> {

    private final boolean success;

    private final String message;

    private final T data;

    public static <T> LoyaltyApiResponse<T> success(
            String message,
            T data
    ) {
        return new LoyaltyApiResponse<>(
                true,
                message,
                data
        );
    }

    public static <T> LoyaltyApiResponse<T> success(
            T data
    ) {
        return new LoyaltyApiResponse<>(
                true,
                "Success",
                data
        );
    }

    public static <T> LoyaltyApiResponse<T> failure(
            String message
    ) {
        return new LoyaltyApiResponse<>(
                false,
                message,
                null
        );
    }
}