package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.ANFakePlayer;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.source.ISpecialSourceProvider;
import com.hollingsworth.arsnouveau.api.spell.EntitySpellResolver;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.wrapped_caster.RuneCaster;
import com.hollingsworth.arsnouveau.api.util.IWololoable;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.RuneBlock;
import com.hollingsworth.arsnouveau.common.spell.method.MethodTouch;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import com.hollingsworth.arsnouveau.setup.registry.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;
import java.util.UUID;


public class RuneTile extends ModdedTile implements GeoBlockEntity, ITickable, ITooltipProvider, IWololoable {
    public Spell spell = new Spell();
    public boolean isTemporary;
    public boolean disabled;
    public boolean isCharged;
    public boolean isSensitive;
    public int ticksUntilCharge;
    public UUID uuid;
    public Entity touchedEntity;

    public RuneTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.RUNE_TILE, pos, state);
        isCharged = true;
        isTemporary = false;
        disabled = false;
        ticksUntilCharge = 0;
    }

    public void setSpell(Spell spell) {
        this.spell = spell.clone();
        updateBlock();
    }

    public void castSpell(Entity entity) {
        if (entity == null)
            return;
        if (!this.isCharged || spell.isEmpty() || !(level instanceof ServerLevel) || !(spell.get(0) instanceof MethodTouch))
            return;
        if (!this.isTemporary && this.disabled) return;
        try {

            Player playerEntity = uuid != null ? level.getPlayerByUUID(uuid) : ANFakePlayer.getPlayer((ServerLevel) level);
            playerEntity = !this.isSensitive || playerEntity == null ? ANFakePlayer.getPlayer((ServerLevel) level) : playerEntity;
            EntitySpellResolver resolver = new EntitySpellResolver(new SpellContext(entity.level, spell, playerEntity, new RuneCaster(this, SpellContext.CasterType.RUNE)));
            resolver.onCastOnEntity(ItemStack.EMPTY, entity, InteractionHand.MAIN_HAND);
            if (this.isTemporary) {
                level.destroyBlock(worldPosition, false);
                return;
            }
            this.isCharged = false;

            level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).cycle(RuneBlock.POWERED));
            ticksUntilCharge = 20 * 2;
            updateBlock();
        } catch (Exception e) {
            PortUtil.sendMessage(entity, Component.translatable("ars_nouveau.rune.error"));
            e.printStackTrace();
            level.destroyBlock(worldPosition, false);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        tag.put("spell", spell.serialize());
        tag.putBoolean("charged", isCharged);
        tag.putBoolean("redstone", disabled);
        tag.putBoolean("temp", isTemporary);
        tag.putInt("cooldown", ticksUntilCharge);
        if (uuid != null)
            tag.putUUID("uuid", uuid);
        tag.putBoolean("sensitive", isSensitive);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        this.spell = Spell.fromTag(tag.getCompound("spell"));
        this.isCharged = tag.getBoolean("charged");
        this.disabled = tag.getBoolean("redstone");
        this.isTemporary = tag.getBoolean("temp");
        this.ticksUntilCharge = tag.getInt("cooldown");
        if (tag.contains("uuid"))
            this.uuid = tag.getUUID("uuid");
        this.isSensitive = tag.getBoolean("sensitive");
    }

    @Override
    public void tick() {

        if (level == null)
            return;
        if (!level.isClientSide) {
            if (ticksUntilCharge > 0) {
                ticksUntilCharge -= 1;
                return;
            }
        }
        if (this.isCharged)
            return;
        if (!level.isClientSide && this.isTemporary) {
            level.destroyBlock(this.worldPosition, false);
        }
        if (!level.isClientSide) {
            ISpecialSourceProvider provider = SourceUtil.takeSourceWithParticles(worldPosition, level, 10, 100);
            if (provider != null) {
                this.isCharged = true;
                level.setBlockAndUpdate(worldPosition, level.getBlockState(worldPosition).cycle(RuneBlock.POWERED));
                updateBlock();
            } else {
                ticksUntilCharge = 20 * 3;
                updateBlock();
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {}

    AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return factory;
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (ArsNouveau.proxy.getPlayer().hasEffect(ModPotions.MAGIC_FIND_EFFECT)) {
            tooltip.add(Component.literal(spell.getDisplayString()));
        }
    }

    @Override
    public void setColor(ParticleColor color) {
        this.spell = spell.withColor(color);
        updateBlock();
    }

    @Override
    public ParticleColor getColor() {
        return spell.color();
    }
}