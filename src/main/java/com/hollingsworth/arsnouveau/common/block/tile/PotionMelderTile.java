package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.util.ManaUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
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

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PotionMelderTile extends TileEntity implements IAnimatable, ITickableTileEntity {
    int timeMixing;
    boolean isMixing;
    boolean hasMana;
    public PotionMelderTile() {
        super(BlockRegistry.POTION_MELDER_TYPE);
    }

    AnimationFactory manager = new AnimationFactory(this);

    @Override
    public void tick() {

        if(!level.isClientSide && !hasMana && level.getGameTime() % 20 == 0){
            if(ManaUtil.takeManaNearbyWithParticles(worldPosition, level, 5, 100) != null) {
                hasMana = true;
                level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
            }

        }

        if(!hasMana)
            return;

        PotionJarTile tile1 = null;
        PotionJarTile tile2 = null;
        for(Direction d : Direction.values()){
            if(d == Direction.UP || d == Direction.DOWN)
                continue;
            if(tile1 != null && tile2 != null)
                break;
            TileEntity tileEntity = level.getBlockEntity(worldPosition.relative(d));
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
        if(level.getBlockEntity(worldPosition.below()) instanceof PotionJarTile)
            combJar = (PotionJarTile) level.getBlockEntity(worldPosition.below());

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
        if(level.isClientSide ) {
            //Burning jar
            if(timeMixing >= 80 && combJar.getPotion() != Potions.EMPTY) {
                for (int i = 0; i < 3; i++) {
                    double d0 = worldPosition.getX() + 0.5 + ParticleUtil.inRange(-0.25, 0.25);
                    double d1 = worldPosition.getY() + 1 + ParticleUtil.inRange(-0.1, 0.4);
                    double d2 = worldPosition.getZ() + .5 + ParticleUtil.inRange(-0.25, 0.25);
                    level.addParticle(GlowParticleData.createData(
                            ParticleColor.fromInt(combJar.getColor())),
                            d0, d1, d2,
                            0,
                            0.01f,
                            0);
                }
            }
            int offset = 30;
           if(timeMixing >= 60) {
               level.addParticle(GlowParticleData.createData(color1),
                       (float) (worldPosition.getX()) + 0.5 - Math.sin(ClientInfo.ticksInGame / 8D) / 4D,
                       (float) (worldPosition.getY()) + 0.75 - Math.pow(Math.sin(ClientInfo.ticksInGame / 32D), 2.0) / 2d,
                       (float) (worldPosition.getZ()) + 0.5 - Math.cos(ClientInfo.ticksInGame / 8D) / 4D,
                       0, 0, 0);

               level.addParticle(GlowParticleData.createData(color2),
                       (float) (worldPosition.getX()) +0.5 - Math.sin((ClientInfo.ticksInGame +offset)  / 8D)/4D,
                       (float) (worldPosition.getY()) + 0.75 - Math.pow(Math.sin((ClientInfo.ticksInGame +offset)/ 32D), 2.0)/2d,
                       (float) (worldPosition.getZ()) +0.5 - Math.cos((ClientInfo.ticksInGame +offset) / 8D)/4D,
                       0, 0, 0);

           }
           if(timeMixing >= 80) {

               offset = 50;
               level.addParticle(GlowParticleData.createData(color1),
                       (float) (worldPosition.getX()) + 0.5 - Math.sin((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                       (float) (worldPosition.getY()) + 0.75 - Math.pow(Math.sin((ClientInfo.ticksInGame + offset) / 32D), 2.0) / 2d,
                       (float) (worldPosition.getZ()) + 0.5 - Math.cos((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                       0, 0, 0);

               offset = 70;
               level.addParticle(GlowParticleData.createData(color2),
                       (float) (worldPosition.getX()) + 0.5 - Math.sin((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                       (float) (worldPosition.getY()) + 0.75 - Math.pow(Math.sin((ClientInfo.ticksInGame + offset) / 32D), 2.0) / 2d,
                       (float) (worldPosition.getZ()) + 0.5 - Math.cos((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                       0, 0, 0);
           }
           if(timeMixing >= 120)
                timeMixing = 0;
            return;
        }

        if(timeMixing % 20 == 0 && timeMixing > 0 && timeMixing <= 60){

            EntityFlyingItem item = new EntityFlyingItem(level,tile1.getBlockPos().above(), worldPosition, Math.round(255*color1.getRed()), Math.round(255*color1.getGreen()),Math.round(255*color1.getBlue()))
                    .withNoTouch();
            item.setDistanceAdjust(2f);
            level.addFreshEntity(item);
            EntityFlyingItem item2 = new EntityFlyingItem(level,tile2.getBlockPos().above(), worldPosition,  Math.round(255*color2.getRed()), Math.round(255*color2.getGreen()),Math.round(255*color2.getBlue()))
                    .withNoTouch();
            item2.setDistanceAdjust(2f);
            level.addFreshEntity(item2);
        }
        if(!level.isClientSide && timeMixing >= 120){
            timeMixing++;
            if(timeMixing >= 120)
                timeMixing = 0;

            Potion jar1Potion = tile1.getPotion();

            if(combJar.getAmount() == 0){
                combJar.setPotion(jar1Potion, combined);
                combJar.setFill(100);
                tile1.addAmount(-300);
                tile2.addAmount(-300);
                hasMana = false;
                level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
            }else if(combJar.isMixEqual(combined) && combJar.getMaxFill() - combJar.getCurrentFill() >= 100){
                combJar.addAmount(100);
                tile1.addAmount(-300);
                tile2.addAmount(-300);
                hasMana = false;
                level.sendBlockUpdated(worldPosition, level.getBlockState(worldPosition), level.getBlockState(worldPosition), 3);
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
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        this.timeMixing = nbt.getInt("mixing");
        this.isMixing = nbt.getBoolean("isMixing");
        this.hasMana = nbt.getBoolean("hasMana");
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        compound.putInt("mixing", timeMixing);
        compound.putBoolean("isMixing", isMixing);
        compound.putBoolean("hasMana", hasMana);
        return super.save(compound);
    }

    @Override
    @Nullable
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, 3, this.getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(level.getBlockState(worldPosition),pkt.getTag());
    }
}
