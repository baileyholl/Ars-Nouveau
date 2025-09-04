package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.crafting.recipes.AlakarkinosRecipe;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SpawnLootState extends CrabState {
    boolean backToHat;
    int waitTicks;
    AlakarkinosRecipe recipe;

    public SpawnLootState(Alakarkinos alakarkinos, AlakarkinosRecipe recipe) {
        super(alakarkinos);
        this.recipe = recipe;
    }

    @Override
    public void onEnd() {
        super.onEnd();
        alakarkinos.lookAt = null;
    }

    @Override
    public @Nullable CrabState tick() {
        var hatPos = alakarkinos.hatPos;
        if (hatPos == null || alakarkinos.getHome() == null) {
            return new DecideCrabActionState(alakarkinos);
        }
        if (waitTicks > 0) {
            waitTicks--;
            return null;
        }
        alakarkinos.lookAt = Vec3.atCenterOf(hatPos);
        if (BlockUtil.distanceFrom(alakarkinos.blockPosition(), hatPos) > 2) {
            alakarkinos.getNavigation().moveTo(hatPos.getX() + 0.5, hatPos.getY() + 0.5, hatPos.getZ() + 0.5, 1.0);
            return null;
        }
        if (!backToHat) {
            backToHat = true;
            waitTicks = 60;
            alakarkinos.getEntityData().set(Alakarkinos.BLOWING_AT, Optional.of(hatPos));
            alakarkinos.setBlowingBubbles(true);
            alakarkinos.getNavigation().stop();
            return null;
        }
        alakarkinos.setBlowingBubbles(false);
        alakarkinos.findBlockCooldown = 60 * 20;
        List<ItemStack> loot = getLoot();
        if (!loot.isEmpty()) {
            IItemHandler handler = alakarkinos.level.getCapability(Capabilities.ItemHandler.BLOCK, alakarkinos.getHome(), null);

            for (ItemStack stack : loot) {
                if (stack.isEmpty()) {
                    continue;
                }

                EntityFlyingItem.spawn(alakarkinos.getHome(), (ServerLevel) alakarkinos.level, hatPos, alakarkinos.getHome().above())
                        .setStack(stack)
                        .getEntityData().set(EntityFlyingItem.IS_BUBBLE, true);
                ItemHandlerHelper.insertItemStacked(handler, stack, false);
            }
        }
        return new DecideCrabActionState(alakarkinos);
    }


    public List<ItemStack> getLoot() {
        var level = this.alakarkinos.level;
        LootTable loottable = alakarkinos.level.getServer().reloadableRegistries().getLootTable(this.recipe.table());
        var player = ANFakePlayer.getPlayer((ServerLevel) level);
        LootParams lootparams = new LootParams.Builder((ServerLevel) level)
                .withParameter(LootContextParams.ORIGIN, alakarkinos.position())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.CHEST);
        return loottable.getRandomItems(lootparams, alakarkinos.level.random);
    }
}
