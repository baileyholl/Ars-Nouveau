package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.ArsNouveauAPI;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityRitualProjectile;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RitualTile extends TileEntity implements ITickableTileEntity, ITooltipProvider, IAnimatable {
    public AbstractRitual ritual;
    AnimationFactory manager = new AnimationFactory(this);

    public RitualTile() {
        super(BlockRegistry.RITUAL_TILE);
    }

    @Override
    public void tick() {
        if(level.isClientSide){
            World world = getLevel();
            BlockPos pos = getBlockPos();
            Random rand = world.random;
            double xzOffset = 0.25;
//            if(level.random.nextInt(5) == 0){
//                world.addParticle(
//                        ParticleTypes.WHITE_ASH,
//                        pos.getX() +0.5 + ParticleUtil.inRange(-xzOffset/2, xzOffset/2)  , pos.getY() + 1 + ParticleUtil.inRange(-0.1, 0.2) , pos.getZ() +0.5 + ParticleUtil.inRange(-xzOffset/2, xzOffset/2),
//                        0, ParticleUtil.inRange(0.0, 0.05f),0);
//            }

            for(int i =0; i < 50; i++){
                world.addParticle(
                        GlowParticleData.createData(new ParticleColor(
                                rand.nextInt(122),
                                rand.nextInt(22),
                                rand.nextInt(22)
                        )),
                        pos.getX() +0.5 + ParticleUtil.inRange(-xzOffset/2, xzOffset/2)  , pos.getY() + 1 + ParticleUtil.inRange(-0.1, 0.2) , pos.getZ() +0.5 + ParticleUtil.inRange(-xzOffset/2, xzOffset/2),
                        0, ParticleUtil.inRange(0.0, 0.05f),0);
            }
            for(int i =0; i < 50; i++){
                world.addParticle(
                        GlowParticleData.createData(new ParticleColor(
                                rand.nextInt(122),
                                rand.nextInt(22),
                                rand.nextInt(22)
                        )),
                        pos.getX() +0.5 + ParticleUtil.inRange(-xzOffset, xzOffset)  , pos.getY() +1 + ParticleUtil.inRange(0, 0.7) , pos.getZ() +0.5 + ParticleUtil.inRange(-xzOffset, xzOffset),
                        0,ParticleUtil.inRange(0.0, 0.05f),0);;
            }

        }

        if(ritual != null){
            if(ritual.getContext().isDone){
                ritual.onEnd();
                ritual = null;
                return;
            }
            ritual.tryTick();
        }
    }

    public boolean isRitualRunning(){
        return ritual != null && !ritual.getContext().isDone;
    }

    public boolean canAffordCost(int currentExp){
        return ritual.getCost() <= currentExp;
    }

    public boolean canRitualStart(){
        return ritual.canStart();
    }


    public void startRitual(){
        if(ritual == null)
            return;
        ritual.onStart();
        EntityRitualProjectile ritualProjectile = new EntityRitualProjectile(level, worldPosition.getX(), worldPosition.getY() + 1.0, worldPosition.getZ());
        ritualProjectile.setPos(ritualProjectile.getX() +0.5, ritualProjectile.getY(), ritualProjectile.getZ() +0.5);
        ritualProjectile.tilePos = this.getBlockPos();
        level.addFreshEntity(ritualProjectile);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        String ritualID = tag.getString("ritualID");
        if(!ritualID.isEmpty()){
            ritual = ArsNouveauAPI.getInstance().getRitual(ritualID);
            if(ritual != null)
                ritual.read(tag);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        if(ritual != null){
            tag.putString("ritualID", ritual.getID());
            ritual.write(tag);
        }
        return super.save(tag);
    }

    public void setRitual(String selectedRitual) {
        this.ritual = ArsNouveauAPI.getInstance().getRitual(selectedRitual);
    }


    @Override
    public List<String> getTooltip() {
        List<String> tooltips = new ArrayList<>();
        if(ritual != null){
            tooltips.add(ritual.getName());
        }
        return tooltips;
    }

    public int getRitualCost() {
        return ritual.getCost();
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "idle", 0, this::idlePredicate));
    }

    private <P extends IAnimatable> PlayState idlePredicate(AnimationEvent<P> pAnimationEvent) {
        pAnimationEvent.getController().setAnimation(new AnimationBuilder().addAnimation("gem_float", true));
        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }
}
