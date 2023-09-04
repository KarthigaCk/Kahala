package com.abc.assesment.kalaha.service;

import com.abc.assesment.kalaha.exception.KalahaGameException;
import com.abc.assesment.kalaha.exception.KalahaGameExceptionCodes;
import com.abc.assesment.kalaha.model.KalahaGame;
import com.abc.assesment.kalaha.model.KalahaPit;
import com.abc.assesment.kalaha.repository.KalahaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.abc.assesment.kalaha.constants.GameConstants.*;
import static com.abc.assesment.kalaha.model.Player.PLAYER_ONE;

@Service
@Slf4j
public class GameServiceImpl implements GameService {

    @Autowired
    KalahaRepository kalahaRepository;

    private static final AtomicInteger count = new AtomicInteger(0);

    public KalahaGame createNewGame() {

        KalahaGame kalahaGame = KalahaGame.builder().gameId(count.incrementAndGet()).gameStatus(IN_PROGRESS).playersTurn(PLAYER_ONE).kalahaPits(
                addDefaultStones(DEFAULT_STONES)).build();
        kalahaRepository.save(kalahaGame);
        log.info("New Game Started");
        return kalahaGame;
    }

    @Override
    public KalahaGame getGame(Integer gameId) {

        Optional<KalahaGame> kalahaGame = kalahaRepository.findById(gameId);
        if(kalahaGame.isEmpty())
            throw new KalahaGameException(KalahaGameExceptionCodes.GAME_ID_NOT_FOUND, "Game id is not present");
        log.info("GameService call completed");
        return kalahaGame.get();
    }

    private List<KalahaPit> addDefaultStones(int stones) { //creating with default stones
        List<KalahaPit> kalahaPits = new ArrayList<>();
        for (int i = 1; i <= TOTAL_PITS; i++) {
            if (i == 7 || i == 14)//player 1 & 2 kalaha pit
            {
                kalahaPits.add(new KalahaPit(i, 0));
                continue;
            }
            kalahaPits.add(new KalahaPit(i, stones));
        }
        return kalahaPits;
    }

}
