package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.event.EffectResolveEvent;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.mana.IManaCap;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.capability.CapabilityRegistry;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.List;

import static com.hollingsworth.arsnouveau.api.util.ManaUtil.getPlayerDiscounts;

public class SpellResolver {
    public AbstractCastMethod castType;
    public Spell spell;
    public SpellContext spellContext;
    public boolean silent;
    private final ISpellValidator spellValidator;

    public @Nullable HitResult hitResult = null;

    public SpellResolver(SpellContext spellContext){
        this.spell = spellContext.getSpell();
        this.castType = spellContext.getSpell().getCastMethod();
        this.spellContext = spellContext;
        this.spellValidator = ArsNouveauAPI.getInstance().getSpellCastingSpellValidator();
    }

    @Deprecated(forRemoval = true)
    public SpellResolver(ISpellCaster spellCaster, @Nullable LivingEntity castingEntity){
        this(new SpellContext(spellCaster.getSpell(), castingEntity).withColors(spellCaster.getColor()));
    }

    public SpellResolver withSilent(boolean isSilent){
        this.silent = isSilent;
        return this;
    }

    public boolean canCast(LivingEntity entity){
        // Validate the spell
        List<SpellValidationError> validationErrors = spellValidator.validate(spell.recipe);

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

    boolean enoughMana(LivingEntity entity){
        int totalCost = getCastingCost(spell, entity);
        IManaCap manaCap = CapabilityRegistry.getMana(entity).orElse(null);
        if(manaCap == null)
            return false;
        boolean canCast = totalCost <= manaCap.getCurrentMana() || (entity instanceof Player  player && player.isCreative());
        if(!canCast && !entity.getCommandSenderWorld().isClientSide && !silent)
            PortUtil.sendMessageNoSpam(entity,new TranslatableComponent("ars_nouveau.spell.no_mana"));
        return canCast;
    }

    public boolean postEvent(){
        return SpellUtil.postEvent(new SpellCastEvent(spellContext.caster, spell, spellContext));
    }

    private SpellStats getCastStats(){
        LivingEntity caster = spellContext.caster;
        return  new SpellStats.Builder()
                .setAugments(spell.getAugments(0, caster))
                .addItemsFromEntity(caster)
                .build(castType, this.hitResult, caster.level, caster, spellContext);
    }

    public boolean onCast(ItemStack stack, Level level){
        if(canCast(spellContext.caster) && !postEvent()) {
            this.hitResult = null;
            castType.onCast(stack, spellContext.caster, level, getCastStats(), spellContext, this);
            return true;
        }
        return false;
    }

    public boolean onCastOnBlock(BlockHitResult blockRayTraceResult){
        if(canCast(spellContext.caster) && !postEvent()) {
            this.hitResult = blockRayTraceResult;
            castType.onCastOnBlock(blockRayTraceResult, spellContext.caster, getCastStats(), spellContext, this);
            return true;
        }
        return false;
    }

    // Gives context for InteractionHand
    public boolean onCastOnBlock(UseOnContext context){
        if(canCast(spellContext.caster) && !postEvent()) {
            this.hitResult = context.hitResult;
            castType.onCastOnBlock(context, getCastStats(), spellContext, this);
            return true;
        }
        return false;
    }

    public boolean onCastOnEntity(ItemStack stack, Entity target, InteractionHand hand){
        if(canCast(spellContext.caster) && !postEvent()) {
            this.hitResult = new EntityHitResult(target);
            castType.onCastOnEntity(stack, spellContext.caster, target, hand, getCastStats(), spellContext, this);
            return true;
        }
        return false;
    }

    public void onResolveEffect(Level world, HitResult result){
        this.hitResult = result;
        this.resolveAllEffects(world);
    }

    protected void resolveAllEffects(Level world){
        spellContext.resetCastCounter();
        LivingEntity shooter = spellContext.getUnwrappedCaster(world);
        SpellResolveEvent.Pre spellResolveEvent = new SpellResolveEvent.Pre(world, shooter, this.hitResult, spell, spellContext);
        MinecraftForge.EVENT_BUS.post(spellResolveEvent);
        if(spellResolveEvent.isCanceled())
            return;

        while(spellContext.hasNextPart()){
            AbstractSpellPart part = spellContext.nextPart();
            if(part == null)
                break;
            if(part instanceof AbstractAugment)
                continue;
            SpellStats.Builder builder = new SpellStats.Builder();
            List<AbstractAugment> augments = spell.getAugments(spellContext.getCurrentIndex() - 1, shooter);
            SpellStats stats = builder
                    .setAugments(augments)
                    .addItemsFromEntity(shooter)
                    .build(part, this.hitResult, world, shooter, spellContext);
            if(part instanceof AbstractEffect effect){
                EffectResolveEvent.Pre preEvent = new EffectResolveEvent.Pre(world, shooter,  this.hitResult, spell, spellContext, effect, stats);
                if(MinecraftForge.EVENT_BUS.post(preEvent))
                    continue;
                effect.onResolve(this.hitResult, world, shooter, stats, spellContext, this);
                MinecraftForge.EVENT_BUS.post(new EffectResolveEvent.Post(world, shooter,  this.hitResult, spell, spellContext, effect, stats));
            }
        }
        MinecraftForge.EVENT_BUS.post(new SpellResolveEvent.Post(world, shooter, this.hitResult, spell, spellContext));
    }

    public void expendMana(LivingEntity entity){
        int totalCost = getCastingCost(spell, entity);
        CapabilityRegistry.getMana(entity).ifPresent(mana -> mana.removeMana(totalCost));
    }
    // TODO: Change to getResolveCost. Pull spell and entity from SpellContext
    public int getCastingCost(Spell spell, LivingEntity e){
        int cost = spell.getCastingCost() - getPlayerDiscounts(e);
        return Math.max(cost, 0);
    }

    /**
     * Addons can override this to return their custom spell resolver if you change the way logic resolves.
     */
    public SpellResolver getNewResolver(SpellContext context){
        return new SpellResolver(context);
    }

    // Safely unwrap the living entity in the case that the caster is null, aka being cast by a non-player.
    // Moved to SpellContext.
    @Deprecated(forRemoval = true)
    public static LivingEntity getUnwrappedCaster(Level world, SpellContext spellContext){
        LivingEntity shooter = spellContext.getCaster();
        if(shooter == null && spellContext.castingTile != null) {
            shooter = ANFakePlayer.getPlayer((ServerLevel) world);
            BlockPos pos = spellContext.castingTile.getBlockPos();
            shooter.setPos(pos.getX(), pos.getY(), pos.getZ());
        }
        shooter = shooter == null ?  ANFakePlayer.getPlayer((ServerLevel) world) : shooter;
        return shooter;
    }

    @Deprecated(forRemoval = true)
    public boolean postEvent(LivingEntity entity){
        return postEvent();
    }

    @Deprecated(forRemoval = true)
    private SpellStats getCastStats(LivingEntity caster){
        return getCastStats();
    }

    @Deprecated(forRemoval = true)
    public boolean onCastOnEntity(ItemStack stack, LivingEntity playerIn, Entity target, InteractionHand hand){
        return onCastOnEntity(stack, target, hand);
    }

    @Deprecated(forRemoval = true)
    public boolean onCast(ItemStack stack, LivingEntity caster, Level world){
        return onCast(stack, world);
    }

    @Deprecated(forRemoval = true)
    // Caster obtained from spellContext
    public boolean onCastOnBlock(BlockHitResult blockRayTraceResult, LivingEntity caster){
        return onCastOnBlock(blockRayTraceResult);
    }

    @Deprecated(forRemoval = true)
    public void onResolveEffect(Level world, LivingEntity shooter, HitResult result){
        this.onResolveEffect(world, result);
    }

    @Deprecated(forRemoval = true)
    public static void resolveEffects(Level world, LivingEntity shooter, HitResult result, Spell spell, SpellContext spellContext){
        SpellResolver resolver = new SpellResolver(spellContext);
        resolver.hitResult = result;
        resolver.resolveAllEffects(world);
    }

    // TODO: delete bookwyrm
    @Deprecated(forRemoval = true)
    public boolean wouldAllEffectsDoWork(HitResult result, Level world, LivingEntity entity,  SpellStats stats){
        for(AbstractSpellPart spellPart : spell.recipe){
            if(spellPart instanceof AbstractEffect){
                if(!((AbstractEffect) spellPart).wouldSucceed(result, world, entity, stats, spellContext)){
                    return false;
                }
            }
        }
        return true;
    }

    @Deprecated(forRemoval = true)
    public boolean wouldCastOnBlockSuccessfully(BlockHitResult blockRayTraceResult, LivingEntity caster){
        return castType.wouldCastOnBlockSuccessfully(blockRayTraceResult, caster,  getCastStats(caster), this);
    }
}
