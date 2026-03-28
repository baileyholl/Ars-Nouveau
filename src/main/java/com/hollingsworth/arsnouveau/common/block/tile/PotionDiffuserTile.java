package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.util.NBTUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.entity.EntityFlyingItem;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.common.util.PotionUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PotionDiffuserTile extends ModdedTile implements ITickable, IWandable, ITooltipProvider {

    public PotionContents lastConsumedPotion = PotionContents.EMPTY;
    public int ticksToConsume;
    public BlockPos boundPos;
    public boolean isOff;

    public PotionDiffuserTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.POTION_DIFFUSER_TILE, pos, state);
    }

    @Override
    public void tick() {
        if (isOff)
            return;
        if (level.isClientSide() && !isOff && ticksToConsume > 0 && !PotionUtil.isEmpty(lastConsumedPotion) && level.getGameTime() % 8 == 0) {
            level.addParticle(ParticleTypes.SMOKE, getX() + 0.5, getY() + 1, getZ() + 0.5, 0, 0, 0);
            return;
        }

        if (ticksToConsume <= 0 && boundPos != null && level.getGameTime() % 60 == 0) {
            obtainPotion();
        }
        if (ticksToConsume > 0) {
            ticksToConsume--;
            if (level.getGameTime() % (15 * 20) == 0) {
                for (LivingEntity entity : level.getEntitiesOfClass(LivingEntity.class, new AABB(getBlockPos()).inflate(10))) {
                    lastConsumedPotion.forEachEffect(effectinstance -> {
                        if (effectinstance.getEffect().value().isInstantenous()) {
                            effectinstance.getEffect().value().applyInstantenousEffect((ServerLevel) level, null, null, entity, effectinstance.getAmplifier(), 1.0D);
                        } else {
                            entity.addEffect(new MobEffectInstance(effectinstance), null);
                        }
                    }, 1.0f);
                }
            }
        }
    }

    public void obtainPotion() {
        if (level instanceof ServerLevel serverLevel && level.isLoaded(boundPos) && level.getBlockEntity(boundPos) instanceof PotionJarTile jar) {
            if (!PotionUtil.isEmpty(jar.getData()) && jar.getAmount() >= 100) {
                lastConsumedPotion = jar.getData();
                ticksToConsume = 20 * 60 * 10; // 10 mins
                jar.remove(100);
                ParticleColor color2 = ParticleColor.fromInt(jar.getColor());
                EntityFlyingItem.spawn(serverLevel, jar.getBlockPos().above(), worldPosition, Math.round(255 * color2.getRed()), Math.round(255 * color2.getGreen()), Math.round(255 * color2.getBlue()))
                        .withNoTouch().setDistanceAdjust(2f);
                updateBlock();
            }
        }
    }


    @Override
    public void onFinishedConnectionLast(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        if (storedPos != null && level.getBlockEntity(storedPos) instanceof PotionJarTile jar) {
            boundPos = storedPos.immutable();
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.potion_diffuser.set_pos"));
            if (this.lastConsumedPotion != null && !PotionUtil.isEmpty(lastConsumedPotion) && !PotionUtil.arePotionContentsEqual(lastConsumedPotion, jar.getData())) {
                obtainPotion();
            }
            updateBlock();
        } else {
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.potion_diffuser.bind_to_jar"));
        }
    }

    @Override
    protected void loadAdditional(ValueInput pTag) {
        super.loadAdditional(pTag);
        boundPos = NBTUtil.getNullablePos(pTag, "boundPos");
        isOff = pTag.getBooleanOr("isOff", false);
        ticksToConsume = pTag.getIntOr("ticksToConsume", 0);
        lastConsumedPotion = pTag.read("lastConsumedPotion", PotionContents.CODEC).orElse(PotionContents.EMPTY);
    }

    @Override
    protected void saveAdditional(ValueOutput tag) {
        super.saveAdditional(tag);
        if (boundPos != null) {
            NBTUtil.storeBlockPos(tag, "boundPos", boundPos);
        }
        tag.putBoolean("isOff", isOff);
        tag.putInt("ticksToConsume", ticksToConsume);
        if (lastConsumedPotion != null && lastConsumedPotion != PotionContents.EMPTY) {
            tag.store("lastConsumedPotion", PotionContents.CODEC, lastConsumedPotion);
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (boundPos == null) {
            tooltip.add(Component.translatable("ars_nouveau.potion_diffuser.no_pos").withStyle(ChatFormatting.GOLD));
        }
        if (isOff) {
            tooltip.add(Component.translatable("ars_nouveau.potion_diffuser.off").withStyle(ChatFormatting.GOLD));
        }
        if (lastConsumedPotion != null && lastConsumedPotion != PotionContents.EMPTY) {
            PotionContents.addPotionTooltip(lastConsumedPotion.getAllEffects(), tooltip::add, 1.0f, 20f);
        }
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> colorPos) {
        if (boundPos != null) {
            colorPos.add(ColorPos.centered(boundPos));
        }
        return colorPos;
    }
}
