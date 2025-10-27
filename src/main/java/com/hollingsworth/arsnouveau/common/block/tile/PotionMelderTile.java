package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import com.hollingsworth.arsnouveau.setup.config.Config;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PotionMelderTile extends ModdedTile implements GeoBlockEntity, ITickable, IWandable, ITooltipProvider {
    int timeMixing;
    boolean isMixing;
    boolean hasSource;
    public boolean isOff;
    int lastMixedColor;
    public List<BlockPos> fromJars = new ArrayList<>();
    public BlockPos toPos;

    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);

    public PotionMelderTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.POTION_MELDER_TYPE, pos, state);
    }

    @Override
    public void tick() {
        if (level.isClientSide) {
            BlockPos pos = getBlockPos();
            if (level.random.nextInt(6) == 0) {
                level.addParticle(ParticleTypes.BUBBLE_POP, pos.getX() + ParticleUtil.inRange(-0.25, 0.25) + 0.5, pos.getY() + 1, pos.getZ() + 0.5 + ParticleUtil.inRange(-0.25, 0.25), 0, 0, 0);

            }
        }
        int maxMergeTicks = 160;
        if (isOff) {
            isMixing = false;
            timeMixing = 0;
            return;
        }
        if (!level.isClientSide && !hasSource && level.getGameTime() % 20 == 0) {
            if (SourceUtil.takeSourceMultipleWithParticles(worldPosition, level, 5, Config.MELDER_SOURCE_COST.get()) != null) {
                hasSource = true;
                updateBlock();
            }

        }

        if (!hasSource || toPos == null || !level.isLoaded(toPos) || !takeJarsValid() || !(level.getBlockEntity(toPos) instanceof PotionJarTile combJar)) {
            isMixing = false;
            timeMixing = 0;
            return;
        }

        PotionJarTile tile1 = (PotionJarTile) level.getBlockEntity(fromJars.get(0));
        PotionJarTile tile2 = (PotionJarTile) level.getBlockEntity(fromJars.get(1));
        PotionContents data = getCombinedResult(tile1, tile2);
        if (!isOutputUnique(data, tile1, tile2) || outputHasDuplicateEffect(data)) {
            return;
        }
        if (!combJar.canAccept(data, Config.MELDER_OUTPUT.get())) {
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
            if (timeMixing >= 120) {
                for (int i = 0; i < 3; i++) {
                    double d0 = worldPosition.getX() + 0.5 + ParticleUtil.inRange(-0.25, 0.25);
                    double d1 = worldPosition.getY() + 1 + ParticleUtil.inRange(-0.1, 0.4);
                    double d2 = worldPosition.getZ() + .5 + ParticleUtil.inRange(-0.25, 0.25);
                    level.addParticle(GlowParticleData.createData(
                                    ParticleColor.fromInt(PotionContents.getColor(data.getAllEffects()))),
                            d0, d1, d2,
                            0,
                            0.01f,
                            0);
                }
                lastMixedColor = PotionContents.getColor(data.getAllEffects());
            }
            if (timeMixing >= 160)
                timeMixing = 0;
            return;
        }

        if (level instanceof ServerLevel serverLevel) {
            if (timeMixing % 20 == 0 && timeMixing > 0 && timeMixing <= 60) {
                EntityFlyingItem.spawn(serverLevel, tile1.getBlockPos().above(), worldPosition, Math.round(255 * color1.getRed()), Math.round(255 * color1.getGreen()), Math.round(255 * color1.getBlue()))
                        .withNoTouch().setDistanceAdjust(2f);
                EntityFlyingItem.spawn(serverLevel, tile2.getBlockPos().above(), worldPosition, Math.round(255 * color2.getRed()), Math.round(255 * color2.getGreen()), Math.round(255 * color2.getBlue()))
                        .withNoTouch().setDistanceAdjust(2f);
            }
            if (timeMixing >= maxMergeTicks) {
                timeMixing = 0;
                mergePotions(combJar, tile1, tile2, data);
            }
        }
    }

    public void mergePotions(PotionJarTile combJar, PotionJarTile take1, PotionJarTile take2, PotionContents data) {
        combJar.add(
                data, Config.MELDER_OUTPUT.get());
        take1.remove(Config.MELDER_INPUT_COST.get());
        take2.remove(Config.MELDER_INPUT_COST.get());
        hasSource = false;
        ParticleColor color2 = ParticleColor.fromInt(combJar.getColor());
        EntityFlyingItem.spawn((ServerLevel) level, new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 1.0, worldPosition.getZ() + 0.5),
                        new Vec3(combJar.getX() + 0.5, combJar.getY(), combJar.getZ() + 0.5),
                        Math.round(255 * color2.getRed()), Math.round(255 * color2.getGreen()), Math.round(255 * color2.getBlue()))
                .withNoTouch()
                .setDistanceAdjust(2f);
        updateBlock();
    }

    public boolean takeJarsValid() {
        if (fromJars.size() < 2)
            return false;
        for (BlockPos p : fromJars) {
            BlockEntity te = level.getBlockEntity(p);
            if (!level.isLoaded(p) || !(te instanceof PotionJarTile jar) || jar.getAmount() < Config.MELDER_INPUT_COST.get()) {
                return false;
            }
        }
        return true;
    }

    public PotionContents getCombinedResult(PotionJarTile jar1, PotionJarTile jar2) {
        return PotionUtil.merge(jar1.getData(), jar2.getData());
    }

    public boolean isOutputUnique(PotionContents mix, PotionJarTile take1, PotionJarTile take2) {
        return !PotionUtil.arePotionContentsEqual(mix, take1.getData()) && !PotionUtil.arePotionContentsEqual(mix, take2.getData());
    }

    public boolean outputHasDuplicateEffect(PotionContents mix) {
        var effects = mix.getAllEffects();
        var effectSet = new HashSet<MobEffect>();
        for (var effect : effects) {
            if (!effectSet.add(effect.getEffect().value())) {
                return true;
            }
        }
        return false;
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
        if (storedPos != null) {
            if (!closeEnough(storedPos, worldPosition)) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.melder.too_far"));
                return;
            }
            this.toPos = storedPos;
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.melder.to_set"));
            updateBlock();
        }
    }

    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null) {
            if (!closeEnough(storedPos, worldPosition)) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.melder.too_far"));
                return;
            }

            if (this.fromJars.size() >= 2) {
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.melder.from_capped"));
                return;
            }
            this.fromJars.add(storedPos);
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.melder.from_set", fromJars.size()));
            updateBlock();
        }
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        if (toPos != null)
            list.add(ColorPos.centered(toPos, ParticleColor.TO_HIGHLIGHT));
        for (BlockPos p : fromJars) {
            list.add(ColorPos.centered(p, ParticleColor.FROM_HIGHLIGHT));
        }
        return list;
    }

    public boolean closeEnough(BlockPos pos1, BlockPos pos2) {
        return BlockUtil.distanceFrom(Vec3.atCenterOf(pos1), Vec3.atCenterOf(pos2)) <= 3.5;
    }

    private <E extends BlockEntity & GeoAnimatable> PlayState idlePredicate(AnimationState<E> event) {
        event.getController().setAnimation(RawAnimation.begin().thenPlay("stir"));
        return this.isMixing ? PlayState.CONTINUE : PlayState.STOP;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
        animatableManager.add(new AnimationController<>(this, "rotate_controller", 0, this::idlePredicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries) {
        super.loadAdditional(nbt, pRegistries);
        fromJars = new ArrayList<>();
        this.timeMixing = nbt.getInt("mixing");
        this.isMixing = nbt.getBoolean("isMixing");
        this.hasSource = nbt.getBoolean("hasMana");
        int counter = 0;

        while (NBTUtil.hasBlockPos(nbt, "from_" + counter)) {
            BlockPos pos = NBTUtil.getBlockPos(nbt, "from_" + counter);
            if (!this.fromJars.contains(pos))
                this.fromJars.add(pos);
            counter++;
        }

        this.toPos = NBTUtil.getNullablePos(nbt, "to_pos");
        this.isOff = nbt.getBoolean("off");
        this.lastMixedColor = nbt.getInt("lastMixedColor");
    }

    @Override
    public void saveAdditional(CompoundTag compound, HolderLookup.Provider pRegistries) {
        super.saveAdditional(compound, pRegistries);
        compound.putInt("mixing", timeMixing);
        compound.putBoolean("isMixing", isMixing);
        compound.putBoolean("hasMana", hasSource);
        compound.putInt("lastMixedColor", lastMixedColor);
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
        if (!hasSource) {
            tooltip.add(Component.translatable("ars_nouveau.apparatus.nomana").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        }
        if (fromJars.size() < 2)
            tooltip.add(Component.translatable("ars_nouveau.melder.from_set", fromJars.size()).setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        if (toPos == null || !level.isLoaded(toPos) || !(level.getBlockEntity(toPos) instanceof PotionJarTile)) {
            tooltip.add(Component.translatable("ars_nouveau.melder.no_to_pos").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        }
        if (toPos != null && fromJars.size() == 2 && hasSource && !isMixing && !takeJarsValid()) {
            tooltip.add(Component.translatable("ars_nouveau.melder.needs_potion").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
        }
        if (fromJars.size() >= 2 && toPos != null && level.getBlockEntity(toPos) instanceof PotionJarTile combJar) {
            BlockEntity tile1 = level.getBlockEntity(fromJars.get(0));
            BlockEntity tile2 = level.getBlockEntity(fromJars.get(1));
            int inputCost = Config.MELDER_INPUT_COST.get();
            if (!(tile1 instanceof PotionJarTile jar1) || !(tile2 instanceof PotionJarTile jar2) || jar1.getAmount() < inputCost || jar2.getAmount() < inputCost) {
                return;
            }
            PotionContents data = getCombinedResult(jar1, jar2);

            if (!isOutputUnique(data, jar1, jar2)) {
                tooltip.add(Component.translatable("ars_nouveau.melder.output_not_unique").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
                return;
            }
            if (outputHasDuplicateEffect(data)) {
                tooltip.add(Component.translatable("ars_nouveau.melder.output_duplicate_effect").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
                return;
            }
            if (!combJar.canAccept(data, Config.MELDER_OUTPUT.get())) {
                tooltip.add(Component.translatable("ars_nouveau.melder.destination_invalid").setStyle(Style.EMPTY.withColor(ChatFormatting.GOLD)));
            }
        }
    }

    public int getColor() {
        return lastMixedColor == 0 ? new ParticleColor(200, 0, 200).getColor() : lastMixedColor;
    }
}
