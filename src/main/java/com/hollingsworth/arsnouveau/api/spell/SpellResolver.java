package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellCostCalcEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.TileCaster;
import com.hollingsworth.arsnouveau.api.util.CuriosUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.NotEnoughManaPacket;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.CapabilityRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.util.ManaUtil.getPlayerDiscounts;

public class SpellResolver implements Cloneable {
    public AbstractCastMethod castType;
    public Spell spell;
    public SpellContext spellContext;
    public boolean silent;
    private final ISpellValidator spellValidator;

    public @Nullable HitResult hitResult = null;
    public @Nullable SpellResolver previousResolver = null;

    public SpellResolver(SpellContext spellContext) {
        this.spell = spellContext.getSpell();
        this.castType = spellContext.getSpell().getCastMethod();
        this.spellContext = spellContext;
        this.spellValidator = ArsNouveauAPI.getInstance().getSpellCastingSpellValidator();
    }

    public SpellResolver withSilent(boolean isSilent) {
        this.silent = isSilent;
        return this;
    }

    public boolean canCast(LivingEntity entity) {
        // Validate the spell
        List<SpellValidationError> validationErrors = spellValidator.validate(spell.unsafeList());

        if (validationErrors.isEmpty()) {
            // Validation successful. We can check the player's mana now.
            return enoughMana(entity);
        } else {
            // Validation failed, explain why if applicable
            if (!silent && !entity.getCommandSenderWorld().isClientSide) {
                // Sending only the first error to avoid spam
                PortUtil.sendMessageNoSpam(entity, validationErrors.get(0).makeTextComponentExisting());
            }
            return false;
        }
    }

    protected boolean enoughMana(LivingEntity entity) {
        int totalCost = getResolveCost();
        boolean enoughMana = spellContext.getCaster().enoughMana(totalCost);

        boolean canCast = enoughMana || (entity instanceof Player player && player.isCreative());
        if (!canCast && entity instanceof ServerPlayer serverPlayer && !silent) {
            PortUtil.sendMessageNoSpam(serverPlayer, Component.translatable("ars_nouveau.spell.no_mana"));
            Networking.sendToPlayerClient(new NotEnoughManaPacket(totalCost), serverPlayer);
        }
        return canCast;
    }

    public SpellCastEvent postEvent() {
        return SpellUtil.postEvent(new SpellCastEvent(spell, spellContext));
    }

    public SpellStats getCastStats() {
        LivingEntity caster = spellContext.getUnwrappedCaster();
        return new SpellStats.Builder()
                .setAugments(spell.getAugments(0, caster))
                .addItemsFromEntity(caster)
                .build(castType, this.hitResult, caster.level, caster, spellContext);
    }

    public boolean onCast(ItemStack stack, Level level) {
        if (canCast(spellContext.getUnwrappedCaster()) && !postEvent().isCanceled()) {
            this.hitResult = null;
            CastResolveType resolveType = castType.onCast(stack, spellContext.getUnwrappedCaster(), level, getCastStats(), spellContext, this);
            if (resolveType == CastResolveType.SUCCESS) {
                expendMana();
            }
            return resolveType.wasSuccess;
        }
        return false;
    }

    public boolean onCastOnBlock(BlockHitResult blockRayTraceResult) {
        if (canCast(spellContext.getUnwrappedCaster()) && !postEvent().isCanceled()) {
            this.hitResult = blockRayTraceResult;
            CastResolveType resolveType = castType.onCastOnBlock(blockRayTraceResult, spellContext.getUnwrappedCaster(), getCastStats(), spellContext, this);
            if (resolveType == CastResolveType.SUCCESS) {
                expendMana();
            }
            return resolveType.wasSuccess;
        }
        return false;
    }

    // Gives context for InteractionHand
    public boolean onCastOnBlock(UseOnContext context) {
        if (canCast(spellContext.getUnwrappedCaster()) && !postEvent().isCanceled()) {
            this.hitResult = context.hitResult;
            CastResolveType resolveType = castType.onCastOnBlock(context, getCastStats(), spellContext, this);
            if (resolveType == CastResolveType.SUCCESS) {
                expendMana();
            }
            return resolveType.wasSuccess;
        }
        return false;
    }

    public boolean onCastOnEntity(ItemStack stack, Entity target, InteractionHand hand) {
        if (canCast(spellContext.getUnwrappedCaster()) && !postEvent().isCanceled()) {
            this.hitResult = new EntityHitResult(target);
            CastResolveType resolveType = castType.onCastOnEntity(stack, spellContext.getUnwrappedCaster(), target, hand, getCastStats(), spellContext, this);
            if (resolveType == CastResolveType.SUCCESS) {
                expendMana();
            }
            return resolveType.wasSuccess;
        }
        return false;
    }


    /**
     * Sets the starting index to 0 and uncancels the spell, then resolves all effects.
     */
    public void onResolveEffect(Level world, HitResult result) {
        this.hitResult = result;
        this.resolveAllEffects(world);
    }

    /**
     * Sets the starting index to 0 and uncancels the spell, then resolves all effects.
     */
    protected void resolveAllEffects(Level world) {
        spellContext.resetCastCounter();
        resume(world);
    }

    /**
     * Attempts to resolve the remaining effects of the SpellContext without restarting.
     */
    public void resume(Level world){
        LivingEntity shooter = spellContext.getUnwrappedCaster();
        SpellResolveEvent.Pre spellResolveEvent = new SpellResolveEvent.Pre(world, shooter, this.hitResult, spell, spellContext, this);
        NeoForge.EVENT_BUS.post(spellResolveEvent);
        if (spellResolveEvent.isCanceled())
            return;
        BlockPos hitPos = this.hitResult instanceof BlockHitResult blockHitResult ? blockHitResult.getBlockPos() : null;
        Entity hitEntity = this.hitResult instanceof EntityHitResult entityHitResult ? entityHitResult.getEntity() : null;

        while (spellContext.hasNextPart()) {
            AbstractSpellPart part = spellContext.nextPart();
            if (part == null)
                break;
            if (part instanceof AbstractAugment || !part.isEnabled())
                continue;
            List<AbstractAugment> augments = spell.getAugments(spellContext.getCurrentIndex() - 1, shooter);
            SpellStats stats = new SpellStats.Builder()
                    .addItemsFromEntity(shooter)
                    .setAugments(augments)
                    .build(part, this.hitResult, world, shooter, spellContext);
            if (!(part instanceof AbstractEffect effect))
                continue;

            EffectResolveEvent.Pre preEvent = new EffectResolveEvent.Pre(world, shooter, this.hitResult, spell, spellContext, effect, stats, this);
            NeoForge.EVENT_BUS.post(preEvent);
            if (preEvent.isCanceled())
                continue;
            effect.onResolve(this.hitResult, world, shooter, stats, spellContext, this);
            if(hitPos != null){
                var resolveListener = world.getCapability(CapabilityRegistry.BLOCK_SPELL_RESOLVE_CAP, hitPos);
                if(resolveListener != null)
                    resolveListener.onResolve(world, shooter, this.hitResult, spell, spellContext, effect, stats, this);
            }

            if(hitEntity != null){
                var resolveListener = hitEntity.getCapability(CapabilityRegistry.ENTITY_SPELL_RESOLVE_CAP);
                if(resolveListener != null)
                    resolveListener.onResolve(world, shooter, this.hitResult, spell, spellContext, effect, stats, this);
            }
            NeoForge.EVENT_BUS.post(new EffectResolveEvent.Post(world, shooter, this.hitResult, spell, spellContext, effect, stats, this));
        }

        NeoForge.EVENT_BUS.post(new SpellResolveEvent.Post(world, shooter, this.hitResult, spell, spellContext, this));
    }

    public void expendMana() {
        if (spellContext.getCaster() instanceof TileCaster tc && tc.getCasterType() == SpellContext.CasterType.TURRET) {
            return;
        }

        int totalCost = getExpendedCost();
        spellContext.getCaster().expendMana(totalCost);
    }

    /**
     * Simulates the cost required to cast a spell
     */
    // TODO: Remove backwards compat for SpellCostCalcEvent
    public int getResolveCost() {
        int cost = spellContext.getSpell().getCost() - getPlayerDiscounts(spellContext.getUnwrappedCaster(), spell, spellContext.getCasterTool());
        SpellCostCalcEvent event = new SpellCostCalcEvent(spellContext, cost);
        NeoForge.EVENT_BUS.post(event);
        SpellCostCalcEvent.Pre preEvent = new SpellCostCalcEvent.Pre(spellContext, event.currentCost);
        NeoForge.EVENT_BUS.post(preEvent);
        cost = Math.max(0, preEvent.currentCost);
        return cost;
    }

    public int getExpendedCost() {
        int cost = spellContext.getSpell().getCost() - getPlayerDiscounts(spellContext.getUnwrappedCaster(), spell, spellContext.getCasterTool());
        SpellCostCalcEvent event = new SpellCostCalcEvent.Post(spellContext, cost);
        NeoForge.EVENT_BUS.post(event);
        cost = Math.max(0, event.currentCost);
        return cost;
    }

    /**
     * Addons can override this to return their custom spell resolver if you change the way logic resolves.
     */
    public SpellResolver getNewResolver(SpellContext context) {
        SpellResolver newResolver = new SpellResolver(context);
        newResolver.previousResolver = this;
        return newResolver;
    }

    /**
     * Check if the caster has a focus when modifying glyph behavior.
     * Addons can override this to check other types of casters like turrets or entities.
     */
    public boolean hasFocus(ItemStack stack) {
        return CuriosUtil.hasItem(spellContext.getUnwrappedCaster(), stack);
    }

    public boolean hasFocus(Item stack) {
        return CuriosUtil.hasItem(spellContext.getUnwrappedCaster(), stack.getDefaultInstance());
    }

    @Override
    public SpellResolver clone() {
        try {
            SpellResolver clone = (SpellResolver) super.clone();
            clone.spellContext = spellContext.clone();
            clone.previousResolver = this.previousResolver != null ? this.previousResolver.clone() : null;
            clone.castType = this.castType;
            clone.spell = this.spell;
            clone.silent = this.silent;
            clone.hitResult = this.hitResult;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
