package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpellContext implements Cloneable{

    private boolean isCanceled;

    private Spell spell;

    public @Nullable LivingEntity caster;

    private int currentIndex;

    public @Nullable BlockEntity castingTile;

    public ParticleColor.IntWrapper colors;

    private CasterType type;

    public CompoundTag tag = new CompoundTag();

    public SpellContext(@Nonnull Spell spell, @Nullable LivingEntity caster){
        this.spell = spell;
        this.caster = caster;
        this.isCanceled = false;
        this.currentIndex = 0;
        this.colors = ParticleUtil.defaultParticleColorWrapper();
    }

    public SpellContext(ISpellCaster caster, @Nullable LivingEntity casterEntity){
        this(caster.getSpell(), casterEntity);
        withColors(caster.getColor());

    }

    public @Nullable AbstractSpellPart nextPart(){
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

    public boolean hasNextPart(){
        return spell.isValid() && !isCanceled() && currentIndex < spell.recipe.size();
    }

    public SpellContext resetCastCounter(){
        this.currentIndex = 0;
        this.isCanceled = false;
        return this;
    }

    public SpellContext withCastingTile(BlockEntity tile){
        this.castingTile = tile;
        return this;
    }

    public SpellContext withSpellResetCounter(Spell spell){
        this.spell = spell;
        return resetCastCounter();
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

    public SpellContext withSpell(Spell spell){
        this.spell = spell;
        return this;
    }

    public CasterType getType(){
        return this.type == null ? CasterType.OTHER : type;
    }

    public int getCurrentIndex(){return currentIndex;}

    /**
     * @param newIndex set the current Index to param, careful with its usage as it might go out of bounds
     */
    public void setCurrentIndex(int newIndex){this.currentIndex = newIndex;}

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

    @Override
    public SpellContext clone() {
        try {
            SpellContext clone = (SpellContext) super.clone();
            clone.spell = this.spell.clone();
            clone.colors = this.colors.clone();
            clone.tag = this.tag.copy();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
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
