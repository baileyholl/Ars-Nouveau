package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.ModConfig;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.IPickupResponder;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.items.VoidJar;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
        int expansion = 5 + getBuffCount(augments, AugmentAOE.class);

        List<ItemEntity> entityList = world.getEntitiesWithinAABB(ItemEntity.class, new AxisAlignedBB(pos.east(expansion).north(expansion).up(expansion),
                pos.west(expansion).south(expansion).down(expansion)));
        for(ItemEntity i : entityList){

            if(isRealPlayer(shooter) && spellContext.castingTile == null){
                ItemStack stack = i.getItem();
                PlayerEntity player = (PlayerEntity) shooter;
                VoidJar.tryVoiding(player, stack);
                if(!player.addItemStackToInventory(stack)){
                    i.setPosition(player.getPosX(), player.getPosY(), player.getPosZ());
                }
//                i.onCollideWithPlayer((PlayerEntity) shooter);
            }else if(shooter instanceof IPickupResponder){
                i.setItem(((IPickupResponder) shooter).onPickup(i.getItem()));
            }else if(spellContext.castingTile instanceof IPickupResponder){
                i.setItem(((IPickupResponder) spellContext.castingTile).onPickup(i.getItem()));
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return true;
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
