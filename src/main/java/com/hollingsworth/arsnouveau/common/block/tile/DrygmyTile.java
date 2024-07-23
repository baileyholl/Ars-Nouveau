package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.lib.EntityTags;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class DrygmyTile extends SummoningTile implements ITooltipProvider, IWandable {
    public int progress;
    public int bonus;
    public boolean needsMana;
    private List<LivingEntity> nearbyEntities;
    public boolean includeEntities = true;

    public DrygmyTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.DRYGMY_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level.isClientSide) {
            for (int i = 0; i < progress / 2; i++) {
                level.addParticle(
                        GlowParticleData.createData(new ParticleColor(
                                50,
                                255,
                                20
                        )),
                        getBlockPos().getX() + 0.5 + ParticleUtil.inRange(-0.1, 0.1), getBlockPos().getY() + 1 + ParticleUtil.inRange(-0.1, 0.1), getBlockPos().getZ() + 0.5 + ParticleUtil.inRange(-0.1, 0.1),
                        0, 0, 0);
            }
        }else {
            if (level.getGameTime() % 100 == 0) {
                refreshEntitiesAndBonus();
            }

            if (level.getGameTime() % 80 == 0 && needsMana && SourceUtil.takeSourceWithParticles(worldPosition, level, 7, Config.DRYGMY_MANA_COST.get()) != null) {
                this.needsMana = false;
                updateBlock();
            }

            if (level.getGameTime() % 100 == 0 && !needsMana && progress >= getMaxProgress() && !getNearbyEntities().isEmpty()) {
                generateItems();
            }
        }
    }

    public List<LivingEntity> getNearbyEntities() {
        if (nearbyEntities == null)
            this.refreshEntitiesAndBonus();

        return nearbyEntities;
    }

    public @Nullable LivingEntity getRandomEntity() {
        if (getNearbyEntities().isEmpty())
            return null;
        return getNearbyEntities().get(new Random().nextInt(getNearbyEntities().size()));
    }

    public void giveProgress() {
        if (progress < getMaxProgress()) {
            progress += 1;
            updateBlock();
        }

    }

    public int getMaxProgress() {
        return Config.DRYGMY_MAX_PROGRESS.get();
    }

    public void convertedEffect() {
        super.convertedEffect();
        if (tickCounter >= 120 && !level.isClientSide) {
            converted = true;
            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).setValue(SummoningTile.CONVERTED, true));
            EntityDrygmy entityDrygmy = new EntityDrygmy(level, true);
            entityDrygmy.setPos(worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5);
            entityDrygmy.homePos = new BlockPos(getBlockPos());
            level.addFreshEntity(entityDrygmy);
            ParticleUtil.spawnPoof((ServerLevel) level, worldPosition.above());
            tickCounter = 0;
            return;
        }
        if (tickCounter % 10 == 0 && !level.isClientSide) {
            RandomSource r = level.random;
            int min = -2;
            int max = 2;
            EntityFollowProjectile proj1 = new EntityFollowProjectile(level, worldPosition.offset(r.nextInt(max - min) + min, 3, r.nextInt(max - min) + min), worldPosition, r.nextInt(255), r.nextInt(255), r.nextInt(255));
            level.addFreshEntity(proj1);
        }
    }

    public void refreshEntitiesAndBonus() {
        Set<ResourceLocation> uniqueEntities;
        this.nearbyEntities = new ArrayList<>();
        if (this.includeEntities) {
            this.nearbyEntities.addAll(level.getEntitiesOfClass(LivingEntity.class, new AABB(getBlockPos().north(10).west(10).below(6), getBlockPos().south(10).east(10).above(6))));
        }
        this.nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, new AABB(getBlockPos().north(10).west(10).below(6).getBottomCenter(), getBlockPos().south(10).east(10).above(6).getBottomCenter()));
        for(BlockPos b : BlockPos.withinManhattan(getBlockPos(), 10, 10, 10)){
            if(level.getBlockEntity(b) instanceof MobJarTile mobJarTile && mobJarTile.getEntity() instanceof LivingEntity livingEntity){
                nearbyEntities.add(livingEntity);
            }
        }

        this.nearbyEntities = this.nearbyEntities.stream().filter(l -> !(l instanceof EntityDrygmy) && !(l instanceof Player)).collect(Collectors.toList());
        uniqueEntities = nearbyEntities.stream().map(l -> EntityType.getKey(l.getType())).collect(Collectors.toSet());
        this.bonus = uniqueEntities.size() * Config.DRYGMY_UNIQUE_BONUS.get() + Math.min(Config.DRYGMY_QUANTITY_CAP.get(), nearbyEntities.size());
        updateBlock();
    }

    public void generateItems() {
        List<ItemStack> stacks = new ArrayList<>();
        ANFakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerLevel) level);
        DamageSource damageSource = level.damageSources().playerAttack(fakePlayer);
        int numberItems = Config.DRYGMY_BASE_ITEM.get() + this.bonus;
        int exp = 0;
        if(!(this.level instanceof ServerLevel serverLevel)){
            return;
        }
        // Create the loot table and exp count
        for (LivingEntity entity : getNearbyEntities()) {
            if (entity.getType().is(EntityTags.DRYGMY_BLACKLIST)) {
                continue;
            }

            var key = entity.getLootTable();
            LootTable loottable = serverLevel.getServer().reloadableRegistries().getLootTable(key);

            LootParams.Builder lootcontext$builder = (new LootParams.Builder((ServerLevel) this.level))
                    .withParameter(LootContextParams.THIS_ENTITY, entity).withParameter(LootContextParams.ORIGIN, entity.position())
                    .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                    .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, fakePlayer)
                    .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, damageSource.getDirectEntity());
            lootcontext$builder = lootcontext$builder.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, fakePlayer)
                    .withLuck(fakePlayer.getLuck());

            stacks.addAll(loottable.getRandomItems(lootcontext$builder.create(LootContextParamSets.ENTITY)));
            int oldExp = 0;
            if (entity instanceof Mob mob) {
                oldExp = mob.xpReward;
            }
            exp += entity.getExperienceReward((ServerLevel) level, fakePlayer);

            if (entity instanceof Mob mob) {
                // EVERY TIME GET EXPERIENCE REWARD IS CALLED IN ZOMBIE ENTITY IT MULTIPLIES BY 2.5X.
                mob.xpReward = oldExp;
            }
        }
        // Pull our items randomly and break once our stack count is over our max item list
        int itemsPicked = 0;
        if (!stacks.isEmpty()) {
            for (int i = 0; i < numberItems; i++) {
                ItemStack stack = stacks.get(level.random.nextInt(stacks.size())).copy();
                itemsPicked += stack.getCount();
                BlockUtil.insertItemAdjacent(level, worldPosition, stack);
                if (itemsPicked >= numberItems)
                    break;
            }
        }

        exp *= .25;
        if (exp > 3) {
            int numGreater = exp / 12;
            exp -= numGreater * 12;
            int numLesser = exp / 3;
            if ((exp - numLesser * 3) > 0)
                numLesser++;
            if (numGreater > 0)
                BlockUtil.insertItemAdjacent(level, worldPosition, new ItemStack(ItemsRegistry.GREATER_EXPERIENCE_GEM.get(), numGreater));
            if (numLesser > 0)
                BlockUtil.insertItemAdjacent(level, worldPosition, new ItemStack(ItemsRegistry.EXPERIENCE_GEM.get(), numLesser));
        }

        this.progress = 0;
        this.needsMana = true;
        updateBlock();
    }

    @Override
    public void onWanded(Player playerEntity) {
        includeEntities = !includeEntities;
        updateBlock();
    }

    @Override
    protected void loadAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.loadAdditional(compound, pRegistries);
        this.progress = compound.getInt("progress");
        this.bonus = compound.getInt("bonus");
        this.needsMana = compound.getBoolean("needsMana");
        this.includeEntities = !compound.contains("includeEntities") || compound.getBoolean("includeEntities");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.putInt("progress", progress);
        tag.putInt("bonus", bonus);
        tag.putBoolean("needsMana", needsMana);
        tag.putBoolean("includeEntities", includeEntities);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (isOff) {
            tooltip.add(Component.translatable("ars_nouveau.tooltip.turned_off"));
        }
        if (this.needsMana) {
            tooltip.add(Component.translatable("ars_nouveau.wixie.need_mana"));
        }
        if (!this.includeEntities) {
            tooltip.add(Component.translatable("ars_nouveau.drygmy.only_use_jars"));
        }
    }
}
