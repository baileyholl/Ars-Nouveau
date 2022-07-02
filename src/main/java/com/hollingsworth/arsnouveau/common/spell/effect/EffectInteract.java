package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EffectInteract extends AbstractEffect {
    public static EffectInteract INSTANCE = new EffectInteract();

    private EffectInteract() {
        super(GlyphLib.EffectInteractID, "Interact");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        Entity e = rayTraceResult.getEntity();
        if (e instanceof Animal) {
            if (shooter instanceof Player) {
                ((Animal) e).mobInteract((Player) shooter, InteractionHand.MAIN_HAND);

            } else if (shooter instanceof IInteractResponder) {
                FakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerLevel) world);
                fakePlayer.inventory.clearContent();
                fakePlayer.setPos(e.getX(), e.getY(), e.getZ());
                ItemStack stack = ((IInteractResponder) shooter).getHeldItem().copy();
                fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stack);
                e.interact(fakePlayer, InteractionHand.MAIN_HAND);
                List<ItemStack> items = new ArrayList<>();
                if (e instanceof IForgeShearable && fakePlayer.getMainHandItem().getItem() instanceof ShearsItem && ((IForgeShearable) e).isShearable(fakePlayer.getMainHandItem(), world, e.blockPosition())) {
                    items.addAll(((IForgeShearable) e).onSheared(fakePlayer, fakePlayer.getMainHandItem(), world, e.blockPosition(),
                            fakePlayer.getMainHandItem().getEnchantmentLevel(Enchantments.BLOCK_FORTUNE)));
                }
                items.addAll(fakePlayer.inventory.items);
                items.addAll(fakePlayer.inventory.armor);
                items.addAll(fakePlayer.inventory.offhand);
                returnItems(rayTraceResult, world, shooter, items);
            }
        }
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos blockPos = rayTraceResult.getBlockPos();
        BlockState blockState = world.getBlockState(blockPos);
        if (!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerLevel) world), world, blockPos))
            return;

        // Stop duping shears on blocks that create a new itemstack on right click
        if (world.getBlockEntity(blockPos) != null && world.getBlockEntity(blockPos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent())
            return;

        if (isRealPlayer(shooter)) {
            blockState.use(world, (Player) shooter, InteractionHand.MAIN_HAND, rayTraceResult);
        } else {
            FakePlayer player = ANFakePlayer.getPlayer((ServerLevel) world);
            // NOTE: Get IInteractResponder held item if we have one
            ItemStack stack = shooter instanceof IInteractResponder ? ((IInteractResponder) shooter).getHeldItem().copy() : ItemStack.EMPTY;
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);

            blockState.use(world, player, InteractionHand.MAIN_HAND, rayTraceResult);

            // NOTE: Return all items that were used by the fake player
            // NOTE: Returning of items should probably not only be done for shears. But for now it's better than not returning shears
            List<ItemStack> items = new ArrayList<>();
            if (player.getMainHandItem().getItem() instanceof ShearsItem) {
                items.addAll(player.inventory.items);
                items.addAll(player.inventory.armor);
                items.addAll(player.inventory.offhand);
                returnItems(rayTraceResult, world, shooter, items);
            }


        }
    }

    public void returnItems(HitResult rayTraceResult, Level world, LivingEntity shooter, List<ItemStack> items) {
        for (ItemStack i : items) {
            if (shooter instanceof IPickupResponder) {
                ItemStack leftOver = ((IPickupResponder) shooter).onPickup(i);
                if (!leftOver.isEmpty())
                    world.addFreshEntity(new ItemEntity(world, rayTraceResult.getLocation().x, rayTraceResult.getLocation().y, rayTraceResult.getLocation().z, leftOver));
            } else {
                world.addFreshEntity(new ItemEntity(world, rayTraceResult.getLocation().x, rayTraceResult.getLocation().y, rayTraceResult.getLocation().z, i));
            }
        }
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf();
    }

    @Override
    public String getBookDescription() {
        return "Interacts with blocks or entities as it were a player. Useful for reaching levers, chests, or animals.";
    }

    @Override
    public int getDefaultManaCost() {
        return 10;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}