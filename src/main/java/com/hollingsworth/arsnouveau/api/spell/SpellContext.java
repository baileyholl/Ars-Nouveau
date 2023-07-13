package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.IWrappedCaster;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.LivingCaster;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class SpellContext implements Cloneable {

    private boolean isCanceled;

    private Spell spell;
    private ItemStack casterTool = ItemStack.EMPTY;
    @Nullable
    private LivingEntity caster;

    private int currentIndex;

    public @Nullable BlockEntity castingTile;

    private ParticleColor colors = ParticleColor.defaultParticleColor();

    private CasterType type;
    private Level level;

    public CompoundTag tag = new CompoundTag();
    private IWrappedCaster wrappedCaster;

    // TODO: Convert usages to use casterTool
    public SpellContext(Level level,@NotNull Spell spell, @Nullable LivingEntity caster, IWrappedCaster wrappedCaster) {
        this.level = level;
        this.spell = spell;
        this.caster = caster;
        this.colors = spell.color.clone();
        this.wrappedCaster = wrappedCaster;
    }

    public SpellContext(Level level,@NotNull Spell spell, @Nullable LivingEntity caster, IWrappedCaster wrappedCaster, ItemStack casterTool) {
        this(level, spell, caster, wrappedCaster);
        this.casterTool = casterTool.copy();
    }

    public static SpellContext fromEntity(@NotNull Spell spell, @NotNull LivingEntity caster, ItemStack castingTool){
        return new SpellContext(caster.level, spell, caster, LivingCaster.from(caster), castingTool);
    }

    public SpellContext withWrappedCaster(IWrappedCaster caster){
        this.wrappedCaster = caster;
        if(caster instanceof LivingCaster livingCaster){
            this.caster = livingCaster.livingEntity;
        }
        return this;
    }

    public @Nullable AbstractSpellPart nextPart() {
        this.currentIndex++;
        AbstractSpellPart part = null;
        try {
            part = getSpell().recipe.get(currentIndex - 1);
        } catch (Throwable e) { // This can happen if a new spell context is created but does not reset the bounds.
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

    public boolean hasNextPart() {
        return spell.isValid() && !isCanceled() && currentIndex < spell.recipe.size();
    }

    public SpellContext resetCastCounter() {
        this.currentIndex = 0;
        this.isCanceled = false;
        return this;
    }

    public SpellContext withColors(ParticleColor colors) {
        this.colors = colors;
        return this;
    }

    public SpellContext withSpell(Spell spell) {
        this.spell = spell;
        return this;
    }

    /**
     * Returns a non-null caster that belongs to this spell.
     * This should always default to a fake player if an actual entity did not cast the spell.
     * Generally tiles would set the caster in this context, but casters can be nullable.
     */
    public@NotNull LivingEntity getUnwrappedCaster() {
        LivingEntity shooter = this.caster;
        if (shooter == null && this.castingTile != null) {
            shooter = ANFakePlayer.getPlayer((ServerLevel) level);
            BlockPos pos = this.castingTile.getBlockPos();
            shooter.setPos(pos.getX(), pos.getY(), pos.getZ());
        }
        shooter = shooter == null ? ANFakePlayer.getPlayer((ServerLevel) level) : shooter;
        return shooter;
    }

    public @NotNull IWrappedCaster getCaster(){
        return wrappedCaster;
    }
    
    public ItemStack getCasterTool(){ return casterTool;}

    @Deprecated(forRemoval = true, since = "3.4.0")
    public CasterType getType() {
        return this.type == null ? this.wrappedCaster.getCasterType() : type;
    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    /**
     * @param newIndex set the current Index to param, careful with its usage as it might go out of bounds
     */
    public void setCurrentIndex(int newIndex) {
        this.currentIndex = newIndex;
    }

    public boolean isCanceled() {
        return isCanceled;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    public @NotNull Spell getSpell() {
        return spell == null ? new Spell() : spell;
    }

    public @NotNull Spell getRemainingSpell() {
        if (getCurrentIndex() >= getSpell().recipe.size())
            return getSpell().clone().setRecipe(new ArrayList<>());
        return getSpell().clone().setRecipe(new ArrayList<>(getSpell().recipe.subList(getCurrentIndex(), getSpell().recipe.size())));
    }

    @Override
    public SpellContext clone() {
        try {
            SpellContext clone = (SpellContext) super.clone();
            clone.spell = this.spell.clone();
            clone.colors = this.colors.clone();
            clone.tag = this.tag.copy();
            clone.caster = this.caster;
            clone.castingTile = this.castingTile;
            clone.casterTool = this.casterTool.copy();
            clone.type = this.type;
            clone.level = this.level;
            clone.wrappedCaster = this.wrappedCaster;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public ParticleColor getColors() {
        return colors.clone();
    }

    public void setColors(ParticleColor colors) {
        this.colors = colors;
    }

    public void setCaster(@Nullable LivingEntity caster) {
        this.caster = caster;
    }

    public void setCasterTool(ItemStack stack) {
        this.casterTool = stack.copy();
    }

    /**
     * The type of caster that created the spell. Used for removing or changing behaviors of effects that would otherwise cause problems, like GUI opening effects.
     */
    //TODO: Move caster type to own class
    public static class CasterType {
        public static final CasterType RUNE = new CasterType("rune");
        public static final CasterType TURRET = new CasterType("turret");
        public static final CasterType ENTITY = new CasterType("entity");
        public static final CasterType OTHER = new CasterType("other");
        public static final CasterType LIVING_ENTITY = new CasterType("living_entity");
        public static final CasterType PLAYER = new CasterType("player");
        public String id;

        public CasterType(String id) {
            this.id = id;
        }
    }

}
