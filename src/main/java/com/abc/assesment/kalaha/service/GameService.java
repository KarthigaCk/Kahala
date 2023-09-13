package com.abc.assesment.kalaha.service;

import com.abc.assesment.kalaha.model.KalahaGame;

public interface GameService {
    /**
     * Creates a new game
     *
     * @return KalahaGame New game with default stones added
     */
    KalahaGame createNewGame();

    /**
     * Get the game details
     *
     * @param gameId
     * @return Kalaha game with details
     */
    KalahaGame getGameInfo(Integer gameId);

    /**
     * Plays the game
     *
     * @param gameId
     * @param pitId
     * @return Kalaha game after making moves with next player and game status info
     */
    KalahaGame playGame(Integer gameId, Integer pitId);
}
