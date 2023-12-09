package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
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
    private SpellContext previousContext;

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

    public SpellContext withParent(SpellContext parent){
        this.previousContext = parent;
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

    public SpellContext popContext(){
        Spell remainder = getRemainingSpell();
        int depth = 0;
        for(AbstractSpellPart spellPart : remainder.recipe){
            //check some escape contexts might be popping other contexts, not ours
            if(ArsNouveauAPI.IsContextCreator(spellPart)){
                depth +=1;
            }
            if(spellPart instanceof IContextManipulator manipulator){
                depth -=1;
                //actually pop the current context
                if(depth <= 0) {
                    SpellContext newContext = manipulator.manipulate(this);
                    if (newContext != null) {
                        newContext.previousContext = this;
                        return newContext;
                    }
                }
            }
        }
        setCanceled(true);
        return this;
    }

    public boolean hasNextPart() {
        return spell.isValid() && !isCanceled() && currentIndex < spell.recipe.size();
    }

    public @Nullable AbstractSpellPart canceledAt(){
        if(isCanceled && currentIndex > 0){
            return spell.recipe.get(currentIndex - 1);
        }
        return null;
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

    /**
     * Sets isCanceled calls {@link AbstractSpellPart#onContextCanceled(SpellContext)} for all remaining spell parts if canceled is true.
     * @param canceled The new canceled state.
     * @return The new canceled state after the spell parts have been notified if canceled was true.
     */
    public boolean setCanceled(boolean canceled) {
        isCanceled = canceled;
        if(isCanceled) {
            Spell remainder = getRemainingSpell();
            for (AbstractSpellPart spellPart : remainder.recipe) {
                spellPart.onContextCanceled(this);
            }
        }
        return isCanceled;
    }

    /**
     * Sets isCanceled to true without the side effects of {@link SpellContext#setCanceled(boolean)}.
     * This is used for abrupt termination of a spell. setCanceled should be used whenever possible.
     */
    public void stop(){
        this.isCanceled = true;
    }

    public @NotNull Spell getSpell() {
        return spell == null ? new Spell() : spell;
    }

    public @NotNull Spell getRemainingSpell() {
        Spell remainder = getSpell().clone();
        if (getCurrentIndex() >= getSpell().recipe.size())
            return remainder.setRecipe(new ArrayList<>());

        return getSpell().clone().setRecipe(new ArrayList<>(getSpell().recipe.subList(getCurrentIndex(), getSpell().recipe.size())));
    }

    public @Nullable SpellContext getPreviousContext(){
        return previousContext;
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
            /*
            LoggerContext.getContext().getLogger(SpellContext.class).info("previous context:"+previousContext);
            if(previousContext != null){
                LoggerContext.getContext().getLogger(SpellContext.class).info("previous context spell:"+previousContext.getSpell().serialize().toString());
            }
             */
            clone.previousContext = this.previousContext == null ? null : this.previousContext.clone();
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
