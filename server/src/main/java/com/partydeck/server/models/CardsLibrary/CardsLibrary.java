package com.partydeck.server.models.CardsLibrary;

import com.partydeck.server.models.Enums.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CardsLibrary {
    protected ArrayList<Card> unUsedCards;
    protected ArrayList<Card> usedCards;

    public CardsLibrary(){
        unUsedCards = new ArrayList<>();
        usedCards = new ArrayList<>();
        initCardsHeap();
    }

    public void shuffle(){
        Collections.shuffle(unUsedCards);
    }

    private void initCardsHeap(){
        //点数和花色
        initBasicCards();
        initEquitmentCards();
        initTipsBagCards();
    }

    private void initBasicCards(){
        //普通杀
        Map<Suit,Map<Integer,Integer>> sha = new HashMap<>();

        Map<Integer,Integer> sha_hearts_rank = new HashMap<>();
        sha_hearts_rank.put(1,1);sha_hearts_rank.put(2,1);sha_hearts_rank.put(10,1);sha_hearts_rank.put(11,1);sha_hearts_rank.put(12,1);
        sha.put(Suit.HEARTS,sha_hearts_rank);

        Map<Integer,Integer> sha_diamonds_rank = new HashMap<>();
        sha_diamonds_rank.put(1,1);sha_diamonds_rank.put(2,1);sha_diamonds_rank.put(10,1);sha_diamonds_rank.put(11,1);sha_diamonds_rank.put(12,1);
        sha.put(Suit.DIAMONDS,sha_diamonds_rank);

        Map<Integer,Integer> sha_clubs_rank = new HashMap<>();
        sha_clubs_rank.put(1,1);sha_clubs_rank.put(2,1);sha_clubs_rank.put(10,1);sha_clubs_rank.put(11,1);sha_clubs_rank.put(12,1);
        sha.put(Suit.CLUBS,sha_clubs_rank);

        Map<Integer,Integer> sha_spades_rank = new HashMap<>();
        sha_spades_rank.put(1,1);sha_spades_rank.put(2,1);sha_spades_rank.put(10,1);sha_spades_rank.put(11,1);sha_spades_rank.put(12,1);
        sha.put(Suit.SPADES,sha_spades_rank);

        sha.forEach((key,value) -> {
            value.forEach((key2,value2)->{
                for(int i=0;i<value2;i++){
                    Card card = new CardSha(key,key2);
                    unUsedCards.add(card);
                }
            });
        });
        //属性杀
    }

    private void initEquitmentCards(){

    }

    private void initTipsBagCards(){

    }
}
