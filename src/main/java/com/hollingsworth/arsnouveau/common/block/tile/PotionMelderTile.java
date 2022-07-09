package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.ClientInfo;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
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

public class PotionMelderTile extends ModdedTile implements IAnimatable, ITickable, IWandable, ITooltipProvider {
    int timeMixing;
    boolean isMixing;
    boolean hasMana;
    public boolean isOff;

    public List<BlockPos> fromJars = new ArrayList<>();
    public BlockPos toPos;

    AnimationFactory manager = new AnimationFactory(this);

    public PotionMelderTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.POTION_MELDER_TYPE, pos, state);
    }

    @Override
    public void tick() {
        if(isOff) {
            isMixing = false;
            timeMixing = 0;
            return;
        }
        if (!level.isClientSide && !hasMana && level.getGameTime() % 20 == 0) {
            if (SourceUtil.takeSourceNearbyWithParticles(worldPosition, level, 5, 100) != null) {
                hasMana = true;
                updateBlock();
            }

        }

        if (!hasMana || toPos == null || !level.isLoaded(toPos) ||  !takeJarsValid() || !(level.getBlockEntity(toPos) instanceof PotionJarTile combJar)) {
            isMixing = false;
            timeMixing = 0;
            return;
        }

        PotionJarTile tile1 = (PotionJarTile) level.getBlockEntity(fromJars.get(0));
        PotionJarTile tile2 = (PotionJarTile) level.getBlockEntity(fromJars.get(1));

        List<MobEffectInstance> combined = getCombinedResult(tile1, tile2);
        if (!canDestinationAccept(combJar, combined)) {
            isMixing = false;
            timeMixing = 0;
            return;
        }

        isMixing = true;
        timeMixing++;
        ParticleColor color1 = ParticleColor.fromInt(tile1.getColor());
        ParticleColor color2 = ParticleColor.fromInt(tile2.getColor());
        if (level.isClientSide) {
            //Burning jar
            if (timeMixing >= 80 && combJar.getPotion() != Potions.EMPTY) {
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
            if (timeMixing >= 60) {
                level.addParticle(GlowParticleData.createData(color1),
                        (worldPosition.getX()) + 0.5 - Math.sin(ClientInfo.ticksInGame / 8D) / 4D,
                        (worldPosition.getY()) + 0.75 - Math.pow(Math.sin(ClientInfo.ticksInGame / 32D), 2.0) / 2d,
                        (worldPosition.getZ()) + 0.5 - Math.cos(ClientInfo.ticksInGame / 8D) / 4D,
                        0, 0, 0);

                level.addParticle(GlowParticleData.createData(color2),
                        (worldPosition.getX()) + 0.5 - Math.sin((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                        (worldPosition.getY()) + 0.75 - Math.pow(Math.sin((ClientInfo.ticksInGame + offset) / 32D), 2.0) / 2d,
                        (worldPosition.getZ()) + 0.5 - Math.cos((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                        0, 0, 0);

            }
            if (timeMixing >= 80) {

                offset = 50;
                level.addParticle(GlowParticleData.createData(color1),
                        (worldPosition.getX()) + 0.5 - Math.sin((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                        (worldPosition.getY()) + 0.75 - Math.pow(Math.sin((ClientInfo.ticksInGame + offset) / 32D), 2.0) / 2d,
                        (worldPosition.getZ()) + 0.5 - Math.cos((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                        0, 0, 0);

                offset = 70;
                level.addParticle(GlowParticleData.createData(color2),
                        (worldPosition.getX()) + 0.5 - Math.sin((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                        (worldPosition.getY()) + 0.75 - Math.pow(Math.sin((ClientInfo.ticksInGame + offset) / 32D), 2.0) / 2d,
                        (worldPosition.getZ()) + 0.5 - Math.cos((ClientInfo.ticksInGame + offset) / 8D) / 4D,
                        0, 0, 0);
            }
            if (timeMixing >= 120)
                timeMixing = 0;
            return;
        }

        if (timeMixing % 20 == 0 && timeMixing > 0 && timeMixing <= 60) {

            EntityFlyingItem item = new EntityFlyingItem(level, tile1.getBlockPos().above(), worldPosition, Math.round(255 * color1.getRed()), Math.round(255 * color1.getGreen()), Math.round(255 * color1.getBlue()))
                    .withNoTouch();
            item.setDistanceAdjust(2f);
            level.addFreshEntity(item);
            EntityFlyingItem item2 = new EntityFlyingItem(level, tile2.getBlockPos().above(), worldPosition, Math.round(255 * color2.getRed()), Math.round(255 * color2.getGreen()), Math.round(255 * color2.getBlue()))
                    .withNoTouch();
            item2.setDistanceAdjust(2f);
            level.addFreshEntity(item2);
        }
        if (!level.isClientSide && timeMixing >= 120) {
            timeMixing++;
            if (timeMixing >= 120)
                timeMixing = 0;

            Potion jar1Potion = tile1.getPotion();

            if (combJar.getAmount() == 0) {
                combJar.setPotion(jar1Potion, combined);
                mergePotions(combJar, tile1, tile2);
            } else if (combJar.isMixEqual(combined) && combJar.getMaxFill() - combJar.getCurrentFill() >= 100) {
                mergePotions(combJar, tile1, tile2);
            }
        }
    }

    public boolean canDestinationAccept(PotionJarTile combJar,  List<MobEffectInstance> combined) {
        return (combJar.isMixEqual(combined) && (combJar.getMaxFill() - combJar.getCurrentFill() >= 100)) || combJar.getAmount() == 0;
    }

    public void mergePotions(PotionJarTile combJar, PotionJarTile take1, PotionJarTile take2){
        combJar.addAmount(100);
        take1.addAmount(-300);
        take2.addAmount(-300);
        hasMana = false;
        ParticleColor color2 = ParticleColor.fromInt(combJar.getColor());
        EntityFlyingItem item2 = new EntityFlyingItem(level, worldPosition, combJar.getBlockPos().above(), Math.round(255 * color2.getRed()), Math.round(255 * color2.getGreen()), Math.round(255 * color2.getBlue()))
                .withNoTouch();
        item2.setDistanceAdjust(2f);
        level.addFreshEntity(item2);
        updateBlock();
    }

    public boolean takeJarsValid(){
        if(fromJars.size() < 2)
            return false;
        for(BlockPos p : fromJars){
            BlockEntity te = level.getBlockEntity(p);
            if(!level.isLoaded(p) || !(te instanceof PotionJarTile jar) || jar.getCurrentFill() < 300){
                return false;
            }
        }
        return true;
    }

    public List<MobEffectInstance> getCombinedResult(PotionJarTile jar1, PotionJarTile jar2) {
        Set<MobEffectInstance> set = new HashSet<>();
        set.addAll(jar1.getFullEffects());
        set.addAll(jar2.getFullEffects());
        return new ArrayList<>(set);
    }

    @Override
    public void onWanded(Player playerEntity) {
        this.toPos = null;
        this.fromJars = new ArrayList<>();
        PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.cleared"));
        updateBlock();
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if(storedPos != null) {
            this.toPos = storedPos;
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.melder.to_set"));
            updateBlock();
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if(storedPos != null) {
            if(this.fromJars.size() >= 2){
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.melder.from_capped"));
                return;
            }
            this.fromJars.add(storedPos);
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.melder.from_set", fromJars.size()));
            updateBlock();
        }
    }

    private <E extends BlockEntity & IAnimatable> PlayState idlePredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("spin", true));
        return this.isMixing ? PlayState.CONTINUE : PlayState.STOP;
    }

    private <E extends BlockEntity & IAnimatable> PlayState floatPredicate(AnimationEvent<E> event) {
        event.getController().setAnimation(new AnimationBuilder().addAnimation("floating", true));
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        animationData.addAnimationController(new AnimationController(this, "rotate_controller", 0, this::idlePredicate));
        animationData.addAnimationController(new AnimationController(this, "float", 0, this::floatPredicate));
    }

    @Override
    public AnimationFactory getFactory() {
        return manager;
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.timeMixing = nbt.getInt("mixing");
        this.isMixing = nbt.getBoolean("isMixing");
        this.hasMana = nbt.getBoolean("hasMana");
        int counter = 0;

        while (NBTUtil.hasBlockPos(nbt, "from_" + counter)) {
            BlockPos pos = NBTUtil.getBlockPos(nbt, "from_" + counter);
            if (!this.fromJars.contains(pos))
                this.fromJars.add(pos);
            counter++;
        }

        this.toPos = NBTUtil.getBlockPos(nbt, "to_pos");
        this.isOff = nbt.getBoolean("off");
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        compound.putInt("mixing", timeMixing);
        compound.putBoolean("isMixing", isMixing);
        compound.putBoolean("hasMana", hasMana);

        NBTUtil.storeBlockPos(compound, "to_pos", this.toPos);
        int counter = 0;
        for (BlockPos p : this.fromJars) {
            NBTUtil.storeBlockPos(compound, "from_" + counter, p);
            counter++;
        }
        compound.putBoolean("off", this.isOff);
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if(!hasMana){
            tooltip.add(Component.translatable("ars_nouveau.needs_mana"));
        }
        tooltip.add(Component.translatable("ars_nouveau.melder.from_set", fromJars.size()));
        if(toPos == null){
            tooltip.add(Component.translatable("ars_nouveau.melder.no_to_pos"));
        }
        if(toPos != null && fromJars.size() == 2 && hasMana && !isMixing && !takeJarsValid()){
            tooltip.add(Component.translatable("ars_nouveau.melder.needs_potion"));
        }
        if(fromJars.size() >= 2 && toPos != null && level.getBlockEntity(toPos) instanceof PotionJarTile combJar){
            PotionJarTile tile1 = (PotionJarTile) level.getBlockEntity(fromJars.get(0));
            PotionJarTile tile2 = (PotionJarTile) level.getBlockEntity(fromJars.get(1));
            if(tile1.getAmount() < 300 || tile2.getAmount() < 300) {
                return;
            }
            List<MobEffectInstance> combined = getCombinedResult(tile1, tile2);
            if(!canDestinationAccept(combJar, combined)){
                tooltip.add(Component.translatable("ars_nouveau.melder.destination_invalid"));
            }
        }
    }
}
