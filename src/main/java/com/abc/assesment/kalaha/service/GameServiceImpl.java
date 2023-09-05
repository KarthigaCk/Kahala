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
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;

import static com.abc.assesment.kalaha.constants.GameConstants.*;
import static com.abc.assesment.kalaha.model.Player.PLAYER_ONE;
import static com.abc.assesment.kalaha.model.Player.PLAYER_TWO;

@Service
@Slf4j
public class GameServiceImpl implements GameService {

    private static final AtomicInteger count = new AtomicInteger(0);
    @Autowired
    KalahaRepository kalahaRepository;

    public KalahaGame createNewGame() {

        KalahaGame kalahaGame = KalahaGame.builder().gameId(count.incrementAndGet()).gameStatus(
                IN_PROGRESS).playersTurn(PLAYER_ONE).kalahaPits(
                addDefaultStones(DEFAULT_STONES)).build();
        kalahaRepository.save(kalahaGame);
        log.info("New Game Started");
        return kalahaGame;
    }

    @Override
    public KalahaGame getGame(Integer gameId) {

        Optional<KalahaGame> kalahaGame = kalahaRepository.findById(gameId);
        if (kalahaGame.isEmpty())
            throw new KalahaGameException(KalahaGameExceptionCodes.GAME_ID_NOT_FOUND, "Game id is not available");
        log.info("GameService call completed");
        return kalahaGame.get();
    }

    @Override
    public KalahaGame playGame(Integer gameId, Integer pitId) {

        log.info("Play game service started");
        KalahaGame kalahaGame = getGame(gameId);

        if (!kalahaGame.getGameStatus().equals(IN_PROGRESS)) {
            throw new KalahaGameException(KalahaGameExceptionCodes.GAME_OVER,
                                          kalahaGame.getGameId() + " Game is not in progress. Game status : " + kalahaGame.getGameStatus());
        }

        //If player has selected other player's pit, throw an exception
        validatePitSelection(kalahaGame, pitId);

        //Get stones in the pit
        KalahaPit selectedPit = kalahaGame.getPitDetailsById(pitId);
        Integer stonesInPit = selectedPit.getStones();

        // Check if the selected pit has stones
        if (stonesInPit == 0) {
            throw new KalahaGameException(KalahaGameExceptionCodes.INVALID_SELECTION,
                                          "Please select a pit with stones");
        }

        List<KalahaPit> allPits = kalahaGame.getKalahaPits();
        //clear the chosen pit
        allPits.get(pitId - 1).clear();

        boolean houseIndex = false;

        for (int i = 1; i <= stonesInPit; i++) {

            if (i == stonesInPit) {//last stone
                int nextPitId = pitId + 1;
                boolean isNextPitOwn = isNextPitOwn(kalahaGame, nextPitId);
                if (isNextPitOwn) {
                    KalahaPit nextPit = kalahaGame.getPitDetailsById(nextPitId);
                    if (nextPit.isEmpty()) {//capturing stones
                        captureStones(kalahaGame, nextPitId, allPits);
                        break;
                    }
                }
            }

            // Handle circular movement between pits
            pitId = (pitId % TOTAL_PITS);//check if this works

            //skipping  opposite player's house pit
            if (shouldSkipOthersHousePit(kalahaGame, pitId)) {
                i--;
                pitId++;
                continue;
            }

            // if the last stone is added in the player's house they get one more chance
            if (i == stonesInPit && shouldGiveExtraTurn(kalahaGame, pitId)) {
                houseIndex = true;
            }

            //adding stones
            allPits.get(pitId).addStones(1);

            pitId++;

        }
        kalahaGame.setKalahaPits(allPits);

        if (!houseIndex) {
            kalahaGame.setPlayersTurn(kalahaGame.getPlayersTurn() == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE);
        }

        if (isGameOver(kalahaGame)) {
            setWinner(kalahaGame);
        }

        kalahaRepository.save(kalahaGame);

        log.info("Play game service completed");

        return kalahaGame;
    }

    private boolean shouldGiveExtraTurn(KalahaGame kalahaGame, Integer pitId) {
        return ((kalahaGame.getPlayersTurn() == PLAYER_ONE && pitId == PLAYER_ONE_HOUSE_PIT)
                || (kalahaGame.getPlayersTurn() == PLAYER_TWO && pitId == PLAYER_TWO_HOUSE_PIT));
    }

    private boolean shouldSkipOthersHousePit(KalahaGame kalahaGame, Integer pitId) {
        return ((pitId == PLAYER_ONE_HOUSE_PIT && kalahaGame.getPlayersTurn() != PLAYER_ONE)
                || (pitId == PLAYER_TWO_HOUSE_PIT && kalahaGame.getPlayersTurn() != PLAYER_TWO));
    }

    private void captureStones(KalahaGame kalahaGame, Integer nextPitId, List<KalahaPit> allPits) {
        int oppositePitIndex = TOTAL_PITS - nextPitId;
        int stonesInOppPit = kalahaGame.getPitDetailsById(oppositePitIndex).getStones();
        int housePitIndex = kalahaGame.getPlayersTurn() == PLAYER_ONE ? PLAYER_ONE_HOUSE_PIT : PLAYER_TWO_HOUSE_PIT;
        allPits.get(housePitIndex).addStones(stonesInOppPit + 1);
        allPits.get(oppositePitIndex - 1).clear();
    }

    private boolean isNextPitOwn(KalahaGame kalahaGame, Integer nextPitId) {
        return ((kalahaGame.getPlayersTurn() == PLAYER_ONE && nextPitId < 7)
                || (kalahaGame.getPlayersTurn() == PLAYER_TWO && (nextPitId > 7 && nextPitId < 14)));
    }

    private void validatePitSelection(KalahaGame kalahaGame, Integer pitId) {

        if ((kalahaGame.getPlayersTurn() == PLAYER_ONE && pitId > 6)
                || (kalahaGame.getPlayersTurn() == PLAYER_TWO && pitId < 8)) {
            throw new KalahaGameException(KalahaGameExceptionCodes.INVALID_SELECTION,
                                          "Please select your own pit" + kalahaGame.getPlayersTurn());
        }

    }

    private void setWinner(KalahaGame kalahaGame) {
        Integer playerAStones = kalahaGame.getKalahaPits().get(PLAYER_ONE_HOUSE_PIT).getStones();
        Integer playerBStones = kalahaGame.getKalahaPits().get(PLAYER_TWO_HOUSE_PIT).getStones();

        switch (playerAStones.compareTo(playerBStones)) {
            case 0:
                kalahaGame.setGameStatus("It is a tie!");
                break;
            case -1:
                kalahaGame.setGameStatus("Player 2 wins");
                break;
            case 1:
                kalahaGame.setGameStatus("Player 1 wins");
                break;
        }
    }

    private boolean isGameOver(KalahaGame kalahaGame) {

        if (isPlayersNonHousePitsEmpty(PLAYER_ONE.getId(),
                                       kalahaGame.getKalahaPits()) || isPlayersNonHousePitsEmpty(
                PLAYER_TWO.getId(),
                kalahaGame.getKalahaPits())) {

            addAllStonesToHouseIndex(PLAYER_ONE.getId(), kalahaGame.getKalahaPits());
            addAllStonesToHouseIndex(PLAYER_TWO.getId(), kalahaGame.getKalahaPits());
            kalahaGame.getKalahaPits().stream().filter(
                    kalahaPit -> kalahaPit.getPitId() != 7 && kalahaPit.getPitId() != 14).forEach(KalahaPit::clear);
            return true;
        }
        return false;
    }

    private void addAllStonesToHouseIndex(Integer id, List<KalahaPit> kalahaPits) {
        if (id == PLAYER_ONE.getId()) {
            OptionalInt optionalIntSum = kalahaPits.stream().filter(pit -> pit.getPitId() < 7).mapToInt(
                    KalahaPit::getStones).reduce(Integer::sum);
            int sumOfStones = optionalIntSum.isPresent() ? optionalIntSum.getAsInt() : 0;
            kalahaPits.get(PLAYER_ONE_HOUSE_PIT).addStones(sumOfStones);
        }

        if (id == PLAYER_TWO.getId()) {
            OptionalInt optionalIntSum = kalahaPits.stream().filter(
                    pit -> pit.getPitId() > 7 && pit.getPitId() < 14).mapToInt(KalahaPit::getStones).reduce(
                    Integer::sum);
            int sumOfStones = optionalIntSum.isPresent() ? optionalIntSum.getAsInt() : 0;
            kalahaPits.get(PLAYER_TWO_HOUSE_PIT).addStones(sumOfStones);
        }

    }

    private boolean isPlayersNonHousePitsEmpty(Integer id, List<KalahaPit> kalahaPits) {

        if (id == PLAYER_ONE.getId()) {
            return kalahaPits.stream().filter(pit -> pit.getPitId() < 7).allMatch(KalahaPit::isEmpty);
        }

        return kalahaPits.stream().filter(pit -> pit.getPitId() > 7 && pit.getPitId() < 14).allMatch(
                KalahaPit::isEmpty);

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

