package com.partydeck.server.models;

import com.partydeck.server.models.CardsLibrary.CardsLibrary;
import com.partydeck.server.models.Enums.BroadcastContext;
import com.partydeck.server.models.Enums.IdentityType;
import com.partydeck.server.models.WarLords.WarLordsLibrary;
import com.partydeck.server.models.helpers.Identifiable;
import com.partydeck.server.models.iterable.Circle;
import com.partydeck.server.models.events.PlayerEventListener;
import com.partydeck.server.models.CardsLibrary.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.util.*;

/**
 * An object representing the game
 * @author Itay Schechner
 * @version 1.0
 */
public class Game implements PlayerEventListener, Identifiable<String>, Runnable{

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);


    private String id;
    private int game_players_num;

    private Circle<Player> players;
    private String message;

    private boolean started;
    private boolean resumed;

    private CardsLibrary CardsHeap;

    private WarLordsLibrary WarLordsHeap;

    private GameEventListener eventListener;

    private String Lord_id;

    public Game(){
        this.id = "";
        this.players = new Circle<>();
        this.game_players_num = -1;
        this.message = "";
        this.started = false;
        this.resumed = false;
        this.eventListener = null;
        this.CardsHeap = new CardsLibrary();
    }

    public Game(String id,int game_players_num){
        this();
        this.id = id;
        this.game_players_num = game_players_num;
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
            if(players.count(Player::isAdmin)==0){
                player.makeAdmin();
            }
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


    }

    @Override
    public void onGameStartRequest(){
        //分配身份
        IdentityType[] Identity = IdentityType.values();
        Collections.shuffle(Arrays.asList(Identity));
        for(int i=0;i<Identity.length;i++){
            players.getByindex(i).setIdentity(Identity[i]);
        }

        //创建牌堆 洗牌
        CardsHeap = new CardsLibrary();
        CardsHeap.shuffle();
        WarLordsHeap = new WarLordsLibrary();
        WarLordsHeap.shuffle();
    }
    /**
     * Fires when the admin requests to start the game
     * @param player the player who asked to start
     */
    @Override
    public void onStartRequest(Player player) {

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
     * Fires every time a judge picks a card
     *
     * @param cardId  the picked card id
     * @param judge the current judge
     */
    @Override
    public void onJudgePick(String cardId, Player judge) {

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

    public boolean isMessageNull(){
        return message.isEmpty();
    }
}
