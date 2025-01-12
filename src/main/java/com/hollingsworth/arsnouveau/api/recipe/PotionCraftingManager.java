package com.hollingsworth.arsnouveau.api.recipe;

import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.util.ANCodecs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PotionCraftingManager extends CraftingManager {
    private boolean hasObtainedPotion;
    private PotionContents potionNeeded;
    public PotionContents potionOut;

    public PotionCraftingManager(){
        super();
    }

    public PotionCraftingManager(PotionContents potionNeeded, List<ItemStack> itemsNeeded, PotionContents potionOut) {
        super(ItemStack.EMPTY, itemsNeeded);
        this.potionNeeded = potionNeeded;
        this.potionOut = potionOut;
        neededItems = itemsNeeded;
        remainingItems = itemsNeeded;
        outputStack = ItemStack.EMPTY;
        hasObtainedPotion = potionNeeded == PotionContents.EMPTY || potionNeeded.is(Potions.WATER);
    }

    @Override
    public boolean canBeCompleted() {
        return !needsPotion() && super.canBeCompleted();
    }

    public boolean needsPotion(){
        return !(hasObtainedPotion);
    }

    public PotionContents getPotionNeeded(){
        return potionNeeded;
    }

    public void setObtainedPotion(boolean hasObtainedPotion) {
        this.hasObtainedPotion = hasObtainedPotion;
    }

    @Override
    public void completeCraft(WixieCauldronTile tile) {
        Level level = tile.getLevel();
        BlockPos worldPosition = tile.getBlockPos();
        BlockPos jarPos = WixieCauldronTile.findPotionStorage(tile.getLevel(), tile.getBlockPos(), potionOut);
        if (jarPos == null) {
            if (!tile.needsPotionStorage()) {
                tile.setNeedsPotionStorage(true);
                level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
            }
            return;
        }else if (level instanceof ServerLevel serverLevel && level.getBlockEntity(jarPos) instanceof PotionJarTile jar) {
            tile.setNeedsPotionStorage(false);
            jar.add(potionOut,300);
            ParticleColor color2 = ParticleColor.fromInt(jar.getColor());
            EntityFlyingItem.spawn(serverLevel, new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ()+ 0.5),
                    new Vec3(jarPos.getX() + 0.5, jarPos.getY(), jarPos.getZ() + 0.5),
                    Math.round(255 * color2.getRed()), Math.round(255 * color2.getGreen()), Math.round(255 * color2.getBlue()))
                    .withNoTouch();
        }
        super.completeCraft(tile);
    }

    @Override
    public boolean isCraftInvalid() {
        return false;
    }

    @Override
    public void write(HolderLookup.Provider provider,  CompoundTag tag) {
        super.write(provider, tag);
        tag.put("potionout", ANCodecs.encode(provider, PotionContents.CODEC, potionOut));
        tag.put("potionNeeded", ANCodecs.encode(provider, PotionContents.CODEC, getPotionNeeded()));
        tag.putBoolean("gotPotion", hasObtainedPotion);
    }

    public void read(HolderLookup.Provider provider, CompoundTag tag){
        super.read(provider, tag);
        potionOut = ANCodecs.decode(provider, PotionContents.CODEC, tag.getCompound("potionout"));
        potionNeeded = ANCodecs.decode(provider, PotionContents.CODEC, tag.getCompound("potionNeeded"));
        hasObtainedPotion = tag.getBoolean("gotPotion");
    }
}
