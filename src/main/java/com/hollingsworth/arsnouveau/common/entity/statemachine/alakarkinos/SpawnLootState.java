package com.hollingsworth.arsnouveau.common.entity.statemachine.alakarkinos;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.entity.Alakarkinos;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

public class SpawnLootState extends CrabState{
    boolean backToHat;
    int waitTicks;
    public SpawnLootState(Alakarkinos alakarkinos) {
        super(alakarkinos);
    }

    @Override
    public @Nullable CrabState tick() {
        var hatPos = alakarkinos.hatPos;
        if(hatPos == null){
            return new DecideCrabActionState(alakarkinos);
        }
        if(waitTicks > 0){
            waitTicks--;
            return null;
        }
        alakarkinos.getLookControl().setLookAt(hatPos.getX() + 0.5, hatPos.getY() + 0.5, hatPos.getZ() + 0.5);
        if (BlockUtil.distanceFrom(alakarkinos.blockPosition(), hatPos) > 2) {
            alakarkinos.getNavigation().moveTo(hatPos.getX() + 0.5, hatPos.getY() + 0.5, hatPos.getZ() + 0.5, 1.0);
            return null;
        }
        if (!backToHat) {
            backToHat = true;
            waitTicks = 60;
            alakarkinos.setBlowingBubbles(true);
            alakarkinos.getNavigation().stop();
            return null;
        }
        alakarkinos.setBlowingBubbles(false);
        alakarkinos.findBlockCooldown = 60;
        ItemStack loot = getLoot();
        return new DecideCrabActionState(alakarkinos);
    }


    public ItemStack getLoot() {
        var level = this.alakarkinos.level;
        LootTable loottable = alakarkinos.level.getServer().reloadableRegistries().getLootTable(BuiltInLootTables.DESERT_PYRAMID_ARCHAEOLOGY);
        var player = ANFakePlayer.getPlayer((ServerLevel) level);
        LootParams lootparams = new LootParams.Builder((ServerLevel) level)
                .withParameter(LootContextParams.ORIGIN, alakarkinos.position())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.CHEST);
        ObjectArrayList<ItemStack> objectarraylist = loottable.getRandomItems(lootparams, alakarkinos.level.random);
        return objectarraylist.isEmpty() ? ItemStack.EMPTY : objectarraylist.getFirst();
    }
}
