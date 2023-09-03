package com.abc.assesment.kalaha.service;

import com.abc.assesment.kalaha.model.KalahaGame;
import com.abc.assesment.kalaha.model.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.abc.assesment.kalaha.constants.GameConstants.*;

@Service
@Slf4j
public class GameServiceImpl implements GameService {
    public KalahaGame createNewGame() {
        KalahaGame kalahaGame = new KalahaGame(DEFAULT_STONES);//creating with default stones
        kalahaGame.setGameStatus(IN_PROGRESS);
        kalahaGame.setPlayersTurn(Player.PLAYER_ONE);
        log.info("New Game Started");
        return kalahaGame;
    }

}
