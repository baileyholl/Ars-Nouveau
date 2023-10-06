package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ritual.RangeRitual;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;

public class DenySpawnRitual extends RangeRitual {
    public int radius = 32;
    public boolean deniedSpawn;

    public boolean denySpawn(LivingSpawnEvent.CheckSpawn checkSpawn){
        boolean shouldDeny = checkSpawn.getSpawnReason() == MobSpawnType.NATURAL
                && !checkSpawn.isSpawner()
                && checkSpawn.getEntity() instanceof Enemy
                && checkSpawn.getEntity().distanceToSqr(getPos().getX(), getPos().getY(), getPos().getZ()) <= radius * radius;
        if(shouldDeny){
            checkSpawn.setResult(LivingSpawnEvent.CheckSpawn.Result.DENY);
            deniedSpawn = true;
        }
        return shouldDeny;
    }

    @Override
    public void onStart() {
        super.onStart();
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
        if(deniedSpawn && getWorld().getGameTime() % 300 == 0){
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
        return new ResourceLocation(ArsNouveau.MODID, RitualLib.SANCTUARY);
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
