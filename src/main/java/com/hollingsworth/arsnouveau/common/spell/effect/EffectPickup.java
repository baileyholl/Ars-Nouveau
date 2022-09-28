package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAOE;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = new BlockPos(rayTraceResult.getLocation());
        double expansion = 2 + spellStats.getAoeMultiplier();
        Vec3 posVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());

        List<ItemEntity> entityList = world.getEntitiesOfClass(ItemEntity.class, new AABB(
                posVec.add(expansion, expansion, expansion), posVec.subtract(expansion, expansion, expansion)));
        InventoryManager manager = spellContext.getCaster().getInvManager().extractSlotMax(-1);
        for (ItemEntity i : entityList) {
            ItemStack stack = i.getItem();
            if (stack.isEmpty() || MinecraftForge.EVENT_BUS.post(new EntityItemPickupEvent(getPlayer(shooter, (ServerLevel) world), i)))
                continue;
            stack = manager.insertStack(stack);
            i.setItem(stack);
        }
        List<ExperienceOrb> orbList = world.getEntitiesOfClass(ExperienceOrb.class, new AABB(
                posVec.add(expansion, expansion, expansion), posVec.subtract(expansion, expansion, expansion)));
        for (ExperienceOrb i : orbList) {
            if (shooter instanceof Player player && isNotFakePlayer(player) && spellContext.castingTile == null) {
                if (MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp(player, i)))
                    continue;
                player.giveExperiencePoints(i.value);
                i.remove(Entity.RemovalReason.DISCARDED);
            }
        }
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
