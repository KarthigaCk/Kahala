package com.abc.assesment.kalaha.controller;

import com.abc.assesment.kalaha.model.KalahaGame;
import com.abc.assesment.kalaha.service.GameService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/kahalaGame")
public class GameController {

    @Autowired
    GameService gameService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<KalahaGame> createNewGame() {
        KalahaGame kahalaGame = gameService.createNewGame();
        log.info("Game Created");
        return ResponseEntity.ok(kahalaGame);
    }

}
