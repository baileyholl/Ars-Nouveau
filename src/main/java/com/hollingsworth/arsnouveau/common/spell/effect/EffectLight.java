package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.client.particle.RainbowParticleColor;
import com.hollingsworth.arsnouveau.common.block.SconceBlock;
import com.hollingsworth.arsnouveau.common.block.tile.LightTile;
import com.hollingsworth.arsnouveau.common.block.tile.TempLightTile;
import com.hollingsworth.arsnouveau.common.lib.GlyphLib;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateGlowColor;
import com.hollingsworth.arsnouveau.common.spell.augment.*;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class EffectLight extends AbstractEffect implements IPotionEffect {
    public static EffectLight INSTANCE = new EffectLight();

    private EffectLight() {
        super(GlyphLib.EffectLightID, "Conjure Magelight");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        if (rayTraceResult.getEntity() instanceof ILightable iLightable) {
            iLightable.onLight(rayTraceResult, world, shooter, spellStats, spellContext);
        }

        if (!(rayTraceResult.getEntity() instanceof LivingEntity living))
            return;
        if (!shooter.equals(living) || spellStats.isSensitive()) {
            this.applyConfigPotion(living, MobEffects.GLOWING, spellStats);
            // apply custom color to the entity if sensitive and the effect is successfully applied
            if (living.hasEffect(MobEffects.GLOWING) && spellStats.isSensitive())
                Networking.sendToNearbyClient(world, living, new PacketUpdateGlowColor(living.getId(), spellContext.getColors() instanceof RainbowParticleColor ? -1 : spellContext.getColors().getColor()));
        }
        this.applyConfigPotion(living, MobEffects.NIGHT_VISION, spellStats);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @NotNull LivingEntity shooter, SpellStats spellStats, SpellContext spellContext, SpellResolver resolver) {
        BlockPos pos = rayTraceResult.getBlockPos().relative(rayTraceResult.getDirection());
        Player player = getPlayer(shooter, (ServerLevel) world);
        if (!BlockUtil.destroyRespectsClaim(player, world, pos))
            return;

        if (world.getBlockEntity(rayTraceResult.getBlockPos()) instanceof ILightable lightable) {
            lightable.onLight(rayTraceResult, world, shooter, spellStats, spellContext);
            return;
        } else if (world.getBlockState(rayTraceResult.getBlockPos()).getBlock() instanceof ILightable lightable) {
            lightable.onLight(rayTraceResult, world, shooter, spellStats, spellContext);
            return;
        } else if (world.getBlockEntity(pos) instanceof SignBlockEntity sign) {
            sign.updateText((a) -> sign.getText(true).setHasGlowingText(true),
                    sign.isFacingFrontText(player)
            );
            world.gameEvent(GameEvent.BLOCK_CHANGE, sign.getBlockPos(), GameEvent.Context.of(player, sign.getBlockState()));
        }

        if (world.getBlockState(pos).canBeReplaced()
                && world.isUnobstructed(BlockRegistry.LIGHT_BLOCK.get().defaultBlockState(), pos, CollisionContext.of(ANFakePlayer.getPlayer((ServerLevel) world)))
                && world.isInWorldBounds(pos)) {
            BlockState lightBlockState = (spellStats.getDurationMultiplier() != 0 ? BlockRegistry.T_LIGHT_BLOCK.get() : BlockRegistry.LIGHT_BLOCK.get()).defaultBlockState().setValue(WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER);
            world.setBlockAndUpdate(pos, lightBlockState.setValue(SconceBlock.LIGHT_LEVEL, Math.max(0, Math.min(15, 14 + (int) spellStats.getAmpMultiplier()))));
            if (world.getBlockEntity(pos) instanceof LightTile tile) {
                tile.color = spellContext.getColors();
                if (tile instanceof TempLightTile tempLightTile)
                    tempLightTile.lengthModifier = spellStats.getDurationMultiplier();
            }
            world.sendBlockUpdated(pos, world.getBlockState(pos), world.getBlockState(pos), 2);

        }
    }

    @Override
    public void buildConfig(ModConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addPotionConfig(builder, 30);
        addExtendTimeConfig(builder, 8);
    }

    @Override
    protected void addDefaultAugmentLimits(Map<ResourceLocation, Integer> defaults) {
        defaults.put(AugmentAmplify.INSTANCE.getRegistryName(), 1);
        defaults.put(AugmentSensitive.INSTANCE.getRegistryName(), 1);
    }

    @Override
    public int getDefaultManaCost() {
        return 25;
    }

    @NotNull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDurationDown.INSTANCE, AugmentDampen.INSTANCE, AugmentExtendTime.INSTANCE, AugmentSensitive.INSTANCE);
    }

    @Override
    public void addAugmentDescriptions(Map<AbstractAugment, String> map) {
        super.addAugmentDescriptions(map);
        map.put(AugmentExtendTime.INSTANCE, "The light will instead be temporary, lasting longer. Affects the duration of Glowing and Night Vision.");
        map.put(AugmentDurationDown.INSTANCE, "The light will last for a shorter duration. Affects the duration of Glowing and Night Vision.");
        map.put(AugmentAmplify.INSTANCE, "Increases the light level.");
        map.put(AugmentDampen.INSTANCE, "Decreases the light level.");
        map.put(AugmentSensitive.INSTANCE, "Allows Glowing to be applied to the caster and applies the spell color to the Glowing effect.");
    }

    @Override
    public String getBookDescription() {
        return "If cast on a block, a permanent light source is created. May be amplified up to Glowstone brightness, or Dampened for a lower light level. When cast on yourself, you will receive night vision. When cast on other entities or with Sensitive, they will receive Night Vision and Glowing. If Sensitive, Glowing will use the spell color.";
    }

    @NotNull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.CONJURATION);
    }

    @Override
    public int getBaseDuration() {
        return POTION_TIME == null ? 30 : POTION_TIME.get();
    }

    @Override
    public int getExtendTimeDuration() {
        return EXTEND_TIME == null ? 8 : EXTEND_TIME.get();
    }
}
