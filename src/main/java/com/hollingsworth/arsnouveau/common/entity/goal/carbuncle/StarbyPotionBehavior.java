package com.hollingsworth.arsnouveau.common.entity.goal.carbuncle;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.entity.Starbuncle;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StarbyPotionBehavior extends StarbyListBehavior {
    public static final ResourceLocation POTION_ID = ArsNouveau.prefix( "starby_potion");

    private @Nullable PotionData heldPotion = null;
    private int amount;

    public StarbyPotionBehavior(Starbuncle entity, CompoundTag tag) {
        super(entity, tag);
        heldPotion = PotionData.fromTag(tag.getCompound("potionData"));
        amount = tag.getInt("amount");
        goals.add(new WrappedGoal(4, new GoToBedGoal(starbuncle, this)));
        goals.add(new WrappedGoal(3, new PotionTakeGoal(entity, this)));
        goals.add(new WrappedGoal(3, new PotionStoreGoal(entity, this)));
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionFirst(storedPos, face, storedEntity, playerEntity);
        if (storedPos != null && level.getBlockEntity(storedPos) instanceof PotionJarTile) {
            addToPos(storedPos);
            syncTag();
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.potion_to"));
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable Direction face, @Nullable LivingEntity storedEntity, Player playerEntity) {
        super.onFinishedConnectionLast(storedPos, face, storedEntity, playerEntity);
        if (storedPos != null && level.getBlockEntity(storedPos) instanceof PotionJarTile) {
            addFromPos(storedPos);
            syncTag();
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.starbuncle.potion_from"));
        }
    }

    public @Nullable BlockPos getJarForTake() {
        for (BlockPos pos : FROM_LIST) {
            if (isPositionValidTake(pos)) {
                return pos;
            }
        }
        return null;
    }

    public boolean isPositionValidTake(BlockPos p) {
        if (p == null)
            return false;
        if (level.getBlockEntity(p) instanceof PotionJarTile jar) {
            // Check if we can store the potion we take from this jar
            return jar.getAmount() >= 100 && getJarForStorage(jar.getData()) != null;
        }
        return false;
    }

    public @Nullable BlockPos getJarForStorage(PotionData data) {
        for (BlockPos pos : TO_LIST) {
            if (level.getBlockEntity(pos) instanceof PotionJarTile && isPositionValidStore(pos, data)) {
                return pos;
            }
        }
        return null;
    }

    public boolean isPositionValidStore(BlockPos p, PotionData data) {
        if (p == null || data == null)
            return false;
        return level.getBlockEntity(p) instanceof PotionJarTile jar && jar.canAccept(data, 100);
    }

    public PotionData getHeldPotion() {
        return heldPotion == null ? new PotionData() : heldPotion;
    }

    public void setHeldPotion(PotionData data) {
        heldPotion = data;
        syncTag();
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return this.amount;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        super.getTooltip(tooltip);
        tooltip.add(Component.translatable("ars_nouveau.starbuncle.storing_potions", TO_LIST.size()));
        tooltip.add(Component.translatable("ars_nouveau.starbuncle.taking_potions", FROM_LIST.size()));
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if (heldPotion != null) {
            tag.put("potionData", heldPotion.toTag());
        }
        tag.putInt("amount", amount);
        return super.toTag(tag);
    }

    @Override
    public ItemStack getStackForRender() {
        if (heldPotion != null && heldPotion.getPotion() != PotionContents.EMPTY) {
            return heldPotion.asPotionStack();
        }
        return super.getStackForRender();
    }

    @Override
    protected ResourceLocation getRegistryName() {
        return POTION_ID;
    }
}
