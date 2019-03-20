package com.hollingsworth.craftedmagic.spell.method;

public class ModifierProjectile extends MethodType {


    @Override
    public int getManaCost() {
        return 0;
    }

    @Override
    public String getTag() {
        return "Projectile";
    }

    @Override
    public void onCast() {
        System.out.println("Summoning projectile");
    }
}
