package com.partydeck.server.models.CardsLibrary;

import com.partydeck.server.models.Enums.CardsName;
import com.partydeck.server.models.Enums.CardsType;
import com.partydeck.server.models.Enums.Suit;

public class Card {
    protected Suit suit;
    protected int rank;
    protected CardsType cardType;
    protected CardsName cardName;

    public Card(){
        suit = Suit.HEARTS;
        rank = 1;
        cardType = CardsType.BASIC_CARDS;
        cardName = CardsName.KILL;
    }
}
