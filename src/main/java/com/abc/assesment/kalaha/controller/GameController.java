package com.abc.assesment.kalaha.controller;

import com.abc.assesment.kalaha.exception.KalahaGameException;
import com.abc.assesment.kalaha.model.KalahaGame;
import com.abc.assesment.kalaha.service.GameService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.abc.assesment.kalaha.exception.KalahaGameExceptionCodes.INVALID_SELECTION;

@Slf4j
@RestController
@RequestMapping("/kahalaGame")
public class GameController {

    @Autowired
    GameService gameService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<KalahaGame> createNewGame() {
        KalahaGame kahalaGame = gameService.createNewGame();
        log.info("Game Created");
        return ResponseEntity.status(HttpStatus.OK).body(kahalaGame);
    }

    @GetMapping(value = "/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<KalahaGame> getGame(@Parameter(required = true) @PathVariable(value = "id") Integer gameId) {
        log.info("Get game called");
        return ResponseEntity.status(HttpStatus.OK).body(gameService.getGame(gameId));
    }

    @PostMapping(value = "/{gameId}/pits/{pitId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<KalahaGame> playGame(@Parameter(required = true) @PathVariable(value = "gameId")Integer gameId,
                                               @Parameter(description = "Pit id should be between 1-6 or 8-13",required = true)
                                               @PathVariable(value = "pitId")Integer pitId)  {

        log.info("playing for gameId "+gameId +" and pit id "+pitId);
        if(pitId < 1 || pitId.equals(7) || pitId > 13){
            throw new KalahaGameException(INVALID_SELECTION,"Invalid pit index. Pit id or index should be between 1-6 or 8-13");
        }
        KalahaGame kalahaGame = gameService.playGame(gameId,pitId);

        return ResponseEntity.status(HttpStatus.OK).body(kalahaGame);
    }

}
