package com.partydeck.server.models;

import com.partydeck.server.models.CardAreas.Equitment;
import com.partydeck.server.models.CardAreas.Judge;
import com.partydeck.server.models.Enums.IdentityType;
import com.partydeck.server.models.WarLords.whiteWarLord;
import com.partydeck.server.models.events.PlayerEventListener;
import com.partydeck.server.models.CardsLibrary.Card;
import com.partydeck.server.models.helpers.Identifiable;
import com.partydeck.server.models.skills.baseSkill;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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


    public abstract boolean isConnected();
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
