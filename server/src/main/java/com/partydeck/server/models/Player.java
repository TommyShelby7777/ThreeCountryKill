package com.partydeck.server.models;

import com.partydeck.server.models.CardAreas.Equitment;
import com.partydeck.server.models.CardAreas.Judge;
import com.partydeck.server.models.WarLords.whiteWarLord;
import com.partydeck.server.models.events.PlayerEventListener;
import com.partydeck.server.models.CardsLibrary.Card;
import com.partydeck.server.models.helpers.Identifiable;
import com.partydeck.server.models.skills.baseSkill;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * A class representing a player object
 * @author Itay Schechner
 * @version 1.0
 */
public abstract class Player implements Identifiable<String> {

    //玩家属性
    // attributes
    protected String id;

    // if an id changes, keep a reference to the old one as well.
    protected String oldId;

    protected String nickname;

    protected boolean admin;

    //通用属性，即白板玩家具有的属性
    protected int maximum_Hand_cards;

    protected int attack_distance;

    protected int distance;

    protected Card[] hand_Cards;

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
