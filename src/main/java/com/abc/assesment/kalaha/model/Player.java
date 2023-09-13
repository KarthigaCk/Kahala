package com.abc.assesment.kalaha.model;

import lombok.Getter;
/**
 * @author Karthiga
 * Model class for players
 */
@Getter
public enum Player {
    PLAYER_ONE(1),
    PLAYER_TWO(2);
    private Integer id;

    Player(Integer id) {
        this.id = id;
    }

}
