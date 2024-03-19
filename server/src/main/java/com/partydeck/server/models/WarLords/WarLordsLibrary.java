package com.partydeck.server.models.WarLords;

import java.util.ArrayList;
import java.util.Collections;

public class WarLordsLibrary {
    protected ArrayList<whiteWarLord> warLords;
    public WarLordsLibrary(){
        warLords = new ArrayList<>();
        initWarLordsHeap();
    }

    public void shuffle(){
        Collections.shuffle(warLords);
    }

    private void initWarLordsHeap(){

    }
}
