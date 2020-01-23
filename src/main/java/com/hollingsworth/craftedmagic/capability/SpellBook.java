package com.hollingsworth.craftedmagic.capability;

import java.util.ArrayList;

public class SpellBook {

    private int bookMode;
    private ArrayList recipes;

    public SpellBook(){

    }


    public int getBookMode() {
        return bookMode;
    }

    public void setBookMode(int bookMode) {
        this.bookMode = bookMode;
    }

    public ArrayList getRecipes() {
        return recipes;
    }

    public void setRecipes(ArrayList recipes) {
        this.recipes = recipes;
    }
}
