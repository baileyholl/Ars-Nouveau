package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import javax.annotation.Nullable;
import java.util.List;

public class EffectInteract extends AbstractEffect {
    public EffectInteract() {
        super(ModConfig.EffectInteractID, "Interact");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        if(rayTraceResult instanceof BlockRayTraceResult){
            if(shooter instanceof PlayerEntity)
                world.getBlockState(((BlockRayTraceResult) rayTraceResult).getPos()).onBlockActivated(world, (PlayerEntity)shooter, Hand.MAIN_HAND, (BlockRayTraceResult)rayTraceResult);
            else if(world instanceof ServerWorld){
                world.getBlockState(((BlockRayTraceResult) rayTraceResult).getPos()).onBlockActivated(world, FakePlayerFactory.getMinecraft((ServerWorld)world), Hand.MAIN_HAND, (BlockRayTraceResult)rayTraceResult);
            }
        }
        if(rayTraceResult instanceof EntityRayTraceResult){
            Entity e = ((EntityRayTraceResult) rayTraceResult).getEntity();
            if(e instanceof AgeableEntity){
                if(shooter instanceof PlayerEntity){
                    e.applyPlayerInteraction((PlayerEntity) shooter, rayTraceResult.getHitVec(), Hand.MAIN_HAND);
                }else{
                    FakePlayer fakePlayer = FakePlayerFactory.getMinecraft((ServerWorld)world);
                    fakePlayer.setHeldItem(Hand.MAIN_HAND, shooter.getHeldItemMainhand());
                    e.applyPlayerInteraction( fakePlayer, rayTraceResult.getHitVec(), Hand.MAIN_HAND);
                }
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        return nonAirAnythingSuccess(rayTraceResult, world);
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.LEVER;
    }

    @Override
    protected String getBookDescription() {
        return "Interacts with blocks or entities as it were a player. Useful for reaching levers, chests, or animals.";
    }

    @Override
    public int getManaCost() {
        return 10;
    }
}
