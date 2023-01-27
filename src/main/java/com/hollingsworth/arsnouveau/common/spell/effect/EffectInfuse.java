package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.item.inv.ExtractedStack;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.AnimBlockSummon;
import com.hollingsworth.arsnouveau.common.items.PotionFlask;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectInfuse extends AbstractEffect {
    public static EffectInfuse INSTANCE = new EffectInfuse(GlyphLib.EffectInfuseID, "Infuse");

    public EffectInfuse(String tag, String description) {
        super(tag, description);
    }

    public EffectInfuse(ResourceLocation tag, String description) {
        super(tag, description);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockState state = world.getBlockState(rayTraceResult.getBlockPos());

        if (!state.isAir() && !(state.getBlock() instanceof EntityBlock) && canSummon(shooter)){
            AnimBlockSummon blockSummon = new AnimBlockSummon(world, state);
            blockSummon.setPos(rayTraceResult.getLocation().add(0,1,0));
            blockSummon.setTicksLeft(60000);
            blockSummon.setTarget(shooter.getLastHurtMob());
            blockSummon.setAggressive(true);
            blockSummon.setTame(true);
            blockSummon.tame((Player) shooter);
            summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, blockSummon);
            world.setBlockAndUpdate(rayTraceResult.getBlockPos(), Blocks.AIR.defaultBlockState());
        }
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof LivingEntity livingEntity) {
            InventoryManager manager = spellContext.getCaster().getInvManager();
            ExtractedStack extractedFlask = manager.extractItem(i -> {
                PotionFlask.FlaskData data = new PotionFlask.FlaskData(i);
                return !data.getPotion().isEmpty() && data.getCount() > 0;
            }, 1);
            if (!extractedFlask.isEmpty()) {
                PotionFlask.FlaskData data = new PotionFlask.FlaskData(extractedFlask.getStack());
                data.getPotion().applyEffects(shooter, shooter, livingEntity);
                data.setCount(data.getCount() - 1);
                extractedFlask.returnOrDrop(world, shooter.getOnPos());
            } else {
                ExtractedStack potion = manager.extractItem(i -> i.getItem() instanceof PotionItem, 1);
                if (!potion.isEmpty()) {
                    ItemStack stack = potion.getStack();
                    PotionData potionData = new PotionData(stack);
                    potionData.applyEffects(shooter, shooter, livingEntity);
                    stack.shrink(1);
                    potion.replaceAndReturnOrDrop(new ItemStack(Items.GLASS_BOTTLE), world, shooter.getOnPos());
                }
            }
        }
    }

    @Override
    public int getDefaultManaCost() {
        return 30;
    }

    @NotNull
    @Override
    protected Set<AbstractAugment> getCompatibleAugments() {
        return setOf();
    }

    @Override
    public SpellTier defaultTier() {
        return SpellTier.TWO;
    }

    @Override
    public String getBookDescription() {
        return "Infuses a target with a potion from your flask or held potions. Consumes the potion.";
    }
}
