package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class StarbyPotionBehavior extends StarbyListBehavior {
    public @Nullable PotionData heldPotion = null;

    public StarbyPotionBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, storedEntity, playerEntity);
        if(storedPos != null && level.getBlockEntity(storedPos) instanceof PotionJarTile){
            this.TO_LIST.add(storedPos.immutable());
            PortUtil.sendMessage(storedEntity, Component.translatable("ars_nouveau.starbuncle.potion_to"));
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, storedEntity, playerEntity);
        if(storedPos != null && level.getBlockEntity(storedPos) instanceof PotionJarTile){
            this.FROM_LIST.add(storedPos.immutable());
            PortUtil.sendMessage(storedEntity, Component.translatable("ars_nouveau.starbuncle.potion_from"));
        }
    }

    public boolean isPositionValidTake(BlockPos p) {
        if (p == null)
            return false;
        if(level.getBlockEntity(p) instanceof PotionJarTile jar){
//            return TO_LIST.stream().anyMatch(pos -> level.getBlockEntity(pos) instanceof PotionJarTile jar2 && jar2.isMixEqual(jar));
        }
        return false;
    }

    public boolean isPositionValidStore(BlockPos p) {
        if (p == null)
            return false;
        return level.getBlockEntity(p) instanceof PotionJarTile jar && canJarAcceptHeld(jar);
    }

    public boolean canJarAcceptHeld(PotionJarTile tile){
        if(heldPotion == null)
            return false;
        return false;
//        return tile.getPotion() == Potions.EMPTY || (tile.isMixEqual(heldPotion.instance) && tile.getAmount() < tile.getMaxFill());
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
