package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.entity.ChangeableBehavior;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;

public class StarbyBehavior extends ChangeableBehavior {
    public Starbuncle starbuncle;

    public StarbyBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        this.starbuncle = entity;
        goals.add(new WrappedGoal(8, new LookAtPlayerGoal(starbuncle, Player.class, 3.0F, 0.01F)));
        goals.add(new WrappedGoal(8, new NonHoggingLook(starbuncle, Mob.class, 3.0F, 0.01f)));
        goals.add(new WrappedGoal(1, new OpenDoorGoal(starbuncle, true)));
    }

    public boolean canGoToBed() {
        return true;
    }

    public boolean isBedPowered() {
        if (starbuncle.data.bedPos == null || !starbuncle.level.isLoaded(starbuncle.data.bedPos)) {
            return false;
        }
        BlockState state = starbuncle.level.getBlockState(starbuncle.data.bedPos);
        if (!state.is(BlockTagProvider.SUMMON_SLEEPABLE)) {
            return false;
        }
        return state.hasProperty(BlockStateProperties.POWERED) && state.getValue(BlockStateProperties.POWERED);
    }

    public @Nullable BlockPos getBedPos() {
        if (starbuncle.data.bedPos == null || !starbuncle.level.isLoaded(starbuncle.data.bedPos)) {
            return null;
        }
        return starbuncle.data.bedPos;
    }

    public boolean isBedValid(BlockPos bedPos) {
        return starbuncle.level.isLoaded(bedPos) && starbuncle.level.getBlockState(new BlockPos(bedPos)).is(BlockTagProvider.SUMMON_SLEEPABLE);
    }

    public boolean isOnBed() {
        return starbuncle.level.getBlockState(BlockPos.containing(starbuncle.position)).is(BlockTagProvider.SUMMON_SLEEPABLE);
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction side, @Nullable LivingEntity storedEntity, Player playerEntity, boolean linkRemoval) {
        super.onFinishedConnectionFirst(storedPos, side, storedEntity, playerEntity, linkRemoval);

        if (storedPos != null && playerEntity.level.getBlockState(storedPos).is(BlockTagProvider.SUMMON_SLEEPABLE)) {
            if (linkRemoval) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.remove_bed"));
                starbuncle.data.bedPos = null;
            } else {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.set_bed"));
                starbuncle.data.bedPos = storedPos.immutable();
            }
        }
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix("starby");
    }

    public void syncTag() {
        starbuncle.syncBehavior();
    }

    @Override
    public ItemStack getStackForRender() {
        return starbuncle.getHeldStack();
    }
}
