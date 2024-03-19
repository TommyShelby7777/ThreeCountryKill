package com.partydeck.server.models.CardsLibrary;

import com.partydeck.server.models.Enums.CardsName;
import com.partydeck.server.models.Enums.Suit;

import static com.partydeck.server.models.Enums.CardsName.KILL;

public class CardSha extends Card{
    public CardSha(){
        super();
    }

    public CardSha(Suit suit,int rank){
        super();
        this.suit = suit;
        this.rank = rank;
        this.cardName = KILL;
    }
    public CardSha(Suit suit, int rank, CardsName cardsName){
        super();
        this.suit = suit;
        this.rank = rank;
        this.cardName = cardsName;
    }
}
