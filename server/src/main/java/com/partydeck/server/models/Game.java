package com.partydeck.server.models;

import com.partydeck.server.models.iterable.Circle;
import com.partydeck.server.models.iterable.Deck;
import com.partydeck.server.models.events.PlayerEventListener;
import com.partydeck.server.models.events.RoundEventListener;
import com.partydeck.server.models.helpers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * An object representing the game
 * @author Itay Schechner
 * @version 1.0
 */
public class Game implements PlayerEventListener, RoundEventListener, Identifiable<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);


    private String id;

    private Circle<Player> players;

    private Round currentRound;

    private boolean started;
    private boolean resumed;

    private GameEventListener eventListener;



    /**
     * Checks if a given id is identical to the identifiable object id
     *
     * @param id the id to compare to
     * @return true if the two values match
     */
    @Override
    public boolean is(String id) {
        return this.id.equals(id);
    }

    /**
     * Returns the id of the object
     *
     * @return the object id
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Returns the number of players in the game
     * @return the size of the players cache
     */
    public int getPlayerCount() {
        return this.players.size();
    }

    /**
     * Set the game event listener
     * @param eventListener the event listener to set
     */
    public void setGameEventListener(GameEventListener eventListener) {
        this.eventListener = eventListener;
    }

    /*
    * Connection-related methods:
    *   Broadcasting
    *   Connection create
    *   Connection resume
     */

    private void broadcastAll(BroadcastContext context, Object... args) {
        Map<String, Object> argsMap = new HashMap<>();
        for (int i = 0; i < args.length - 1; i+=2) {
            if (args[i] instanceof String)
                argsMap.put((String) args[i], args[i + 1]);
        }
        for (Player player: players) {
            player.broadcast(context, argsMap);
        }
    }

    /**
     * Adds a player to the game
     * @param player the player to add
     * @return true if player is successfully added
     */
    public boolean onConnectionCreate(Player player) {


        return false;
    }

    /*
     * The actual game methods:
     *  game start, resume
     *  round start, end, skip, next
     *  player events
     */


    /**
     * Fires when a connection is renewed
     * @param player the player that has renewed connection
     * @return true if player is returned to the game successfully
     */
    public boolean onConnectionResume(Player player) {


        return true;
    }

    private void onResume() {
        if (eventListener != null)
            eventListener.onGameResume(id);

        broadcastAll(BroadcastContext.GAME_RESUMED);

        // in order for a game to be resumed, it has to be started, and therefore the current round != null.
        resumed = true;
        currentRound.clear();
        currentRound.start();

    }

    /**
     * Fires when the admin requests to start the game
     * @param player the player who asked to start
     */
    @Override
    public void onStartRequest(Player player) {
        int playerCount = players.count(Player::isConnected);
        if (player.isAdminOf(this) && playerCount >= MIN_NUMBER_OF_PLAYERS) { // if the start request was valid

            if (eventListener != null)
                eventListener.onGameStart(id);

            broadcastAll(BroadcastContext.GAME_STARTED, "dispatched", "start");

            started = true;
            resumed = true;
            currentRound = new Round();
            currentRound.setRoundEventListener(this);
            currentRound.setNumberOfParticipants(playerCount);
            currentRound.start();

        }
    }

    /**
     * Fires every time a new round is created
     */
    @Override
    public void onRoundStart() {
        Optional<Player> judgeOpt = players.circleAndFind(Player::isConnected);
        if (!started || !resumed || judgeOpt.isEmpty())
            return;

        Optional<Card> questionOpt = questionDeck.pickTopCard();

        if (questionOpt.isEmpty()) {
            onStop();
            return;
        }

        Player judge = judgeOpt.get();
        Card question = questionOpt.get();

        judge.setJudge(true);
        currentRound.setJudge(judge);
        currentRound.setNumberOfParticipants(players.count(Player::isConnected)); // update this in case a player joined mid-game
        broadcastAll(BroadcastContext.ROUND_STARTED, "j", judge.getNickname(), "q", question.getContent());
    }

    /**
     * Fires every time a player uses a card
     *
     * @param card   the card used
     * @param player the player that used the card
     * @return the new card to be added
     */
    @Override
    public Card onCardUse(Card card, Player player) {
        answerDeck.insertCardInBottom(card);
        currentRound.recordUse(card, player);
        broadcastAll(BroadcastContext.PLAYER_USAGE, "playerId", player.getId(), "playerName", player.getNickname());
        return answerDeck.pickTopCard().orElseThrow(); // there must be a card here since it was recently inserted
    }

    /**
     * Fires when a player asks to skip
     *
     * @param player the player who asked to skip
     */
    @Override
    public void onSkipRequest(Player player) {
        if (player.isAdminOf(this)) {
            currentRound.emitSkip();
        }
    }

    /**
     * Fires when all of the options are ready
     *
     * @param options the options picked by the players
     * @param judge   the current judge
     */
    @Override
    public void onOptionsReady(Iterable<Card> options, Player judge) {
        broadcastAll(BroadcastContext.PICK, "pick", options);
    }

    /**
     * Fires every time a judge picks a card
     *
     * @param cardId  the picked card id
     * @param judge the current judge
     */
    @Override
    public void onJudgePick(String cardId, Player judge) {
        if (judge.isJudgeOf(this)) {
            try {
                Player winner = currentRound.getWinner(cardId).orElseThrow();
                winner.incrementRoundsWon();
                broadcastAll(BroadcastContext.ROUND_ENDED, "winningCard", cardId, "playerWon", winner.getNickname());
            } catch (Exception e) {
                broadcastAll(BroadcastContext.ROUND_ENDED_404, "playerWon", "nobody");
            } finally {
                judge.setJudge(false);
            }
        }
    }

    /**
     * Fires when a round is unexpectedly ended
     *
     * @param judge the judge of the round
     */
    @Override
    public void onUnexpectedRoundEnd(Player judge) {
        broadcastAll(BroadcastContext.ROUND_ENDED_404, "playerWon", "nobody");
        judge.setJudge(false);
    }

    /**
     * Fires when the game admin presses 'next'.
     *
     * @param player the player who pressed next
     */
    @Override
    public void onNextRoundRequest(Player player) {
        LOGGER.info("Requesting for next round" + resumed + started + questionDeck.hasNext());
        if (player.isAdminOf(this)) {
            currentRound.clear();
            if (questionDeck.hasNext() && started)
                currentRound.start();
            else
                onStop();
        }
    }

    /**
     * Fires when the admin requests to stop the game
     * @param player the player wo asked to stop
     */
    @Override
    public void onStopRequest(Player player) {
        if (player.isAdminOf(this)) {
            onStop();
        }
    }

    /*
    * Game lifecycle end:
    *   Connection Pause, Destroy
    *   Game Pause, Stop, Destroy
     */

    /**
     * Fires when the player has disconnected
     *
     * @param player the player who disconnected
     */
    @Override
    public void onConnectionPause(Player player) {

        int newPlayerCount = players.count(Player::isConnected);

        if (currentRound != null)
            currentRound.setNumberOfParticipants(newPlayerCount);

        if (player.isAdminOf(this)) {
            player.demoteToPlayer();
            players.find(Player::isConnected).ifPresent(Player::makeAdmin);
        }

        broadcastAll(BroadcastContext.CONNECTION_PAUSE, "count", newPlayerCount);

        // handle onPause
        if (started && newPlayerCount < 3) // game should pause
            onPause();
    }

    /**
     * Fires when the game has started and there are less than 3 players in the game
     */
    private void onPause() {
        this.resumed = false;
        broadcastAll(BroadcastContext.GAME_PAUSED);

        if (eventListener != null)
            eventListener.onGamePause(id);

        if (currentRound != null) // emit full skip, wait for admin to press "next" or "end game"
            currentRound.emitFullSkip();

    }

    private void onStop() {
        this.started = false;
        this.resumed = false;
        List<ScoreboardRow> scores = new ArrayList<>();
        for (Player player: players)
            scores.add(new ScoreboardRow(player));
        broadcastAll(BroadcastContext.GAME_ENDED, "scores", scores.stream().sorted().collect(Collectors.toList()));
        onDestroy();
    }

    /**
     * Fires when a player was unexpectedly disconnected for too long
     *
     * @param player the player who disconnected
     */
    @Override
    public void onConnectionDestroy(Player player) {
        players.removeEntry(player);

        broadcastAll(BroadcastContext.PLAYER_LEFT, "left", player.getNickname(), "leftId", player.getId());

        // return player cards
        Arrays.stream(player.currentCards).forEach(answerDeck::insertCardInBottom);
        player.currentCards = new Card[] {};

        if ((started && players.size() < 3) || players.size() == 0) {
            broadcastAll(BroadcastContext.GAME_INTERRUPTED);
            onDestroy();
        }

    }

    public void onDestroy() {
        for (Player player: players)
            player.destroyConnection();
        if (eventListener != null)
            eventListener.onGameDestroy(id);
    }
}
