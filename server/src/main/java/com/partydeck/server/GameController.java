package com.partydeck.server;

import com.partydeck.server.services.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * The main server controller
 * @author Itay Schechner
 * @version 1.0
 */
@RestController
public class GameController {

    @Autowired
    private GameService gameService;

    /**
     * Creates a new game instance
     * @return an object with the created game code.
     */
    @PostMapping("/create")
    public CreateGameResponse createGame(@RequestBody CreateGameRequest request) {
        try {
            String code = gameService.createGame(request.getGame_players_num());
            return new CreateGameResponse(code, 201);
        } catch (StackOverflowError e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not generate game id");
        }
    }

    /**
     * Check if a game with a certain id exists
     * @param code the code to check
     * @param response the response object
     * @return an object with the check result
     */
    @GetMapping("/check")
    public CheckGameResponse checkGame(@RequestParam String code, HttpServletResponse response) {
        boolean exists = gameService.checkGame(code);
        if (!exists)
            response.setStatus(404);
        return new CheckGameResponse(exists ? 200 : 404, exists);
    }

    public static class CreateGameRequest {

        private int game_players_num;

        public CreateGameRequest(int game_players_num) {
            this.game_players_num = game_players_num;
        }

        public int getGame_players_num() {
            return game_players_num;
        }
        public int setGame_players_num(int game_players_num){
            this.game_players_num = game_players_num;
            return this.game_players_num;
        }
    }
    public static class CreateGameResponse {

        private String code;
        private int status;

        public CreateGameResponse(String code, int status) {
            this.code = code;
            this.status = status;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
    public static class CheckGameResponse {

        private int code;
        private boolean exists;

        public CheckGameResponse(int code, boolean exists) {
            this.code = code;
            this.exists = exists;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public boolean isExists() {
            return exists;
        }

        public void setExists(boolean exists) {
            this.exists = exists;
        }
    }
}
