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
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EffectPickup extends AbstractEffect {
    public static EffectPickup INSTANCE = new EffectPickup();

    private EffectPickup() {
        super(GlyphLib.EffectPickupID, "Item Pickup");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level casterWorld, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = BlockPos.containing(rayTraceResult.getLocation());
        double expansion = 2 + spellStats.getAoeMultiplier();
        Vec3 posVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());

        Level world = spellContext.level;
        List<ItemEntity> entityList = world.getEntitiesOfClass(ItemEntity.class, new AABB(
                posVec.add(expansion, expansion, expansion), posVec.subtract(expansion, expansion, expansion)));
        InventoryManager manager = spellContext.getCaster().getInvManager().extractSlotMax(-1);
        for (ItemEntity i : entityList) {
            i.setPickUpDelay(0); // Fixes backpack mods respecting pickup delay
            var pickupPre = NeoForge.EVENT_BUS.post(new ItemEntityPickupEvent.Pre(getPlayer(shooter, (ServerLevel) world), i));
            ItemStack stack = i.getItem();
            if (stack.isEmpty() || pickupPre.canPickup().isFalse())
                continue;
            stack = manager.insertStack(stack);
            i.setItem(stack);
            NeoForge.EVENT_BUS.post(new ItemEntityPickupEvent.Post(getPlayer(shooter, (ServerLevel) casterWorld), i, stack));
        }
        List<ExperienceOrb> orbList = world.getEntitiesOfClass(ExperienceOrb.class, new AABB(
                posVec.add(expansion, expansion, expansion), posVec.subtract(expansion, expansion, expansion)));
        for (ExperienceOrb i : orbList) {
            if (shooter instanceof Player player && isNotFakePlayer(player) && spellContext.castingTile == null) {
                var expPickup = NeoForge.EVENT_BUS.post(new net.neoforged.neoforge.event.entity.player.PlayerXpEvent.PickupXp(player, i));
                if (expPickup.isCanceled())
                    continue;
                player.giveExperiencePoints(i.value);
                i.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

   @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAOE.INSTANCE);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentAOE.INSTANCE, "Increases the radius of the pickup effect.");
    }

    @Override
    public String getBookDescription() {
        return "Picks up nearby items in a medium radius where this spell is activated. The range may be expanded with AOE.";
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
