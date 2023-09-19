package com.abc.assesment.kalaha.service;

import com.abc.assesment.kalaha.exception.KalahaGameException;
import com.abc.assesment.kalaha.exception.KalahaGameExceptionCodes;
import com.abc.assesment.kalaha.model.KalahaGame;
import com.abc.assesment.kalaha.model.KalahaPit;
import com.abc.assesment.kalaha.persistance.entity.KalahaGameEntity;
import com.abc.assesment.kalaha.persistance.entity.KalahaPitEntity;
import com.abc.assesment.kalaha.persistance.repository.KalahaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.abc.assesment.kalaha.constants.GameConstants.DEFAULT_STONES;
import static com.abc.assesment.kalaha.constants.GameConstants.IN_PROGRESS;
import static com.abc.assesment.kalaha.constants.GameConstants.PLAYER_ONE_HOUSE_INDEX;
import static com.abc.assesment.kalaha.constants.GameConstants.PLAYER_TWO_HOUSE_INDEX;
import static com.abc.assesment.kalaha.constants.GameConstants.TOTAL_PITS;
import static com.abc.assesment.kalaha.model.Player.PLAYER_ONE;
import static com.abc.assesment.kalaha.model.Player.PLAYER_TWO;
/**
 * @author Karthiga
 * Kalaha game Service
 */
@Service
@Slf4j
public class GameServiceImpl implements GameService {

    private static final AtomicInteger count = new AtomicInteger(0);
    private final KalahaRepository kalahaRepository;

    public GameServiceImpl(KalahaRepository kalahaRepository) {
        this.kalahaRepository = kalahaRepository;
    }
    @Override
    public KalahaGame createNewGame() {
        KalahaGameEntity kalahaGameEntity = KalahaGameEntity.builder()
                                                            .gameId(count.incrementAndGet())
                                                            .gameStatus(IN_PROGRESS)
                                                            .playersTurn(PLAYER_ONE)
                                                            .kalahaPits(addDefaultStones())
                                                            .build();
        kalahaRepository.save(kalahaGameEntity);
        log.info("New Game Started");
        return mapKahalaGameFromEntity(kalahaGameEntity);
    }
    @Override
    public KalahaGame getGameInfo(Integer gameId) {
        KalahaGameEntity kalahaGameEntity = getKalahaGameEntity(gameId);
        log.info("GameService call completed");
        return mapKahalaGameFromEntity(kalahaGameEntity);
    }

    @Override
    public KalahaGame playGame(Integer gameId, Integer pitId) {

        log.info("Play game service started");
        KalahaGameEntity kalahaGameEntity = getKalahaGameEntity(gameId);
        if (!isGameInProgress(kalahaGameEntity.getGameStatus())) {
            throw new KalahaGameException(KalahaGameExceptionCodes.GAME_OVER, kalahaGameEntity.getGameId()
                                                                                + " Game is not in progress. Game status : "
                                                                                + kalahaGameEntity.getGameStatus());
        }
        //If player has selected other player's pit, throw an exception
        if(isInvalidPitSelection(kalahaGameEntity, pitId)){
            throw new KalahaGameException(KalahaGameExceptionCodes.INVALID_PITID_SELECTION, "Please select your own pit"
                                                                                + kalahaGameEntity.getPlayersTurn());
        }

        if (isSelectedPitEmpty(getStonesFromSelectedPit(kalahaGameEntity, pitId))) {
            throw new KalahaGameException(KalahaGameExceptionCodes.INVALID_PITID_SELECTION, "Please select a pit with stones");
        }
        sowStones(kalahaGameEntity,pitId);
        if (isGameOver(kalahaGameEntity)) {
            setWinner(kalahaGameEntity);
        }
        kalahaRepository.save(kalahaGameEntity);
        log.info("Play game service completed");

        return mapKahalaGameFromEntity(kalahaGameEntity);
    }

    private boolean isSelectedPitEmpty(Integer stonesInPit) {
        return stonesInPit == 0;
    }

    private Integer getStonesFromSelectedPit(KalahaGameEntity kalahaGameEntity, Integer pitId) {
        return  kalahaGameEntity.getPitDetailsById(pitId).getStones();
    }

    private boolean isGameInProgress(String gameStatus) {
        return gameStatus.equals(IN_PROGRESS);
    }

    private void sowStones(KalahaGameEntity kalahaGameEntity,Integer pitId) {
        boolean isLastStoneOnPlayerSide = false;
        Integer stonesInPit = getStonesFromSelectedPit(kalahaGameEntity, pitId);
        List<KalahaPitEntity> allPits = kalahaGameEntity.getKalahaPits();
        clearSelectedPit(allPits, pitId);
        for (int i = 1; i <= stonesInPit; i++) {
            //Handle circular movement between pits
            pitId = (pitId % TOTAL_PITS);
            if (i == stonesInPit) {
                /*The last stone lands in an own empty pit, the player
            captures his own stone and all stones in the opposite pit */
                int nextPitId = pitId + 1;
                if (isNextEmptyPitOwnedByPlayer(kalahaGameEntity, nextPitId)) {
                        captureStonesFromOpponent(kalahaGameEntity, nextPitId, allPits);
                        break;
                }
                // If the last stone is added in the player's house they get one more chance
                if (shouldGiveExtraTurn(kalahaGameEntity, pitId)) {
                    isLastStoneOnPlayerSide = true;
                }
            }
            //Skipping  opponent's house pit
            if (shouldSkipOthersHousePit(kalahaGameEntity, pitId)) {
                i--;
                pitId++;
                continue;
            }
            allPits.get(pitId).addStones(1);
            pitId++;
        }
        if (!isLastStoneOnPlayerSide) {
            kalahaGameEntity.setPlayersTurn(kalahaGameEntity.getPlayersTurn() == PLAYER_ONE ? PLAYER_TWO : PLAYER_ONE);
        }
        kalahaGameEntity.setKalahaPits(allPits);
    }
    private void clearSelectedPit(List<KalahaPitEntity> allPits, int pitId) {
        allPits.get(pitId - 1).clear();
    }
    private boolean shouldGiveExtraTurn(KalahaGameEntity kalahaGameEntity, Integer pitId) {
        return ((kalahaGameEntity.getPlayersTurn() == PLAYER_ONE && pitId == PLAYER_ONE_HOUSE_INDEX)
                || (kalahaGameEntity.getPlayersTurn() == PLAYER_TWO && pitId == PLAYER_TWO_HOUSE_INDEX));
    }

    private boolean shouldSkipOthersHousePit(KalahaGameEntity kalahaGameEntity, Integer pitId) {
        return ((kalahaGameEntity.getPlayersTurn() != PLAYER_ONE && pitId == PLAYER_ONE_HOUSE_INDEX)
                || (kalahaGameEntity.getPlayersTurn() != PLAYER_TWO  && pitId == PLAYER_TWO_HOUSE_INDEX));
    }

    private void captureStonesFromOpponent(KalahaGameEntity kalahaGameEntity, Integer nextPitId, List<KalahaPitEntity> allPits) {
        int oppositePitId = TOTAL_PITS - nextPitId;
        int stonesInOppPit = kalahaGameEntity.getPitDetailsById(oppositePitId).getStones();
        int housePitIndex = kalahaGameEntity.getPlayersTurn() == PLAYER_ONE ? PLAYER_ONE_HOUSE_INDEX : PLAYER_TWO_HOUSE_INDEX;
        allPits.get(housePitIndex).addStones(stonesInOppPit + 1);
        allPits.get(oppositePitId - 1).clear();
    }

    private boolean isNextEmptyPitOwnedByPlayer(KalahaGameEntity kalahaGameEntity, Integer nextPitId) {
        KalahaPitEntity nextPit = kalahaGameEntity.getPitDetailsById(nextPitId);
        return ((kalahaGameEntity.getPlayersTurn() == PLAYER_ONE && nextPitId < 7)
                   || (kalahaGameEntity.getPlayersTurn() == PLAYER_TWO && (nextPitId > 7 && nextPitId < 14)))
                && nextPit.isEmpty();
    }

    private boolean isInvalidPitSelection(KalahaGameEntity kalahaGameEntity, Integer pitId) {
        return ((kalahaGameEntity.getPlayersTurn() == PLAYER_ONE && pitId > 6)
                || (kalahaGameEntity.getPlayersTurn() == PLAYER_TWO && pitId < 8));
    }

    private void setWinner(KalahaGameEntity kalahaGameEntity) {
        Integer playerAStones = kalahaGameEntity.getKalahaPits().get(PLAYER_ONE_HOUSE_INDEX).getStones();
        Integer playerBStones = kalahaGameEntity.getKalahaPits().get(PLAYER_TWO_HOUSE_INDEX).getStones();

        switch (playerAStones.compareTo(playerBStones)) {
            case 0:
                kalahaGameEntity.setGameStatus("It is a tie!");
                break;
            case -1:
                kalahaGameEntity.setGameStatus("Player 2 wins");
                break;
            case 1:
                kalahaGameEntity.setGameStatus("Player 1 wins");
                break;
        }
    }

    private boolean isGameOver(KalahaGameEntity kalahaGameEntity) {

        if (isPlayersNonHousePitsEmpty(PLAYER_ONE.getPlayerId(), kalahaGameEntity.getKalahaPits())
                                       || isPlayersNonHousePitsEmpty(PLAYER_TWO.getPlayerId(), kalahaGameEntity.getKalahaPits())) {
            addAllStonesToHouseIndex(PLAYER_ONE.getPlayerId(), kalahaGameEntity.getKalahaPits());
            addAllStonesToHouseIndex(PLAYER_TWO.getPlayerId(), kalahaGameEntity.getKalahaPits());
            kalahaGameEntity.getKalahaPits().stream()
                                            .filter(kalahaPit -> kalahaPit.getPitId() != 7 && kalahaPit.getPitId() != 14)
                                            .forEach(KalahaPitEntity::clear);
            return true;
        }
        return false;
    }

    private void addAllStonesToHouseIndex(Integer playerId, List<KalahaPitEntity> kalahaPits) {
        if (playerId.equals(PLAYER_ONE.getPlayerId())) {
            OptionalInt optionalIntSum = kalahaPits.stream()
                                                   .filter(pit -> pit.getPitId() < 7)
                                                   .mapToInt(KalahaPitEntity::getStones)
                                                   .reduce(Integer::sum);
            int sumOfStones = optionalIntSum.isPresent() ? optionalIntSum.getAsInt() : 0;
            kalahaPits.get(PLAYER_ONE_HOUSE_INDEX).addStones(sumOfStones);
        }

        if (playerId.equals(PLAYER_TWO.getPlayerId())) {
            OptionalInt optionalIntSum = kalahaPits.stream()
                                                   .filter(pit -> pit.getPitId() > 7 && pit.getPitId() < 14)
                                                   .mapToInt(KalahaPitEntity::getStones)
                                                   .reduce(Integer::sum);
            int sumOfStones = optionalIntSum.isPresent() ? optionalIntSum.getAsInt() : 0;
            kalahaPits.get(PLAYER_TWO_HOUSE_INDEX).addStones(sumOfStones);
        }

    }

    private boolean isPlayersNonHousePitsEmpty(Integer playerId, List<KalahaPitEntity> kalahaPits) {

        List<KalahaPitEntity> relevantPits = playerId.equals(PLAYER_ONE.getPlayerId())
                                             ? kalahaPits.stream()
                                                         .filter(pit -> pit.getPitId() < 7)
                                                         .collect(Collectors.toList())
                                             : kalahaPits.stream()
                                                         .filter(pit -> pit.getPitId() > 7 && pit.getPitId() < 14)
                                                         .collect(Collectors.toList());

        return relevantPits.stream()
                           .allMatch(KalahaPitEntity::isEmpty);
    }

    private List<KalahaPitEntity> addDefaultStones() {
        return IntStream.rangeClosed(1, TOTAL_PITS)
                        .mapToObj(i -> new KalahaPitEntity(i, (i == 7 || i == 14) ? 0 : DEFAULT_STONES))
                        .collect(Collectors.toList());
    }

    private KalahaGame mapKahalaGameFromEntity(KalahaGameEntity kalahaGameEntity) {
        return KalahaGame.builder()
                         .gameId(kalahaGameEntity.getGameId())
                         .gameStatus(kalahaGameEntity.getGameStatus())
                         .playersTurn(kalahaGameEntity.getPlayersTurn())
                         .kalahaPits(mapKahalaPitsFromEntity(kalahaGameEntity.getKalahaPits()))
                         .build();
    }

    private List<KalahaPit> mapKahalaPitsFromEntity(List<KalahaPitEntity> kalahaPitEntities) {
        return kalahaPitEntities.stream()
                                .map(kalahaPitEntity -> KalahaPit.builder()
                                                                 .pitId(kalahaPitEntity.getPitId())
                                                                 .stones(kalahaPitEntity.getStones())
                                                                 .build())
                                .collect(Collectors.toList());
    }

    private KalahaGameEntity getKalahaGameEntity(Integer gameId) {

        Optional<KalahaGameEntity> kalahaGameEntity = kalahaRepository.findById(gameId);
        if (kalahaGameEntity.isEmpty())
            throw new KalahaGameException(KalahaGameExceptionCodes.GAME_ID_NOT_FOUND, "Game id is not available");
        return kalahaGameEntity.get();
    }
}