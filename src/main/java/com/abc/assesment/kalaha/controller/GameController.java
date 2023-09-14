package com.abc.assesment.kalaha.controller;


import com.abc.assesment.kalaha.exception.KalahaGameException;
import com.abc.assesment.kalaha.model.KalahaPlayGameRequest;
import com.abc.assesment.kalaha.model.KalahaGame;
import com.abc.assesment.kalaha.service.GameService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

import static com.abc.assesment.kalaha.exception.KalahaGameExceptionCodes.INVALID_PITID_SELECTION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
/**
 * @author Karthiga
 * Controller for Kalaha game controller
 *
 * */
@Slf4j
@RestController
@RequestMapping(value = "/kalahaGame", produces = APPLICATION_JSON_VALUE)
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Creates a new game
     *
     * @return KalahaGame New game with default stones added
     */
    @PostMapping
    public ResponseEntity<KalahaGame> createNewGame() {
        KalahaGame kahalaGame = gameService.createNewGame();
        log.info("Game Created");
        return ResponseEntity.status(HttpStatus.CREATED).body(kahalaGame);
    }

    /**
     * Get the game details
     *
     * @param gameId
     * @return Kalaha game with details
     */
    @GetMapping(value = "/{id}")
    public ResponseEntity<KalahaGame> getGame(@Parameter(required = true)
                                              @PathVariable(value = "id") Integer gameId) {
        log.info("Get game service called");
        return ResponseEntity.status(HttpStatus.OK).body(gameService.getGameInfo(gameId));
    }

    /**
     * Plays the game
     *
     * @param playGameRequest gameId and selected pit Id
     * @return Kalaha game after making moves with next player and game status info
     */
    @PostMapping(value = "/playGame")
    public ResponseEntity<KalahaGame> playGame(@RequestBody(required = true)
                                               @Valid KalahaPlayGameRequest playGameRequest) {
        if (isInvalidPitId(playGameRequest.getSelectedPitId())) {
            throw new KalahaGameException(INVALID_PITID_SELECTION,
                                          "Invalid pit index. Pit id or index should be between 1-6 or 8-13");
        }
        log.info("Play game service called");
        KalahaGame kalahaGame = gameService.playGame(playGameRequest.getGameId(), playGameRequest.getSelectedPitId());
        return ResponseEntity.status(HttpStatus.OK).body(kalahaGame);
    }
    private boolean isInvalidPitId(int selectedPitId) {
        return (selectedPitId < 1 ||  selectedPitId == 7  || selectedPitId > 13);
    }

}