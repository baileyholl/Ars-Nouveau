package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShearsItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.IForgeShearable;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

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
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {

        if(rayTraceResult instanceof BlockRayTraceResult) {
            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult) rayTraceResult;
            BlockPos blockPos = blockRayTraceResult.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);
            if(!BlockUtil.destroyRespectsClaim(getPlayer(shooter, (ServerWorld) world), world, blockPos))
                return;
            if (isRealPlayer(shooter)) {
                blockState.use(world, (PlayerEntity) shooter, Hand.MAIN_HAND, (BlockRayTraceResult) rayTraceResult);
            } else if (world instanceof ServerWorld) {
                FakePlayer player = new ANFakePlayer((ServerWorld) world);
                // NOTE: Get IInteractResponder held item if we have one
                ItemStack stack = shooter instanceof IInteractResponder ? ((IInteractResponder) shooter).getHeldItem().copy() : ItemStack.EMPTY;
                player.setItemInHand(Hand.MAIN_HAND, stack);

                blockState.use(world, player, Hand.MAIN_HAND, (BlockRayTraceResult) rayTraceResult);

                // NOTE: Return all items that were used by the fake player
                // NOTE: Returning of items should probably not only be done for shears. But for now it's better than not returning shears
                List<ItemStack> items = new ArrayList<>();
                if (player.getMainHandItem().getItem() instanceof ShearsItem) {
                    items.addAll(player.inventory.items);
                    items.addAll(player.inventory.armor);
                    items.addAll(player.inventory.offhand);
                    returnItems(rayTraceResult, world, shooter, augments, spellContext, items);
                }


            }
        }
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity e = ((EntityRayTraceResult) rayTraceResult).getEntity();

            if(e instanceof AnimalEntity){
                if(shooter instanceof PlayerEntity){
                    ((AnimalEntity) e).mobInteract((PlayerEntity) shooter, Hand.MAIN_HAND);

                }else if (shooter instanceof IInteractResponder){
                    FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((ServerWorld)world);
                    fakePlayer.inventory.clearContent();
                    fakePlayer.setPos(e.getX(), e.getY(), e.getZ());
                    ItemStack stack = ((IInteractResponder) shooter).getHeldItem().copy();
                    fakePlayer.setItemInHand(Hand.MAIN_HAND, stack);
                    e.interact( fakePlayer, Hand.MAIN_HAND);
                    List<ItemStack> items = new ArrayList<>();
                    if(e instanceof IForgeShearable && fakePlayer.getMainHandItem().getItem() instanceof ShearsItem && ((IForgeShearable) e).isShearable(fakePlayer.getMainHandItem(), world, e.blockPosition())){
                        items.addAll(((IForgeShearable) e).onSheared(fakePlayer, fakePlayer.getMainHandItem(), world, e.blockPosition(),
                                EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.enchantment.Enchantments.BLOCK_FORTUNE,fakePlayer.getMainHandItem())));
                    }
                    items.addAll(fakePlayer.inventory.items);
                    items.addAll(fakePlayer.inventory.armor);
                    items.addAll(fakePlayer.inventory.offhand);
                    returnItems(rayTraceResult, world, shooter, augments, spellContext, items);
                }
            }
        }
    }

    public void returnItems(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext, List<ItemStack> items){
        for(ItemStack i : items){
            if(shooter instanceof IPickupResponder){
                ItemStack leftOver = ((IPickupResponder) shooter).onPickup(i);
                if(!leftOver.isEmpty())
                    world.addFreshEntity(new ItemEntity(world, rayTraceResult.getLocation().x, rayTraceResult.getLocation().y, rayTraceResult.getLocation().z, leftOver));
            }else{
                world.addFreshEntity(new ItemEntity(world, rayTraceResult.getLocation().x, rayTraceResult.getLocation().y, rayTraceResult.getLocation().z, i));
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return nonAirAnythingSuccess(rayTraceResult, world);
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.LEVER;
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
    public int getManaCost() {
        return 10;
    }
}