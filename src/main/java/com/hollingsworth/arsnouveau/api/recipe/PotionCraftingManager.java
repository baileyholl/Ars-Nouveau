package com.hollingsworth.arsnouveau.api.recipe;

import com.hollingsworth.arsnouveau.api.potion.PotionData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.tile.PotionJarTile;
import com.hollingsworth.arsnouveau.common.block.tile.WixieCauldronTile;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
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
        }else if (level.getBlockEntity(jarPos) instanceof PotionJarTile jar) {
            tile.setNeedsPotionStorage(false);
            jar.add(new PotionData(potionOut),300);
            ParticleColor color2 = ParticleColor.fromInt(jar.getColor());
            EntityFlyingItem flying = new EntityFlyingItem(level, new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ()+ 0.5),
                    new Vec3(jarPos.getX() + 0.5, jarPos.getY(), jarPos.getZ() + 0.5),
                    Math.round(255 * color2.getRed()), Math.round(255 * color2.getGreen()), Math.round(255 * color2.getBlue()))
                    .withNoTouch();
            level.addFreshEntity(flying);
        }
        super.completeCraft(tile);
    }


    @Override
    public void write(CompoundTag tag) {
        super.write(tag);
        CompoundTag outputTag = new CompoundTag();
        PotionUtil.addPotionToTag(potionOut, outputTag);
        tag.put("potionout", outputTag);

        CompoundTag neededTag = new CompoundTag();
        PotionUtil.addPotionToTag(getPotionNeeded(), neededTag);
        tag.put("potionNeeded", neededTag);
        tag.putBoolean("gotPotion", hasObtainedPotion);
    }

    public void read(CompoundTag tag){
        super.read(tag);
        potionOut = PotionUtils.getPotion(tag.getCompound("potionout"));
        potionNeeded = PotionUtils.getPotion(tag.getCompound("potionNeeded"));
        hasObtainedPotion = tag.getBoolean("gotPotion");
    }
}
