package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SpellUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleLineData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.setup.registry.ModEntities;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WritableBookItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.FakePlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;

public class RitualAwakening extends AbstractRitual {

    EntityType<? extends LivingEntity> entity = null;
    BlockPos foundPos;
    UUID uuid;

    @Override
    public void onStart(@Nullable Player player) {
        if (player != null) uuid = player.getUUID();
    }

    public void destroyTree(ServerLevel world, Set<BlockPos> set) {
        FakePlayer fakePlayer = ANFakePlayer.getPlayer(world, uuid);
        for (BlockPos p : set) {
            BlockUtil.destroyBlockSafelyWithoutSound(world, p, false, fakePlayer);
        }
    }

    public void findTargets(ServerLevel world) {
        for (BlockPos p : BlockPos.withinManhattan(getPos(), 3, 1, 3)) {
            Set<BlockPos> blazing = SpellUtil.DFSBlockstates(world, p, 350, (b) -> b.getBlock() == BlockRegistry.BLAZING_LOG.get() || b.getBlock() == BlockRegistry.BLAZING_LEAVES.get());
            if (blazing.size() >= 50) {
                entity = ModEntities.ENTITY_BLAZING_WEALD.get();
                foundPos = p;
                destroyTree(world, blazing);
                return;
            }
            Set<BlockPos> flourishing = SpellUtil.DFSBlockstates(world, p, 350, (b) -> b.getBlock() == BlockRegistry.FLOURISHING_LOG.get() || b.getBlock() == BlockRegistry.FLOURISHING_LEAVES.get());
            if (flourishing.size() >= 50) {
                entity = ModEntities.ENTITY_FLOURISHING_WEALD.get();
                foundPos = p;
                destroyTree(world, flourishing);
                return;
            }
            Set<BlockPos> vexing = SpellUtil.DFSBlockstates(world, p, 350, (b) -> b.getBlock() == BlockRegistry.VEXING_LOG.get() || b.getBlock() == BlockRegistry.VEXING_LEAVES.get());
            if (vexing.size() >= 50) {
                entity = ModEntities.ENTITY_VEXING_WEALD.get();
                foundPos = p;
                destroyTree(world, vexing);
                return;
            }

            Set<BlockPos> cascading = SpellUtil.DFSBlockstates(world, p, 350, (b) -> b.getBlock() == BlockRegistry.CASCADING_LOG.get() || b.getBlock() == BlockRegistry.CASCADING_LEAVE.get());
            if (cascading.size() >= 50) {
                entity = ModEntities.ENTITY_CASCADING_WEALD.get();
                foundPos = p;
                destroyTree(world, cascading);
                return;
            }
            if (world.getBlockState(p).getBlock() == Blocks.BUDDING_AMETHYST) {
                world.setBlock(p, Blocks.AIR.defaultBlockState(), 3);
                entity = ModEntities.AMETHYST_GOLEM.get();
                foundPos = p;
                return;
            }

        }
    }

    @Override
    protected void tick() {
        Level world = getWorld();
        if (world.isClientSide) {
            BlockPos pos = getPos();

            for (int i = 0; i < 10; i++) {
                Vec3 particlePos = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(0.5, 0, 0.5);
                particlePos = particlePos.add(ParticleUtil.pointInSphere().multiply(5, 5, 5));
                world.addParticle(ParticleLineData.createData(getCenterColor()),
                        particlePos.x(), particlePos.y(), particlePos.z(),
                        pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            }
        }
        if (world instanceof ServerLevel serverLevel && serverLevel.getGameTime() % 20 == 0) {
            if(isBookwyrms()){
                int progress = getProgress();
                int numBookwyrms = getConsumedItems().stream().filter(i -> i.getItem() instanceof WritableBookItem).mapToInt(ItemStack::getCount).sum();
                if(progress < numBookwyrms){
                    ItemStack charm = new ItemStack(ItemsRegistry.BOOKWYRM_CHARM);
                    ItemEntity itemEntity = new ItemEntity(serverLevel, getPos().getX() + 0.5, getPos().getY() + 1, getPos().getZ() + 0.5, charm);
                    float range = 0.1f;
                    itemEntity.setDeltaMovement(ParticleUtil.inRange(-range, range),  ParticleUtil.inRange(0.4, 0.6), ParticleUtil.inRange(-range, range));
                    serverLevel.playSound(null, getPos(), SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 1.0F);
                    serverLevel.addFreshEntity(itemEntity);
                }else{
                    setFinished();
                }
            }else {
                if (getProgress() > 5) {
                    findTargets(serverLevel);
                    if (entity != null) {
                        ParticleUtil.spawnPoof(serverLevel, foundPos);
                        LivingEntity walker = entity.create(serverLevel);
                        walker.setPos(foundPos.getX() + 0.5, foundPos.getY(), foundPos.getZ() + 0.5);
                        serverLevel.addFreshEntity(walker);
                        setFinished();
                    }
                }
            }
            incrementProgress();
        }
    }

    public boolean isBookwyrms(){
        return getConsumedItems().stream().anyMatch(i -> i.getItem() instanceof WritableBookItem);
    }

    @Override
    public boolean canConsumeItem(ItemStack stack) {
        return super.canConsumeItem(stack) || stack.getItem() instanceof WritableBookItem;
    }

    @Override
    public ParticleColor getCenterColor() {
        return new ParticleColor(50, 200, 50);
    }

    @Override
    public String getLangName() {
        return "Awakening";
    }

    @Override
    public String getLangDescription() {
        return "Awakens nearby Archwood trees into Weald Walkers and Budding Amethyst into Amethyst Golems. Weald Walkers can be given a position in the world to guard against hostile mobs. They will heal over time, and turn into Weald Waddlers if they die. To create a Weald Walker, perform this ritual near the base of an Archwood Tree. Augmenting with Book and Quills will create Bookwyrm Charms.";
    }

    @Override
    public ResourceLocation getRegistryName() {
        return ArsNouveau.prefix( RitualLib.AWAKENING);
    }
}
