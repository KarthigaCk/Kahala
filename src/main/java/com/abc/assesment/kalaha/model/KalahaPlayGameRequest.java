package com.abc.assesment.kalaha.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import javax.validation.constraints.Min;

/**
 * @author Karthiga
 * Model class for Kalaha play game request
 */
@Getter
@Setter
@Builder
public class KalahaPlayGameRequest {
    @NonNull
    @Min(value = 1, message = "GameId cannot be less than 1")
    private Integer gameId;
    @NonNull
    private Integer selectedPitId;
}
