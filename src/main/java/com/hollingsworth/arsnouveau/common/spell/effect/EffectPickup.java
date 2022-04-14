package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;

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
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        BlockPos pos = new BlockPos(rayTraceResult.getLocation());
        int expansion = 2 + spellStats.getBuffCount(AugmentAOE.INSTANCE);

        List<ItemEntity> entityList = world.getEntitiesOfClass(ItemEntity.class, new AABB(pos.east(expansion).north(expansion).above(expansion),
                pos.west(expansion).south(expansion).below(expansion)));
        for(ItemEntity i : entityList){

            if(isRealPlayer(shooter) && spellContext.castingTile == null){
                ItemStack stack = i.getItem();
                Player player = (Player) shooter;
                if(MinecraftForge.EVENT_BUS.post(new EntityItemPickupEvent(player, i)))
                    continue;
                if(!stack.isEmpty() && !player.addItem(stack)) {
                    i.setPos(player.getX(), player.getY(), player.getZ());
                }
            }else if(shooter instanceof IPickupResponder iPickupResponder){
                i.setItem(iPickupResponder.onPickup(i.getItem()));
            }else if(spellContext.castingTile instanceof IPickupResponder iPickupResponder){
                i.setItem(iPickupResponder.onPickup(i.getItem()));
            }
        }
        List<ExperienceOrb> orbList = world.getEntitiesOfClass(ExperienceOrb.class, new AABB(pos.east(expansion).north(expansion).above(expansion),
                pos.west(expansion).south(expansion).below(expansion)));
        for(ExperienceOrb i : orbList){
            if(isRealPlayer(shooter) && spellContext.castingTile == null){
                Player player = (Player) shooter;
                if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp(player, i)))
                    continue;
                player.giveExperiencePoints(i.value);
                i.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public boolean wouldSucceed(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
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
