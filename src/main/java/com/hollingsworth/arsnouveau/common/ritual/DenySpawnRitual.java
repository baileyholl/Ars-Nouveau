package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.RangeRitual;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.entity.living.MobSpawnEvent;
import org.jetbrains.annotations.Nullable;

public class DenySpawnRitual extends RangeRitual {
    public int radius = 32;
    public boolean deniedSpawn;

    public boolean denySpawn(MobSpawnEvent.FinalizeSpawn checkSpawn){
        boolean shouldDeny = checkSpawn.getSpawnType() == MobSpawnType.NATURAL
                && checkSpawn.getEntity() instanceof Enemy
                && checkSpawn.getEntity().distanceToSqr(getPos().getX(), getPos().getY(), getPos().getZ()) <= radius * radius;
        if(shouldDeny){
            checkSpawn.setSpawnCancelled(true);
            deniedSpawn = true;
        }
        return shouldDeny;
    }

    @Override
    public void onStart(@Nullable Player player) {
        super.onStart(player);
        if(getWorld().isClientSide){
            return;
        }
        for(ItemStack i : getConsumedItems()){
            if(i.is(Items.ROTTEN_FLESH)) {
                radius += i.getCount();
            }
        }
    }

    @Override
    protected void tick() {
        super.tick();
        if(getWorld().isClientSide){
            return;
        }
        if(deniedSpawn && getWorld().getGameTime() % 1200 == 0){
            deniedSpawn = false;
            takeSourceNow();
        }
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return stack.is(Items.ROTTEN_FLESH) && itemConsumedCount(i -> i.getItem() == Items.ROTTEN_FLESH) < 128;
    }

    @Override
    public int getSourceCost() {
        return 500;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( RitualLib.SANCTUARY);
    }

    @Override
    public String getLangName() {
        return "Sanctuary";
    }

    @Override
    public String getLangDescription() {
        return "Denies hostile mobs from naturally spawning in a 32 block radius. Augment with rotten flesh to increase the radius by 1 each, up to 128. Costs source once a minute if a spawn is denied.";
    }

    @Override
    public void read(CompoundTag tag) {
        super.read(tag);
        radius = tag.getInt("radius");
        deniedSpawn = tag.getBoolean("deniedSpawn");
    }

    @Override
    public void write(CompoundTag tag) {
        super.write(tag);
        tag.putInt("radius", radius);
        tag.putBoolean("deniedSpawn", deniedSpawn);
    }
}
