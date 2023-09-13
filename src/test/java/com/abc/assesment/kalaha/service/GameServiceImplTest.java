package com.abc.assesment.kalaha.service;

import com.abc.assesment.kalaha.exception.KalahaGameException;
import com.abc.assesment.kalaha.model.KalahaGame;
import com.abc.assesment.kalaha.persistance.entity.KalahaGameEntity;
import com.abc.assesment.kalaha.persistance.entity.KalahaPitEntity;
import com.abc.assesment.kalaha.persistance.repository.KalahaRepository;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.abc.assesment.kalaha.constants.GameConstants.DEFAULT_STONES;
import static com.abc.assesment.kalaha.constants.GameConstants.IN_PROGRESS;
import static com.abc.assesment.kalaha.constants.GameConstants.TOTAL_PITS;
import static com.abc.assesment.kalaha.model.Player.PLAYER_ONE;
import static com.abc.assesment.kalaha.model.Player.PLAYER_TWO;
import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

/**
 *Test cases for {@link GameServiceImpl}
 */
public class GameServiceImplTest {
    private GameService gameService;
    private KalahaRepository mockKalahaRepository;

    @BeforeMethod
    public void setup() {
        mockKalahaRepository = createMock(KalahaRepository.class);
        gameService = new GameServiceImpl(mockKalahaRepository);
    }

    @AfterMethod
    public void clear() {
        resetAll();
    }

    @Test
    public void createNewGame_test() {

        expect(mockKalahaRepository.save(anyObject()))
                                   .andReturn(new Object());
        replayAll();
        KalahaGame kalahaGame = gameService.createNewGame();
        verifyAll();
        assertNotNull(kalahaGame);
        assertEquals(Integer.valueOf(1), kalahaGame.getGameId());
        assertEquals(IN_PROGRESS, kalahaGame.getGameStatus());
        assertEquals(PLAYER_ONE, kalahaGame.getPlayersTurn());
        assertEquals(Integer.valueOf(DEFAULT_STONES), kalahaGame.getKalahaPits().get(1).getStones());

    }

    @Test
    public void getGameInfo_test() {
        expect(mockKalahaRepository.findById(anyInt()))
                                   .andReturn(Optional.ofNullable(getGameEntityTestData()));
        replayAll();
        KalahaGame kalahaGame = gameService.getGameInfo(1);
        verifyAll();
        assertNotNull(kalahaGame);
        assertEquals(Integer.valueOf(1), kalahaGame.getGameId());
        assertEquals(IN_PROGRESS, kalahaGame.getGameStatus());
        assertEquals(PLAYER_ONE, kalahaGame.getPlayersTurn());
        assertEquals(Integer.valueOf(3), kalahaGame.getKalahaPits().get(6).getStones());
    }

    @Test
    public void playGame_Test() {
        expect(mockKalahaRepository.findById(anyInt()))
                                   .andReturn(Optional.ofNullable(getGameEntityTestData()));
        expect(mockKalahaRepository.save(anyObject()))
                                   .andReturn(new Object());
        replayAll();
        KalahaGame kalahaGame = gameService.playGame(1, 5);
        verifyAll();
        assertNotNull(kalahaGame);
        assertEquals(Integer.valueOf(1), kalahaGame.getGameId());
        assertEquals(IN_PROGRESS, kalahaGame.getGameStatus());
        assertEquals(PLAYER_TWO, kalahaGame.getPlayersTurn());
        assertEquals(Integer.valueOf(4), kalahaGame.getKalahaPits().get(6).getStones());
    }

    @Test(expectedExceptions = KalahaGameException.class)
    public void playGame_Test_gameStatusNotInProgress() {
        expect(mockKalahaRepository.findById(anyInt()))
                                   .andReturn(Optional.ofNullable(getGameEntityTestData_gameStatusNotInProgress()));
        expect(mockKalahaRepository.save(anyObject()))
                                   .andReturn(new Object());
        replayAll();
        KalahaGame kalahaGame = gameService.playGame(1, 5);
        verifyAll();
        assertNull(kalahaGame);
    }

    private KalahaGameEntity getGameEntityTestData() {

        return KalahaGameEntity.builder()
                               .gameId(1)
                               .gameStatus(IN_PROGRESS)
                               .playersTurn(PLAYER_ONE)
                               .kalahaPits(addStonesTestData())
                               .build();
    }

    private KalahaGameEntity getGameEntityTestData_gameStatusNotInProgress() {

        return KalahaGameEntity.builder()
                               .gameId(1)
                               .gameStatus("Player 2 wins")
                               .playersTurn(PLAYER_ONE)
                               .kalahaPits(addStonesTestData())
                               .build();

    }

    private List<KalahaPitEntity> addStonesTestData() {
        return IntStream.rangeClosed(1, TOTAL_PITS)
                        .mapToObj(i -> new KalahaPitEntity(i, (i == 7 || i == 14) ? 3 : 7))
                        .collect(Collectors.toList());
    }
}
