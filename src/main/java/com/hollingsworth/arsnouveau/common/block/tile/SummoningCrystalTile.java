package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.mana.AbstractManaTile;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleSparkleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ManaBlock;
import com.hollingsworth.arsnouveau.common.block.SummoningCrystal;
import com.hollingsworth.arsnouveau.common.entity.EntityWhelp;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.hollingsworth.arsnouveau.api.util.BlockUtil.isTreeBlock;

public class SummoningCrystalTile extends AbstractManaTile implements IAnimatable {

    ArrayList<UUID> entityList = new ArrayList<>();
    int numEntities = 0;

    int tier;
    int taskIndex;
    public boolean isOff;
    public SummoningCrystalTile() {
        super(BlockRegistry.SUMMONING_CRYSTAL_TILE);
        tier = 1;
    }
    AnimationFactory manager = new AnimationFactory(this);
    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "rotate_controller", 0, this::idlePredicate));
    }

    private <E extends TileEntity & IAnimatable > PlayState idlePredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("summoning_crystal", true));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    @Override
    public int getTransferRate() {
        return 0;
    }

    public void summon(LivingEntity entity){
        if(!level.isClientSide){
            numEntities +=1;
            entityList.add(entity.getUUID());
        }
    }

    public void changeTier(PlayerEntity entity){
        if(tier == 1){
            tier = 2;
            entity.sendMessage(new TranslationTextComponent("ars_nouveau.summoning_crystal.5x5"), Util.NIL_UUID);
        }else if(tier == 2){
            tier = 3;
            entity.sendMessage(new TranslationTextComponent("ars_nouveau.summoning_crystal.9x9"), Util.NIL_UUID);
        }else if(tier == 3){
            tier = 1;
            entity.sendMessage(new TranslationTextComponent("ars_nouveau.summoning_crystal.adjacent"), Util.NIL_UUID);
        }
    }


    public ItemStack insertItem(ItemStack stack){
       return BlockUtil.insertItemAdjacent(level, worldPosition, stack);
    }

    public ItemStack getItem(Item item){
        return BlockUtil.getItemAdjacent(level, worldPosition, stack -> stack.getItem() == item);
    }


    public @Nullable BlockPos getNextTaskLoc(@Nullable List<AbstractSpellPart> recipe, EntityWhelp caster){

        if(isOff)
            return null;

        List<BlockPos> posList = getTargets();
        if(posList == null || posList.isEmpty()) {
            return null;
        }
        if(taskIndex + 1 > posList.size()){
            taskIndex = 0;
        }

        BlockPos taskPos = posList.get(taskIndex);

        taskIndex += 1;
        // If the block above is not air
        if (level.getBlockState(taskPos.above()).getMaterial() != Material.AIR && !isTreeBlock(level.getBlockState(taskPos).getBlock())){
            for(int i = 1; i < 4; i++) {
                if (level.getBlockState(taskPos.above(i)).getMaterial() != Material.AIR || isTreeBlock(level.getBlockState(taskPos.above()).getBlock())){
                    taskPos = taskPos.above(i);
                    break;
                }
            }
        }

        Block block = level.getBlockState(taskPos).getBlock();
        if(block instanceof SummoningCrystal || block instanceof ContainerBlock || block instanceof ManaBlock || block instanceof IInventory)
            return null;


        if(recipe != null && caster.getEntityData().get(EntityWhelp.STRICT_MODE)){
            SpellResolver resolver = new SpellResolver(recipe, new SpellContext(recipe, caster));
            if(!resolver.wouldCastOnBlockSuccessfully(new BlockRayTraceResult(new Vector3d(taskPos.getX(), taskPos.getY(), taskPos.getZ()), Direction.UP,taskPos, false ), caster)) {
                return null;
            }
        }
        return taskPos;
    }



    public boolean enoughMana(List<AbstractSpellPart> spellParts){
        if(spellParts == null)
            return false;

        return enoughMana(new Spell(spellParts).getCastingCost() / 4);
    }

    public boolean enoughMana(int manaCost){
        return ManaUtil.hasManaNearby(worldPosition, level, 7, manaCost);
    }

    public boolean removeManaAround(int manaCost){
        return ManaUtil.takeManaNearbyWithParticles(worldPosition, level, 7, manaCost) != null;
    }

    public boolean removeManaAround(List<AbstractSpellPart> spellParts){
        return removeManaAround(new Spell(spellParts).getCastingCost() / 4);
    }

    public List<BlockPos> getTargets(){
        List<BlockPos> positions = new ArrayList<>();
        if(tier == 1){
            positions.add(getBlockPos().north().below());
            positions.add(getBlockPos().south().below());
            positions.add(getBlockPos().east().below());
            positions.add(getBlockPos().west().below());
        }
        if(tier == 2){
            BlockPos.betweenClosedStream(getBlockPos().north(2).east(2).below(1), getBlockPos().south(2).west(2).below()).forEach(t -> positions.add(new BlockPos(t)));
        }
        if(tier == 3){
            BlockPos.betweenClosedStream(getBlockPos().north(4).east(4).below(1), getBlockPos().south(4).west(4).below()).forEach(t -> positions.add(new BlockPos(t)));
        }
        return positions;
    }

    public void cleanupKobolds(){
        List<UUID> list = level.getEntitiesOfClass(EntityWhelp.class, new AxisAlignedBB(worldPosition).inflate(10)).stream().map(f -> f.getUUID()).collect(Collectors.toList());
        ArrayList<UUID> removed = new ArrayList<>();
        for(UUID uuid : this.entityList) {
            if (!list.contains(uuid)) {
                removed.add(uuid);
            }
        }
        for(UUID uuid : removed){
            this.entityList.remove(uuid);
            this.numEntities--;
        }
    }

    @Override
    public void tick() {
        Random rand = level.getRandom();
        if(level.isClientSide && rand.nextInt(6) == 0){
            for(int i = 0; i < 10; i++){
                level.addParticle(ParticleSparkleData.createData(ParticleUtil.defaultParticleColor(), 0.05f, 60),
                        worldPosition.getX()  +ParticleUtil.inRange(-0.5, 0.5) +0.5 , worldPosition.getY() +ParticleUtil.inRange(-1, 1) , worldPosition.getZ() +ParticleUtil.inRange(-0.5, 0.5) +0.5,
                        ParticleUtil.inRange(-0.03, 0.03),  ParticleUtil.inRange(0.01, 0.5), ParticleUtil.inRange(-0.03, 0.03));
            }
        }
        if(level.getGameTime() % 20 != 0  || level.isClientSide)
            return;
        cleanupKobolds();
    }


    public final static String ENTITY_TAG = "entity";
    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state,tag);
        this.numEntities = tag.getInt("entities");
        int count = 0;
        while(tag.hasUUID(ENTITY_TAG + count)){
            entityList.add(tag.getUUID(ENTITY_TAG + count));
            count++;
        }
        taskIndex = tag.getInt("task_index");
        tier = tag.getInt("tier");
        isOff = tag.getBoolean("is_off");
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.putInt("entities", numEntities);
        for (int i = 0; i < entityList.size(); i++) {
            tag.putUUID(ENTITY_TAG + i, entityList.get(i));
        }
        tag.putInt("task_index", taskIndex);
        tag.putInt("tier", tier);
        tag.putBoolean("is_off", isOff);
        return super.save(tag);
    }
}
