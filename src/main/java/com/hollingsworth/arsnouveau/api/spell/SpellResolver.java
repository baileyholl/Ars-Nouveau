package com.hollingsworth.arsnouveau.api.spell;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.common.capability.ManaCapability;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.hollingsworth.arsnouveau.api.util.ManaUtil.getPlayerDiscounts;

public class SpellResolver {
    public AbstractCastMethod castType;
    public Spell spell;
    public final SpellContext spellContext;
    public boolean silent;
    private final ISpellValidator spellValidator;

    @Deprecated // BAD
    public SpellResolver(AbstractCastMethod cast, List<AbstractSpellPart> spell, SpellContext context){
        this.castType = cast;
        this.spell = new Spell(spell);
        this.spellContext = context;
        ArsNouveauAPI api = ArsNouveauAPI.getInstance();
        this.spellValidator = api.getSpellCastingSpellValidator();
    }

    public SpellResolver(SpellContext spellContext){
        this.spell = spellContext.getSpell();
        this.castType = spellContext.getSpell().getCastMethod();
        this.spellContext = spellContext;
        this.spellValidator =  ArsNouveauAPI.getInstance().getSpellCastingSpellValidator();
    }

    @Deprecated
    public SpellResolver(AbstractSpellPart[] spellParts, SpellContext context){
        this(new ArrayList<>(Arrays.asList(spellParts)), context);
    }

    public SpellResolver withSilent(boolean isSilent){
        this.silent = isSilent;
        return this;
    }

    @Deprecated // Removed in favor of Spell constructor
    public SpellResolver(List<AbstractSpellPart> spell, SpellContext context) {
        this(null, spell, context);
        AbstractCastMethod method = null;
        if(spell != null && !spell.isEmpty() && spell.get(0) instanceof AbstractCastMethod)
            method = (AbstractCastMethod) spell.get(0);

        this.castType = method;
    }

    @Deprecated
    public SpellResolver(List<AbstractSpellPart> spell, boolean silent, SpellContext context){
        this(spell, context);
        this.silent = silent;

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
        AtomicBoolean canCast = new AtomicBoolean(false);
        ManaCapability.getMana(entity).ifPresent(mana -> {
            canCast.set(totalCost <= mana.getCurrentMana() || (entity instanceof PlayerEntity &&  ((PlayerEntity) entity).isCreative()));
            if(!canCast.get() && !entity.getCommandSenderWorld().isClientSide && !silent)
                PortUtil.sendMessageNoSpam(entity,new TranslationTextComponent("ars_nouveau.spell.no_mana"));
        });
        return canCast.get();
    }

    public boolean postEvent(LivingEntity entity){
        return SpellUtil.postEvent(new SpellCastEvent(entity, spell));
    }

    public void onCast(ItemStack stack, LivingEntity livingEntity, World world){
        if(canCast(livingEntity) && !postEvent(livingEntity))
            castType.onCast(stack, livingEntity, world, spell.getAugments( 0, livingEntity), spellContext, this);
    }

    public void onCastOnBlock(BlockRayTraceResult blockRayTraceResult, LivingEntity caster){
        if(canCast(caster) && !postEvent(caster))
            castType.onCastOnBlock(blockRayTraceResult, caster, spell.getAugments( 0, caster), spellContext, this);
    }

    public void onCastOnBlock(ItemUseContext context){
        if(canCast(context.getPlayer()) && !postEvent(context.getPlayer()))
            castType.onCastOnBlock(context, spell.getAugments( 0, context.getPlayer()), spellContext, this);
    }

    public void onCastOnEntity(ItemStack stack, LivingEntity playerIn, LivingEntity target, Hand hand){
        if(canCast(playerIn) && !postEvent(playerIn))
            castType.onCastOnEntity(stack, playerIn, target, hand, spell.getAugments(0, playerIn), spellContext, this);
    }

    public void onResolveEffect(World world, LivingEntity shooter, RayTraceResult result){
        SpellResolver.resolveEffects(world, shooter, result, spell, spellContext);
    }

    public static void resolveEffects(World world, LivingEntity shooter, RayTraceResult result, Spell spell, SpellContext spellContext){
        spellContext.resetSpells();
        shooter = getUnwrappedCaster(world, shooter, spellContext);
        for(int i = 0; i < spell.recipe.size(); i++){
            if(spellContext.isCanceled())
                break;
            AbstractSpellPart part = spellContext.nextSpell();
            if(part instanceof AbstractEffect){
                ((AbstractEffect) part).onResolve(result, world, shooter, spell.getAugments(i, shooter), spellContext);
            }
        }
    }

    // Safely unwrap the living entity in the case that the caster is null, aka being cast by a non-player.
    public static LivingEntity getUnwrappedCaster(World world, LivingEntity shooter, SpellContext spellContext){
        if(shooter == null && spellContext.castingTile != null) {
            shooter = FakePlayerFactory.getMinecraft((ServerWorld) world);
            BlockPos pos = spellContext.castingTile.getBlockPos();
            shooter.setPos(pos.getX(), pos.getY(), pos.getZ());
        }
        shooter = shooter == null ? FakePlayerFactory.getMinecraft((ServerWorld) world) : shooter;
        return shooter;
    }

    public boolean wouldAllEffectsDoWork(RayTraceResult result, World world, LivingEntity entity, List<AbstractAugment> augments){
        for(AbstractSpellPart spellPart : spell.recipe){
            if(spellPart instanceof AbstractEffect){
                if(!((AbstractEffect) spellPart).wouldSucceed(result, world, entity, augments)){
                    return false;
                }
            }
        }
        return true;
    }

    public boolean wouldCastSuccessfully(@Nullable ItemStack stack, LivingEntity caster, World world, List<AbstractAugment> augments){
        return castType.wouldCastSuccessfully(stack, caster, world, augments, this);
    }

    public boolean wouldCastOnBlockSuccessfully(ItemUseContext context, List<AbstractAugment> augments){
        return castType.wouldCastOnBlockSuccessfully(context, augments,this );
    }

    public boolean wouldCastOnBlockSuccessfully(BlockRayTraceResult blockRayTraceResult, LivingEntity caster){
        return castType.wouldCastOnBlockSuccessfully(blockRayTraceResult, caster,  spell.getAugments(0, caster), this);
    }

    public boolean wouldCastOnEntitySuccessfully(@Nullable ItemStack stack, LivingEntity caster, LivingEntity target, Hand hand, List<AbstractAugment> augments){
        return castType.wouldCastOnEntitySuccessfully(stack, caster, target, hand, augments,this);
    }


    public void expendMana(LivingEntity entity){
        int totalCost = getCastingCost(spell, entity);
        ManaCapability.getMana(entity).ifPresent(mana -> mana.removeMana(totalCost));
    }

    public int getCastingCost(Spell spell, LivingEntity e){
        int cost = spell.getCastingCost() - getPlayerDiscounts(e);
        return Math.max(cost, 0);
    }
}
