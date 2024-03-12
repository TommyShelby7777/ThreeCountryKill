package com.partydeck.server.models;

import com.partydeck.server.models.CardAreas.Equitment;
import com.partydeck.server.models.CardAreas.Judge;
import com.partydeck.server.models.Enums.BroadcastContext;
import com.partydeck.server.models.Enums.IdentityType;
import com.partydeck.server.models.WarLords.whiteWarLord;
import com.partydeck.server.models.events.PlayerEventListener;
import com.partydeck.server.models.CardsLibrary.Card;
import com.partydeck.server.models.helpers.Identifiable;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A class representing a player object
 * @author Itay Schechner
 * @version 1.0
 */
@Data
public abstract class Player implements Identifiable<String> {

    //玩家属性
    // attributes
    protected String id;

    // if an id changes, keep a reference to the old one as well.
    protected String oldId;

    protected String nickname;

    protected boolean admin;

    //通用属性，即白板玩家具有的属性
    protected IdentityType identity;

    protected int maximum_Hand_cards;

    protected int attack_distance;

    protected int distance;

    protected ArrayList<Card> hand_Cards;

    protected Equitment equitment;

    //判定区
    protected Judge judge;



    //选用的武将
    protected whiteWarLord warLord;

    protected PlayerEventListener eventListener;

    public Player(){
        this.id = "";
        this.oldId = "";
        this.nickname = "Anonymous";
        this.admin = false;
        this.eventListener = null;
        this.maximum_Hand_cards = -1;
        this.attack_distance = -1;
        this.distance = -1;
        this.hand_Cards = new ArrayList<>();
        this.equitment = new Equitment();
        this.judge = new Judge();
        this.warLord = null;
        this.identity = IdentityType.ANYMOUS;
    }

    public Player(String id,String nickname){
        this();
        this.id = id;
        this.nickname = nickname;
    }

    public void makeAdmin(){
        this.admin = true;
    }


    public abstract boolean isConnected();

    protected void handleGameStartRequest(){
        this.eventListener.onGameStartRequest();
    }

    protected void handleMessage(BroadcastContext context,Map<String,Object> data) throws UnsupportedOperationException{
        switch(context){
            case GAME_START:
                handleGameStartRequest();
                break;
        }
    }

    public abstract void broadcast(Map<String,Object> args);

    public void broadcast(BroadcastContext context, Map<String,Object> args){
        args.put("context",context.toString());
        switch(context){
            case PLAYER_JOINED:
            case PLAYER_LEFT:
            case CONNECTION_PAUSE:
            case CONNECTION_RESUME:
                args.put("isAdmin",isAdmin());
                break;
        }
        broadcast(args);
    }

    public void broadcast(BroadcastContext context, Object... args) {
        Map<String, Object> argsMap = new HashMap<>();
        for (int i = 0; i < args.length - 1; i+=2) {
            if (args[i] instanceof String)
                argsMap.put((String) args[i], args[i + 1]);
        }
        broadcast(context, argsMap);
    }
    /**
     * A String representation of the player
     * @return the string holding the player values.
     */
    @Override
    public String toString() {
        return "Player{" +
                '}';
    }
}
