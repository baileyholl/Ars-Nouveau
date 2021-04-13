package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PotionMelderTile extends TileEntity implements IAnimatable, ITickableTileEntity {
    int timeMixing;
    boolean isMixing;
    public PotionMelderTile() {
        super(BlockRegistry.POTION_MELDER_TYPE);
    }

    AnimationFactory manager = new AnimationFactory(this);

    @Override
    public void tick() {

        PotionJarTile tile1 = null;
        PotionJarTile tile2 = null;
        for(Direction d : Direction.values()){
            if(d == Direction.UP || d == Direction.DOWN)
                continue;
            if(tile1 != null && tile2 != null)
                break;
            TileEntity tileEntity = world.getTileEntity(pos.offset(d));
            if(tileEntity instanceof PotionJarTile && ((PotionJarTile) tileEntity).getAmount() > 0){
                if(tile1 == null)
                    tile1 = (PotionJarTile) tileEntity;
                else
                    tile2 = (PotionJarTile) tileEntity;

            }
        }
        if(tile1 == null || tile2 == null || tile1.getAmount() < 300 || tile2.getAmount() < 300) {
            isMixing = false;
            timeMixing = 0;
            return;
        }
        PotionJarTile combJar = null;
        if(world.getTileEntity(pos.down()) instanceof PotionJarTile)
            combJar = (PotionJarTile) world.getTileEntity(pos.down());

        if(combJar == null) {
            isMixing = false;
            timeMixing = 0;
            return;
        }
        List<EffectInstance> combined = getCombinedResult(tile1, tile2);
        if(!(combJar.isMixEqual(combined) && combJar.getMaxFill() - combJar.getCurrentFill() >= 100) && combJar.getAmount() != 0){
            isMixing = false;
            timeMixing = 0;
            return;
        }

        isMixing = true;
        timeMixing++;
        ParticleColor color1 = ParticleColor.fromInt(tile1.getColor());
        ParticleColor color2 = ParticleColor.fromInt(tile2.getColor());
        if(world.isRemote ) {
            //Burning jar
            if(timeMixing >= 80 && combJar.getPotion() != Potions.EMPTY) {
                for (int i = 0; i < 3; i++) {
                    double d0 = pos.getX() + 0.5 + ParticleUtil.inRange(-0.25, 0.25);
                    double d1 = pos.getY() + 1 + ParticleUtil.inRange(-0.1, 0.4);
                    double d2 = pos.getZ() + .5 + ParticleUtil.inRange(-0.25, 0.25);
                    world.addParticle(GlowParticleData.createData(
                            ParticleColor.fromInt(combJar.getColor())),
                            d0, d1, d2,
                            0,
                            0.01f,
                            0);
                }
            }
            int offset = 30;
           if(timeMixing >= 60) {
               world.addParticle(GlowParticleData.createData(color1),
                       (float) (pos.getX()) + 0.5 - Math.sin(ClientInfo.ticksInGame / 8D) / 4D,
                       (float) (pos.getY()) + 0.75 - Math.pow(Math.sin(ClientInfo.ticksInGame / 32D), 2.0) / 2d,
                       (float) (pos.getZ()) + 0.5 - Math.cos(ClientInfo.ticksInGame / 8D) / 4D,
                       0, 0, 0);

               world.addParticle(GlowParticleData.createData(color2),
                       (float) (pos.getX()) +0.5 - Math.sin((ClientInfo.ticksInGame +offset)  / 8D)/4D,
                       (float) (pos.getY()) + 0.75 - Math.pow(Math.sin((ClientInfo.ticksInGame +offset)/ 32D), 2.0)/2d,
                       (float) (pos.getZ()) +0.5 - Math.cos((ClientInfo.ticksInGame +offset) / 8D)/4D,
                       0, 0, 0);

           }
           if(timeMixing >= 80) {

               offset = 50;
               world.addParticle(GlowParticleData.createData(color1),
                       (float) (pos.getX()) + 0.5 - Math.sin((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                       (float) (pos.getY()) + 0.75 - Math.pow(Math.sin((ClientInfo.ticksInGame + offset) / 32D), 2.0) / 2d,
                       (float) (pos.getZ()) + 0.5 - Math.cos((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                       0, 0, 0);

               offset = 70;
               world.addParticle(GlowParticleData.createData(color2),
                       (float) (pos.getX()) + 0.5 - Math.sin((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                       (float) (pos.getY()) + 0.75 - Math.pow(Math.sin((ClientInfo.ticksInGame + offset) / 32D), 2.0) / 2d,
                       (float) (pos.getZ()) + 0.5 - Math.cos((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                       0, 0, 0);
           }
           if(timeMixing >= 120)
                timeMixing = 0;
            return;
        }

        if(timeMixing % 20 == 0 && timeMixing > 0 && timeMixing <= 60){

            EntityFlyingItem item = new EntityFlyingItem(world,tile1.getPos().up(), pos, Math.round(255*color1.getRed()), Math.round(255*color1.getBlue()),Math.round(255*color1.getGreen()))
                    .withNoTouch();
            item.setDistanceAdjust(2f);
            world.addEntity(item);
            EntityFlyingItem item2 = new EntityFlyingItem(world,tile2.getPos().up(), pos,  Math.round(255*color2.getRed()), Math.round(255*color2.getBlue()),Math.round(255*color2.getGreen()))
                    .withNoTouch();
            item2.setDistanceAdjust(2f);
            world.addEntity(item2);
        }
        if(!world.isRemote && timeMixing >= 120){
            timeMixing++;
            if(timeMixing >= 120)
                timeMixing = 0;

            Potion jar1Potion = tile1.getPotion();

            if(combJar.getAmount() == 0){
                combJar.setPotion(jar1Potion, combined);
                combJar.setFill(100);
                tile1.addAmount(-300);
                tile2.addAmount(-300);
            }else if(combJar.isMixEqual(combined) && combJar.getMaxFill() - combJar.getCurrentFill() >= 100){
                combJar.addAmount(100);
                tile1.addAmount(-300);
                tile2.addAmount(-300);
            }
        }


    }

    public List<EffectInstance> getCombinedCustomResult(PotionJarTile jar1, PotionJarTile jar2){
        Set<EffectInstance> set = new HashSet<>();
        set.addAll(jar1.getCustomEffects());
        set.addAll(jar2.getCustomEffects());
        return new ArrayList<EffectInstance>(set);
    }

    public List<EffectInstance> getCombinedResult(PotionJarTile jar1, PotionJarTile jar2){
        Set<EffectInstance> set = new HashSet<>();
        set.addAll(jar1.getFullEffects());
        set.addAll(jar2.getFullEffects());
        return new ArrayList<EffectInstance>(set);
    }
    private <E extends TileEntity  & IAnimatable > PlayState idlePredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("spin", true));
        return this.isMixing ? PlayState.CONTINUE : PlayState.STOP;
    }
    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "rotate_controller", 0, this::idlePredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt) {
        this.timeMixing = nbt.getInt("mixing");
        this.isMixing = nbt.getBoolean("isMixing");
        super.read(state, nbt);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.putInt("mixing", timeMixing);
        compound.putBoolean("isMixing", isMixing);
        return super.write(compound);
    }
}
