package com.abc.assesment.kalaha.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
/**
 * @author Karthiga
 * Model class for Kalaha game
 */
@Builder
@Getter
@Setter
public class KalahaGame {
    private Integer gameId;
    private Player playersTurn;
    private String gameStatus;
    private List<KalahaPit> kalahaPits;
}