package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectPickup extends AbstractEffect {
    public static EffectPickup INSTANCE = new EffectPickup();

    private EffectPickup() {
        super(GlyphLib.EffectPickupID, "Item Pickup");
    }

    @Override
    public void onResolve(RayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        BlockPos pos = new BlockPos(rayTraceResult.getLocation());
        int expansion = 2 + spellStats.getBuffCount(AugmentAOE.INSTANCE);

        List<ItemEntity> entityList = world.getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(pos.east(expansion).north(expansion).above(expansion),
                pos.west(expansion).south(expansion).below(expansion)));
        for(ItemEntity i : entityList){

            if(isRealPlayer(shooter) && spellContext.castingTile == null){
                ItemStack stack = i.getItem();
                PlayerEntity player = (PlayerEntity) shooter;
                if(MinecraftForge.EVENT_BUS.post(new PlayerEvent.ItemPickupEvent(player, i, stack)))
                    continue;
                if(!stack.isEmpty() && !player.addItem(stack)){
                    i.setPos(player.getX(), player.getY(), player.getZ());
                }

            }else if(shooter instanceof IPickupResponder){
                i.setItem(((IPickupResponder) shooter).onPickup(i.getItem()));
            }else if(spellContext.castingTile instanceof IPickupResponder){
                i.setItem(((IPickupResponder) spellContext.castingTile).onPickup(i.getItem()));
            }
        }
        List<ExperienceOrbEntity> orbList = world.getEntitiesOfClass(ExperienceOrbEntity.class, new AxisAlignedBB(pos.east(expansion).north(expansion).above(expansion),
                pos.west(expansion).south(expansion).below(expansion)));
        for(ExperienceOrbEntity i : orbList) {
            if (isRealPlayer(shooter) && spellContext.castingTile == null) {
                PlayerEntity player = (PlayerEntity) shooter;
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp(player, i)))
                    continue;
                player.giveExperiencePoints(i.value);
                i.remove();
            }
        }
    }

    @Override
    public boolean wouldSucceed(RayTraceResult rayTraceResult, World world, LivingEntity shooter, List<AbstractAugment> augments) {
        return true;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE);
    }

    @Override
    public String getBookDescription() {
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

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
