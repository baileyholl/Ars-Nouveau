package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.item.inv.ExtractedStack;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentSensitive;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectInteract extends AbstractEffect {
    public static EffectInteract INSTANCE = new EffectInteract();

    private EffectInteract() {
        super(GlyphLib.EffectInteractID, "Interact");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity e = rayTraceResult.getEntity();
        Player player = getPlayer(shooter, (ServerLevel) world);
        if (!isRealPlayer(shooter)) {
            InventoryManager manager = spellContext.getCaster().getInvManager();
            player = setupFakeInventory(spellContext, world);
            useOnEntity(player, spellStats, e);
            for(ItemStack i : player.inventory.items){
                manager.insertOrDrop(i, world, e.blockPosition());
            }
        }else {
            useOnEntity(player, spellStats, e);
        }
    }

    public InteractionHand getHand(Player player) {
        return player instanceof ANFakePlayer ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
    }

    public boolean handleBucket(ItemStack item, BucketItem bucket, Player player, BlockState state, Level world, BlockPos pos, BlockHitResult rayTraceResult, InteractionHand hand) {
        if (bucket.getFluid() == Fluids.EMPTY) {
            boolean isBucketPickup = state.getBlock() instanceof BucketPickup && world.getFluidState(pos) != Fluids.EMPTY.defaultFluidState();
            BlockPos target = isBucketPickup ? pos : pos.relative(rayTraceResult.getDirection());
            if (world.getFluidState(target) == Fluids.EMPTY.defaultFluidState()) {
                return false;
            }
            BlockState targetState = world.getBlockState(target);
            if (!(targetState.getBlock() instanceof BucketPickup bp)) {
                return false;
            }
            ItemStack pickup = bp.pickupBlock(player, world, target, targetState);
            if (!pickup.isEmpty() && !player.getAbilities().instabuild) {
                bp.getPickupSound(targetState).ifPresent(sound -> player.playSound(sound, 1.0F, 1.0F));
                world.gameEvent(player, GameEvent.FLUID_PICKUP, target);
                ItemStack result = ItemUtils.createFilledResult(item, player, pickup);

                if (player instanceof ServerPlayer serverPlayer) {
                    CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, item);
                }

                player.awardStat(Stats.ITEM_USED.get(bucket));
                if(player.getItemInHand(hand).isEmpty()){
                    player.setItemInHand(hand, result);
                }else{
                    if(!player.addItem(result)){
                        player.level.addFreshEntity(new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), result));
                    }
                }
            }
            return !pickup.isEmpty();

        }
        boolean placed = bucket.emptyContents(player, world, pos, rayTraceResult, item);
        if (placed) {
            if (!player.getAbilities().instabuild) {
                ItemStack result = ItemUtils.createFilledResult(item, player, new ItemStack(Items.BUCKET));
                if(player.getItemInHand(hand).isEmpty()){
                    player.setItemInHand(hand, result);
                }else{
                    if(!player.addItem(result)){
                        player.level.addFreshEntity(new ItemEntity(player.level, player.getX(), player.getY(), player.getZ(), result));
                    }
                }
            }
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, item);
            }
            player.awardStat(Stats.ITEM_USED.get(bucket));
        }
        return placed;
    }

    public void useOnEntity(Player player, SpellStats spellStats, Entity target) {
        if (spellStats.isSensitive()) {
            ItemStack item = player.getItemInHand(getHand(player));
            if(target instanceof  LivingEntity livingEntity) {
                InteractionResult res = item.interactLivingEntity(player, livingEntity, getHand(player));
                if (res != InteractionResult.SUCCESS) {
                    target.interact(player, getHand(player));
                }
            }else{
                target.interact(player, getHand(player));
            }
        } else {
            player.interactOn(target, InteractionHand.MAIN_HAND);
        }
    }

    public void useOnBlock(Player player, SpellStats spellStats, BlockPos pos, BlockState state, Level world, BlockHitResult rayTraceResult) {

        if (spellStats.isSensitive()) {
            ItemStack item = player.getItemInHand(getHand(player));
            if (item.getItem() instanceof BucketItem bucket) {
                handleBucket(item, bucket, player, state, world, pos, rayTraceResult, getHand(player));
                return;
            }
            UseOnContext context = new UseOnContext(player, getHand(player), rayTraceResult);
            item.useOn(context);
        } else {
            state.useItemOn(player.getItemInHand(InteractionHand.MAIN_HAND), world, player, InteractionHand.MAIN_HAND, rayTraceResult);
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world,@NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos blockPos = rayTraceResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, blockPos))
            return;
        if(blockState.is(BlockTagProvider.INTERACT_BLACKLIST)){
            return;
        }
        Player player = getPlayer(shooter, (ServerLevel) world);
        if(isRealPlayer(shooter)){
            useOnBlock(player, spellStats, blockPos, blockState, world, rayTraceResult);
        }else{
            InventoryManager manager = spellContext.getCaster().getInvManager();
            player = setupFakeInventory(spellContext, world);
            useOnBlock(player, spellStats, blockPos, blockState, world, rayTraceResult);
            for(ItemStack i : player.inventory.items){
                manager.insertOrDrop(i, world, rayTraceResult.getBlockPos());
            }
            for(ItemStack i : player.inventory.offhand){
                manager.insertOrDrop(i, world, rayTraceResult.getBlockPos());
            }
        }
    }

    public FakePlayer setupFakeInventory(SpellContext context, Level level){
        InventoryManager manager = context.getCaster().getInvManager();
        ANFakePlayer player = ANFakePlayer.getPlayer((ServerLevel) level);
        player.inventory.clearContent();
        player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        ExtractedStack stack = manager.extractItem(i -> !i.isEmpty(), 1);
        if(!stack.isEmpty()){
            player.setItemSlot(EquipmentSlot.MAINHAND, stack.getStack().copy());
        }
        return player;
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentSensitive.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Interacts with blocks or entities as it were a player. Useful for reaching levers, chests, or animals. Sensitive will use your off-hand item on the block or entity.";
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

   @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}