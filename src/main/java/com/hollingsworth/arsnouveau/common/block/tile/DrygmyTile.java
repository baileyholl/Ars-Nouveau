package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityDrygmy;
import com.hollingsworth.arsnouveau.common.entity.EntityFollowProjectile;
import com.hollingsworth.arsnouveau.common.mixin.ExpInvokerMixin;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.Config;
import com.hollingsworth.arsnouveau.setup.EntityTags;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class DrygmyTile extends SummoningTile implements ITooltipProvider {


    public int progress;
    public int bonus;
    public boolean needsMana;
    private List<LivingEntity> nearbyEntities;

    public DrygmyTile() {
        super(BlockRegistry.DRYGMY_TILE);
    }

    @Override
    public void tick() {
        super.tick();

        if(level.isClientSide){
            for(int i = 0; i < progress/2; i++){
                level.addParticle(
                        GlowParticleData.createData(new ParticleColor(
                                50,
                                255,
                                20
                        )),
                        getBlockPos().getX() +0.5 + ParticleUtil.inRange(-0.1, 0.1)  , getBlockPos().getY() + 1  + ParticleUtil.inRange(-0.1, 0.1) , getBlockPos().getZ() +0.5 + ParticleUtil.inRange(-0.1, 0.1),
                        0,0,0);
            }

        }


        if(!level.isClientSide && level.getGameTime() % 100 == 0){
            refreshEntitiesAndBonus();
        }

        if(!level.isClientSide && level.getGameTime() % 80 == 0 && needsMana && ManaUtil.takeManaNearbyWithParticles(worldPosition, level, 7, Config.DRYGMY_MANA_COST.get()) != null){
            this.needsMana = false;
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
        }

        if(!level.isClientSide && level.getGameTime() % 100 == 0 && !needsMana && progress >= getMaxProgress() && !getNearbyEntities().isEmpty()){
            generateItems();
        }
    }

    public List<LivingEntity> getNearbyEntities(){
        if (nearbyEntities == null)
            this.refreshEntitiesAndBonus();

        return nearbyEntities;
    }

    public @Nullable LivingEntity getRandomEntity(){
        if(getNearbyEntities().isEmpty())
            return null;
        return getNearbyEntities().get(new Random().nextInt(getNearbyEntities().size()));
    }

    public void giveProgress(){
        if(progress < getMaxProgress()){
            progress += 1;
            level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
        }

    }

    public int getMaxProgress(){
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
            ParticleUtil.spawnPoof((ServerWorld) level, worldPosition.above());
            tickCounter = 0;
            return;
        }
        if (tickCounter % 10 == 0 && !level.isClientSide) {
            Random r = level.random;
            int min = -2;
            int max = 2;
            EntityFollowProjectile proj1 = new EntityFollowProjectile(level, worldPosition.offset(r.nextInt(max - min) + min, 3, r.nextInt(max - min) + min), worldPosition, r.nextInt(255), r.nextInt(255), r.nextInt(255));
            level.addFreshEntity(proj1);
        }
    }

    public void refreshEntitiesAndBonus(){
        Set<ResourceLocation> uniqueEntities;
        this.nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, new AxisAlignedBB(getBlockPos().north(10).west(10).below(6), getBlockPos().south(10).east(10).above(6)));
        this.nearbyEntities = this.nearbyEntities.stream().filter(l -> !(l instanceof EntityDrygmy) && !(l instanceof PlayerEntity)).collect(Collectors.toList());
        uniqueEntities = nearbyEntities.stream().map(l -> EntityType.getKey(l.getType())).collect(Collectors.toSet());
        this.bonus = uniqueEntities.size() * Config.DRYGMY_UNIQUE_BONUS.get() + Math.min(Config.DRYGMY_QUANTITY_CAP.get(), nearbyEntities.size());
    }

    public void generateItems(){
        List<ItemStack> stacks = new ArrayList<>();
        ANFakePlayer fakePlayer = ANFakePlayer.getPlayer((ServerWorld) level);
        DamageSource damageSource = DamageSource.playerAttack(fakePlayer);
        int numberItems = Config.DRYGMY_BASE_ITEM.get() + this.bonus;
        int exp = 0;
        // Create the loot table and exp count
        for(LivingEntity entity : getNearbyEntities()){
            if(entity.getType().is(EntityTags.DRYGMY_BLACKLIST)) {
                continue;
            }

            LootTable loottable = this.level.getServer().getLootTables().get(entity.getLootTable());
            LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.level)).withRandom(level.getRandom())
                    .withParameter(LootParameters.THIS_ENTITY, entity).withParameter(LootParameters.ORIGIN, entity.position())
                    .withParameter(LootParameters.DAMAGE_SOURCE, damageSource)
                    .withOptionalParameter(LootParameters.KILLER_ENTITY, fakePlayer.getEntity())
                    .withOptionalParameter(LootParameters.DIRECT_KILLER_ENTITY, damageSource.getDirectEntity());
            lootcontext$builder = lootcontext$builder.withParameter(LootParameters.LAST_DAMAGE_PLAYER, fakePlayer)
                    .withLuck(fakePlayer.getLuck());

            LootContext ctx = lootcontext$builder.create(LootParameterSets.ENTITY);
            stacks.addAll(loottable.getRandomItems(ctx));
            int oldExp = 0;
            if(entity instanceof MobEntity){
                oldExp = ((MobEntity) entity).xpReward;
            }
            exp += ((ExpInvokerMixin) entity).an_getExperienceReward(fakePlayer);

            if(entity instanceof MobEntity){
                // EVERY TIME GET EXPERIENCE REWARD IS CALLED IN ZOMBIE ENTITY IT MULTIPLIES BY 2.5X.
                ((MobEntity) entity).xpReward = oldExp;
            }
        }
        // Pull our items randomly and break once our stack count is over our max item list
        int itemsPicked = 0;
        if(stacks.size() > 0) {
            for (int i = 0; i < numberItems; i++) {
                ItemStack stack = stacks.get(level.random.nextInt(stacks.size())).copy();
                itemsPicked += stack.getCount();
                BlockUtil.insertItemAdjacent(level, worldPosition, stack);
                if(itemsPicked >= numberItems)
                    break;
            }
        }

        exp *= .25;
        if(exp > 3){
            int numGreater = exp / 12;
            exp -= numGreater * 12;
            int numLesser = exp / 3;
            if ((exp - numLesser * 3) > 0)
                numLesser++;
            if(numGreater > 0)
                BlockUtil.insertItemAdjacent(level, worldPosition, new ItemStack(ItemsRegistry.GREATER_EXPERIENCE_GEM, numGreater));
            if(numLesser > 0)
                BlockUtil.insertItemAdjacent(level, worldPosition, new ItemStack(ItemsRegistry.EXPERIENCE_GEM, numLesser));
        }

        this.progress = 0;
        this.needsMana = true;
        level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
    }


    @Override
    public void load(BlockState state, CompoundNBT compound) {
        this.progress = compound.getInt("progress");
        this.bonus = compound.getInt("bonus");
        this.needsMana = compound.getBoolean("needsMana");
        super.load(state, compound);
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putInt("progress", progress);
        compound.putInt("bonus", bonus);
        compound.putBoolean("needsMana", needsMana);
        return super.save(compound);
    }

    @Override
    public List<String> getTooltip() {
        List<String> list = new ArrayList<>();
        if(this.needsMana){
            list.add(new TranslationTextComponent("ars_nouveau.wixie.need_mana").getString());
        }
        return list;
    }
}
