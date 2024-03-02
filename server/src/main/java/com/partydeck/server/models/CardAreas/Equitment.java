package com.partydeck.server.models.CardAreas;

public class Equitment {
    public Weapon weapon;
    public Armor armor;
    public Mount mount;
    Equitment(){
        this.weapon = new Weapon();
        this.armor = new Armor();
        this.mount = new Mount();
    }
}
