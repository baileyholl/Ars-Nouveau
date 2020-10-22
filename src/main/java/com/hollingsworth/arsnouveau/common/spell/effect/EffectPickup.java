package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EffectPickup extends AbstractEffect {
    public EffectPickup() {
        super(ModConfig.EffectPickupID, "Item Pickup");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        BlockPos pos = new BlockPos(rayTraceResult.getHitVec());
        int expansion = getBuffCount(augments, AugmentAOE.class);
        List<ItemEntity> entityList = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos.east(3 + expansion).north(3 + expansion).up(3 + expansion),
                pos.west(3 +expansion).south(3+expansion).down(3+expansion)));
        for(ItemEntity i : entityList){
            if(shooter != null)
                i.setPosition(shooter.getPosX(), shooter.getPosY(), shooter.getPosZ());
            if(shooter instanceof PlayerEntity){
                i.onCollideWithPlayer((PlayerEntity) shooter);
            }else if(shooter instanceof IPickupResponder){
                i.setItem(((IPickupResponder) shooter).onPickup(i.getItem()));
            }else if(spellContext.castingTile instanceof IPickupResponder){
                i.setItem(((IPickupResponder) spellContext.castingTile).onPickup(i.getItem()));
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        BlockPos pos = new BlockPos(rayTraceResult.getHitVec());
        int expansion = getBuffCount(augments, AugmentAOE.class);
        return !world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos.east(3 + expansion).north(3 + expansion).up(3 + expansion),
                pos.west(3 +expansion).south(3+expansion).down(3+expansion))).isEmpty();
    }

    @Override
    protected String getBookDescription() {
        return "Picks up nearby items in a medium radius where this spell is activated. The range may be expanded with AOE.";
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.HOPPER;
    }

    @Override
    public int getManaCost() {
        return 10;
    }
}
