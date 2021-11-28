package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class SpellContext {

    private boolean isCanceled;

    private Spell spell;

    public @Nullable LivingEntity caster;

    private int currentIndex;

    public @Nullable TileEntity castingTile;

    public ParticleColor.IntWrapper colors;

    private CasterType type;


    @Deprecated
    public SpellContext(List<AbstractSpellPart> spell, @Nullable LivingEntity caster){
        this(new Spell(spell), caster);
    }

    public SpellContext(@Nonnull Spell spell, @Nullable LivingEntity caster){
        this.spell = spell;
        this.caster = caster;
        this.isCanceled = false;
        this.currentIndex = 0;
        this.colors = ParticleUtil.defaultParticleColorWrapper();
    }

    // TODO: Rename to nextPart
    public @Nullable AbstractSpellPart nextSpell(){
        this.currentIndex++;
        AbstractSpellPart part = null;
        try {
            part = getSpell().recipe.get(currentIndex - 1);
        }catch (Throwable e){ // This can happen if a new spell context is created but does not reset the bounds.
            System.out.println("=======");
            System.out.println("Invalid spell cast found! This is a bug and should be reported!");
            System.out.println(spell.getDisplayString());
            System.out.println("Casting player: ");
            System.out.println(caster);
            System.out.println("Casting tile:");
            System.out.println(castingTile);
            System.out.println("=======");
            e.printStackTrace();
        }
        return part;
    }

    public SpellContext resetCastCounter(){
        this.currentIndex = 0;
        this.isCanceled = false;
        return this;
    }

    public SpellContext withCastingTile(TileEntity tile){
        this.castingTile = tile;
        return this;
    }

    public SpellContext withSpellResetCounter(Spell spell){
        this.spell = spell;
        resetCastCounter();
        return this;
    }

    public SpellContext withCaster(@Nullable LivingEntity caster){
        this.caster = caster;
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

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public @Nonnull Spell getSpell() {
        return spell == null ? Spell.EMPTY : spell;
    }

    @Nullable
    public LivingEntity getCaster() {
        return caster;
    }

    /**
     * The type of caster that created the spell. Used for removing or changing behaviors of effects that would otherwise cause problems, like GUI opening effects.
     */
    public static class CasterType{
        public static final CasterType RUNE = new CasterType("rune");
        public static final CasterType TURRET = new CasterType("turret");
        public static final CasterType ENTITY = new CasterType("entity");
        public static final CasterType OTHER = new CasterType("other");
        public String id;
        public CasterType(String id){
            this.id = id;
        }
    }

}
