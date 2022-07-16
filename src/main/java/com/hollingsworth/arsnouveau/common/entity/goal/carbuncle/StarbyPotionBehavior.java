package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class StarbyPotionBehavior extends StarbyListBehavior {
    public StarbyPotionBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, storedEntity, playerEntity);
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
    }

    public boolean isPositionValidTake(BlockPos p) {
        if (p == null)
            return false;
        if(level.getBlockEntity(p) instanceof PotionJarTile jar){

        }
        return false;
    }

    public boolean isPositionValidStore(BlockPos p) {
        if (p == null || level.getBlockEntity(p) == null)
            return false;

        return false;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        return super.toTag(tag);
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsNouveau.MODID, "starby_potion");
    }
}
