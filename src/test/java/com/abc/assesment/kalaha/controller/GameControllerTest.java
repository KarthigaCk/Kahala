package com.abc.assesment.kalaha.controller;

import com.abc.assesment.kalaha.exception.KalahaGameException;
import com.abc.assesment.kalaha.model.KalahaGame;
import com.abc.assesment.kalaha.model.KalahaPit;
import com.abc.assesment.kalaha.model.KalahaPlayGameRequest;
import com.abc.assesment.kalaha.persistance.entity.KalahaGameEntity;
import com.abc.assesment.kalaha.persistance.entity.KalahaPitEntity;
import com.abc.assesment.kalaha.service.GameService;
import com.abc.assesment.kalaha.service.GameServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.abc.assesment.kalaha.constants.GameConstants.DEFAULT_STONES;
import static com.abc.assesment.kalaha.constants.GameConstants.IN_PROGRESS;
import static com.abc.assesment.kalaha.constants.GameConstants.TOTAL_PITS;
import static com.abc.assesment.kalaha.model.Player.PLAYER_ONE;
import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replayAll;
import static org.powermock.api.easymock.PowerMock.resetAll;
import static org.powermock.api.easymock.PowerMock.verifyAll;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * Test cases for {@link GameController}
 */
public class GameControllerTest {
    private GameController gameController;
    private GameService mockGameService;
    private KalahaPlayGameRequest kalahaPlayGameRequest;

    @BeforeMethod
    public void setup() {
        mockGameService = createMock(GameService.class);
        gameController = new GameController(mockGameService);
    }

    @AfterMethod
    public void clear() {
        resetAll();
    }

    @Test
    public void createNewGame_test() {

        expect(mockGameService.createNewGame())
                .andReturn(getKalahaGameTestData());
        replayAll();
        ResponseEntity<KalahaGame> kalahaGameResponseEntity = gameController.createNewGame();
        verifyAll();
        assertNotNull(kalahaGameResponseEntity);
        assertEquals(HttpStatus.CREATED, kalahaGameResponseEntity.getStatusCode());
        assertEquals(Integer.valueOf(1), kalahaGameResponseEntity.getBody().getGameId());
        assertEquals(IN_PROGRESS, kalahaGameResponseEntity.getBody().getGameStatus());
        assertEquals(PLAYER_ONE, kalahaGameResponseEntity.getBody().getPlayersTurn());
        assertEquals(Integer.valueOf(7), kalahaGameResponseEntity.getBody().getKalahaPits().get(1).getStones());
    }

    @Test
    public void getGame_test() {

        expect(mockGameService.getGameInfo(anyInt()))
                .andReturn(getKalahaGameTestData());
        replayAll();
        ResponseEntity<KalahaGame> kalahaGameResponseEntity = gameController.getGame(1);
        verifyAll();
        assertNotNull(kalahaGameResponseEntity);
        assertEquals(HttpStatus.OK, kalahaGameResponseEntity.getStatusCode());
        assertEquals(Integer.valueOf(1), kalahaGameResponseEntity.getBody().getGameId());
        assertEquals(IN_PROGRESS, kalahaGameResponseEntity.getBody().getGameStatus());
        assertEquals(PLAYER_ONE, kalahaGameResponseEntity.getBody().getPlayersTurn());
        assertEquals(Integer.valueOf(7), kalahaGameResponseEntity.getBody().getKalahaPits().get(1).getStones());
    }

    @Test
    public void playGame_test() {

        expect(mockGameService.playGame(anyInt(), anyInt()))
                .andReturn(getKalahaGameTestData());
        replayAll();
        ResponseEntity<KalahaGame> kalahaGameResponseEntity = gameController.playGame(getPlayGameRequest());
        verifyAll();
        assertNotNull(kalahaGameResponseEntity);
        assertEquals(HttpStatus.OK, kalahaGameResponseEntity.getStatusCode());
        assertEquals(Integer.valueOf(1), kalahaGameResponseEntity.getBody().getGameId());
        assertEquals(IN_PROGRESS, kalahaGameResponseEntity.getBody().getGameStatus());
        assertEquals(PLAYER_ONE, kalahaGameResponseEntity.getBody().getPlayersTurn());
        assertEquals(Integer.valueOf(7), kalahaGameResponseEntity.getBody().getKalahaPits().get(1).getStones());
    }

    private KalahaPlayGameRequest getPlayGameRequest() {

        return KalahaPlayGameRequest.builder().gameId(1).selectedPitId(6).build();
    }

    private KalahaPlayGameRequest getPlayGameRequestHousePitSelected() {

        return KalahaPlayGameRequest.builder().gameId(1).selectedPitId(6).build();
    }

    private KalahaGame getKalahaGameTestData() {

        return KalahaGame.builder()
                .gameId(1)
                .gameStatus(IN_PROGRESS)
                .playersTurn(PLAYER_ONE)
                .kalahaPits(addStonesTestData())
                .build();

    }

    private List<KalahaPit> addStonesTestData() {
        return IntStream.rangeClosed(1, TOTAL_PITS)
                .mapToObj(i -> new KalahaPit(i, (i == 7 || i == 14) ? 3 : 7))
                .collect(Collectors.toList());
    }
}
