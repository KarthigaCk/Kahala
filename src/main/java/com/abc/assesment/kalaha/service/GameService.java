package com.abc.assesment.kalaha.service;

import com.abc.assesment.kalaha.model.KalahaGame;

public interface GameService {
    /**
     * Creates a new game
     *
     * @return new game
     */
    KalahaGame createNewGame();

    /**
     * @param gameId
     * @return
     */
    KalahaGame getGameInfo(Integer gameId);

    /**
     * @param gameId
     * @param pitId
     * @return
     */
    KalahaGame playGame(Integer gameId, Integer pitId);
}
