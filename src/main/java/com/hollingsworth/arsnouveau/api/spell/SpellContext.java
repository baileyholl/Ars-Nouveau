package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nullable;
import java.util.List;

public class SpellContext {

    private int manaCost;

    private boolean isCanceled;

    private final Spell spell;

    public final @Nullable LivingEntity caster;

    private int currentIndex;

    public @Nullable TileEntity castingTile;

    public ParticleColor.IntWrapper colors;

    private CasterType type;


    @Deprecated
    public SpellContext(List<AbstractSpellPart> spell, @Nullable LivingEntity caster){
        this(new Spell(spell), caster);
    }

    public SpellContext(Spell spell, @Nullable LivingEntity caster){
        this.spell = spell;
        this.caster = caster;
        this.isCanceled = false;
        this.currentIndex = 0;
        this.colors = ParticleUtil.defaultParticleColorWrapper();
    }


    public AbstractSpellPart nextSpell(){
        this.currentIndex++;
        return getSpell().recipe.get(currentIndex - 1);
    }

    public void resetSpells(){
        this.currentIndex = 0;
    }

    public SpellContext withCastingTile(TileEntity tile){
        this.castingTile = tile;
        return this;
    }

    public SpellContext withColors(ParticleColor.IntWrapper colors){
        this.colors = colors;
        return this;
    }

    public SpellContext withType(CasterType type){
        this.type = type;
        return this;
    }

    public CasterType getType(){
        return this.type == null ? CasterType.OTHER : type;
    }

    public int getCurrentIndex(){return currentIndex;}

    public int getManaCost() {
        return manaCost;
    }

    public void setManaCost(int manaCost) {
        this.manaCost = manaCost;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public Spell getSpell() {
        return spell == null ? Spell.EMPTY : spell;
    }


    @Nullable
    public LivingEntity getCaster() {
        return caster;
    }

    public enum CasterType{
        ENTITY,
        RUNE,
        TURRET,
        OTHER
    }
}
