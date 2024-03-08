package com.partydeck.server.models;

import com.partydeck.server.models.CardsLibrary.CardsLibrary;
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
public class Game implements PlayerEventListener, RoundEventListener, Identifiable<String>, Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);


    private String id;
    private int game_players_num;

    private Circle<Player> players;
    private String message;

    private Round currentRound;

    private boolean started;
    private boolean resumed;

    private CardsLibrary CardsHeap;

    private GameEventListener eventListener;

    public Game(){
        this.id = "";
        this.players = new Circle<>();
        this.game_players_num = -1;
        this.message = null;
        this.started = false;
        this.resumed = false;
        this.eventListener = null;
    }


    @Override
    public void run(){

    }


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

    }

    /**
     * Adds a player to the game
     * @param player the player to add
     * @return true if player is successfully added
     */
    public boolean onConnectionCreate(Player player) {
        player.setEventListener(this);

        if(player.isConnected()){
            players.addEntry(player);

            player.broadcast(BroadcastContext.INIT, "id", player.getId(), "isAdmin", player.isAdmin(), "game", id, "players", players.asList(Player::getNickname));
            broadcastAll(BroadcastContext.PLAYER_JOINED, "count", players.count(Player::isConnected), "joined", player.getNickname(), "joinedId", player.getId());

            if(resumed)
                player.broadcast(BroadcastContext.JOINED_MID_GAME);
            if (started && !resumed)
                player.broadcast(BroadcastContext.GAME_PAUSED, new HashMap<>());
            return true;
        }

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

    }

    /**
     * Fires every time a new round is created
     */
    @Override
    public void onRoundStart() {

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

    }

    /**
     * Fires when a player asks to skip
     *
     * @param player the player who asked to skip
     */
    @Override
    public void onSkipRequest(Player player) {

    }

    /**
     * Fires when all of the options are ready
     *
     * @param options the options picked by the players
     * @param judge   the current judge
     */
    @Override
    public void onOptionsReady(Iterable<Card> options, Player judge) {

    }

    /**
     * Fires every time a judge picks a card
     *
     * @param cardId  the picked card id
     * @param judge the current judge
     */
    @Override
    public void onJudgePick(String cardId, Player judge) {

    }

    /**
     * Fires when a round is unexpectedly ended
     *
     * @param judge the judge of the round
     */
    @Override
    public void onUnexpectedRoundEnd(Player judge) {

    }

    /**
     * Fires when the game admin presses 'next'.
     *
     * @param player the player who pressed next
     */
    @Override
    public void onNextRoundRequest(Player player) {

    }

    /**
     * Fires when the admin requests to stop the game
     * @param player the player wo asked to stop
     */
    @Override
    public void onStopRequest(Player player) {

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

    }

    /**
     * Fires when the game has started and there are less than 3 players in the game
     */
    private void onPause() {

    }

    private void onStop() {

    }

    /**
     * Fires when a player was unexpectedly disconnected for too long
     *
     * @param player the player who disconnected
     */
    @Override
    public void onConnectionDestroy(Player player) {

    }

    public void onDestroy() {

    }
}
