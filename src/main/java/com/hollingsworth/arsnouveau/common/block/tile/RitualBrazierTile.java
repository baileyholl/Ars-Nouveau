package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.entity.IDispellable;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.item.inv.IInvProvider;
import com.hollingsworth.arsnouveau.api.item.inv.InventoryManager;
import com.hollingsworth.arsnouveau.api.registry.ParticleColorRegistry;
import com.hollingsworth.arsnouveau.api.registry.RitualRegistry;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.spell.ILightable;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellStats;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.SourceUtil;
import com.hollingsworth.arsnouveau.client.particle.ColorPos;
import com.hollingsworth.arsnouveau.client.particle.GlowParticleData;
import com.hollingsworth.arsnouveau.client.particle.ParticleColor;
import com.hollingsworth.arsnouveau.client.particle.ParticleUtil;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.block.RitualBrazierBlock;
import com.hollingsworth.arsnouveau.common.util.PortUtil;
import com.hollingsworth.arsnouveau.setup.registry.BlockRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class RitualBrazierTile extends ModdedTile implements ITooltipProvider, GeoBlockEntity, ILightable, ITickable, IInvProvider, IDispellable, IWandable {
    public AbstractRitual ritual;
    AnimatableInstanceCache manager = GeckoLibUtil.createInstanceCache(this);
    public boolean isDecorative;
    public ParticleColor color = ParticleColor.defaultParticleColor();
    public boolean isOff;
    public BlockPos relayPos;


    public RitualBrazierTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public RitualBrazierTile(BlockPos p, BlockState s) {
        super(BlockRegistry.RITUAL_TILE.get(), p, s);
    }

    @Override
    public void onFinishedConnectionFirst(@Nullable BlockPos storedPos, @Nullable LivingEntity storedEntity, Player playerEntity) {
        // check if position is a BrazierRelayTile
        if(storedPos != null && level.getBlockEntity(storedPos) instanceof BrazierRelayTile relayTile){
            if(BlockUtil.distanceFrom(getBlockPos(), storedPos) > 16){
                PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.fail"));
                return;
            }
            relayPos = storedPos.immutable();
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.brazier_relay.connected"));
            updateBlock();
        }
    }

    @Override
    public void onWanded(Player playerEntity) {
        if(relayPos != null){
            relayPos = null;
            PortUtil.sendMessage(playerEntity, Component.translatable("ars_nouveau.connections.cleared"));
            updateBlock();
        }
    }

    @Override
    public List<ColorPos> getWandHighlight(List<ColorPos> list) {
        if(relayPos != null){
            list.add(ColorPos.centered(relayPos, ParticleColor.TO_HIGHLIGHT));
        }
        return list;
    }

    public void makeParticle(ParticleColor centerColor, ParticleColor outerColor, int intensity) {
        Level world = getLevel();
        BlockPos pos = getBlockPos();
        double xzOffset = 0.25;
        boolean isWeakFire = ritual != null && ritual.needsSourceNow();
        double centerYMax = isWeakFire ? 0.1 : 0.2;
        double outerYMax = isWeakFire ? 0.5 : 0.7;
        double ySpeed = isWeakFire ? 0.02f : 0.05f;
        intensity = isWeakFire ? intensity / 2 : intensity;
        for (int i = 0; i < intensity; i++) {
            world.addParticle(
                    GlowParticleData.createData(centerColor.transition((int) level.getGameTime() * 10)),
                    pos.getX() + 0.5 + ParticleUtil.inRange(-xzOffset / 2, xzOffset / 2), pos.getY() + 1 + ParticleUtil.inRange(-0.05, centerYMax), pos.getZ() + 0.5 + ParticleUtil.inRange(-xzOffset / 2, xzOffset / 2),
                    0, ParticleUtil.inRange(0.0, ySpeed), 0);
        }
        for (int i = 0; i < intensity; i++) {
            world.addParticle(
                    GlowParticleData.createData(outerColor.transition((int) level.getGameTime() * 10)),
                    pos.getX() + 0.5 + ParticleUtil.inRange(-xzOffset, xzOffset), pos.getY() + 1 + ParticleUtil.inRange(0, outerYMax), pos.getZ() + 0.5 + ParticleUtil.inRange(-xzOffset, xzOffset),
                    0, ParticleUtil.inRange(0.0, ySpeed), 0);
        }
        if(relayPos != null && level.getBlockEntity(relayPos) instanceof BrazierRelayTile relayTile){
            relayTile.makeParticle(centerColor, outerColor, intensity);
        }
    }

    @Override
    public void tick() {
        if (isDecorative && level.isClientSide) {
            makeParticle(color.transition((int) level.getGameTime() * 20), color.transition((int) level.getGameTime() * 20 + 200), 10);
            return;
        }


        if (level.isClientSide && ritual != null) {
            makeParticle(ritual.getCenterColor(), ritual.getOuterColor(), ritual.getParticleIntensity());
        }
        if (isOff)
            return;
        if (ritual != null) {

            if (ritual.getContext().isDone) {
                ritual.onEnd();
                ritual = null;
                getLevel().playSound(null, getBlockPos(), SoundEvents.FIRE_EXTINGUISH, SoundSource.NEUTRAL, 1.0f, 1.0f);
                getLevel().setBlock(getBlockPos(), getLevel().getBlockState(getBlockPos()).setValue(RitualBrazierBlock.LIT, false), 3);
                updateBlock();
                return;
            }
            if (!ritual.isRunning() && !level.isClientSide && level.getGameTime() % 5 == 0) {
                level.getEntitiesOfClass(ItemEntity.class, new AABB(getBlockPos()).inflate(1)).forEach(i -> {
                    tryBurnStack(i.getItem());
                });
            }
            if (ritual.consumesSource() && ritual.needsSourceNow()) {
                int cost = ritual.getSourceCost();
                if (SourceUtil.takeSourceMultipleWithParticles(getBlockPos(), getLevel(), 6, cost) != null) {
                    ritual.setNeedsSource(false);
                    updateBlock();
                } else {
                    return;
                }
            }
            if(this.relayPos != null && level.isLoaded(this.relayPos) && level.getBlockEntity(this.relayPos) instanceof BrazierRelayTile relayTile){
                ritual.tryTick(relayTile);
                relayTile.ticksToLightOff = 2;
                relayTile.isDecorative = false;
            }else{
                ritual.tryTick(this);
            }
        }
    }

    public boolean takeSource(){
        if (ritual.consumesSource() && ritual.needsSourceNow()) {
            int cost = ritual.getSourceCost();
            if (SourceUtil.takeSourceMultipleWithParticles(getBlockPos(), getLevel(), 6, cost) != null) {
                ritual.setNeedsSource(false);
                updateBlock();
                return true;
            }
        }
        return false;
    }

    public boolean tryBurnStack(ItemStack stack){
        if(ritual != null && !ritual.isRunning() && !level.isClientSide && ritual.canConsumeItem(stack)) {
            ritual.onItemConsumed(stack);
            ParticleUtil.spawnPoof((ServerLevel) level, getBlockPos());
            level.playSound(null, getBlockPos(), SoundEvents.FIRECHARGE_USE, SoundSource.NEUTRAL, 0.3f, 1.0f);
            return true;
        }
        return false;
    }

    public boolean isRitualDone() {
        return ritual != null && ritual.getContext().isDone;
    }

    @Deprecated(since = "4.10.1", forRemoval = true)
    public boolean canRitualStart() {
        return ritual.canStart(null);
    }

    @Deprecated(since = "4.10.1", forRemoval = true)
    public void startRitual() {
        startRitual(null);
    }

    public void startRitual(@Nullable Player player) {
        if (ritual == null || !ritual.canStart(player) || ritual.isRunning())
            return;
        getLevel().playSound(null, getBlockPos(), SoundEvents.ILLUSIONER_CAST_SPELL, SoundSource.NEUTRAL, 1.0f, 1.0f);
        ritual.onStart(player);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.loadAdditional(tag, pRegistries);
        String ritualIDString = tag.getString("ritualID");
        if (!ritualIDString.isEmpty()) {
            ResourceLocation ritualID = ResourceLocation.tryParse(ritualIDString);
            ritual = RitualRegistry.getRitual(ritualID);
            if (ritual != null) {
                ritual.tile = this;
                ritual.read(pRegistries, tag);
            }
        } else {
            ritual = null;
        }
        color = ParticleColorRegistry.from(tag.getCompound("color"));
        isDecorative = tag.getBoolean("decorative");
        isOff = tag.getBoolean("off");

        if(tag.contains("relayPos")){
            this.relayPos = BlockPos.of(tag.getLong("relayPos"));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider pRegistries) {
        super.saveAdditional(tag, pRegistries);
        if (ritual != null) {
            tag.putString("ritualID", ritual.getRegistryName().toString());
            ritual.write(pRegistries, tag);
        } else {
            tag.remove("ritualID");
        }
        tag.put("color", color.serialize());
        tag.putBoolean("decorative", isDecorative);
        tag.putBoolean("off", isOff);
        // store the relay position
        if(this.relayPos != null){
            tag.putLong("relayPos", this.relayPos.asLong());
        }
    }

    public boolean canTakeAnotherRitual() {
        return this.ritual == null || this.ritual.isRunning();
    }

    public void setRitual(ResourceLocation selectedRitual) {
        this.ritual = RitualRegistry.getRitual(selectedRitual);
        if (ritual != null) {
            this.ritual.tile = this;
            Level world = getLevel();
            BlockState state = world.getBlockState(getBlockPos());
            world.setBlock(getBlockPos(), state.setValue(RitualBrazierBlock.LIT, true), 3);
        }
        this.isDecorative = false;
        level.playSound(null, getBlockPos(), SoundEvents.FLINTANDSTEEL_USE, SoundSource.NEUTRAL, 1.0f, 1.0f);
        updateBlock();
    }

    @Override
    public void getTooltip(List<Component> tooltips) {
        if (ritual != null) {
            tooltips.add(Component.literal(ritual.getName()));
            if (isOff) {
                tooltips.add(Component.translatable("ars_nouveau.tooltip.turned_off").withStyle(ChatFormatting.GOLD));
                return;
            }
            if (!ritual.isRunning()) {
                if (!ritual.canStart(ArsNouveau.proxy.getPlayer())) {
                    tooltips.add(Component.translatable("ars_nouveau.tooltip.conditions_unmet").withStyle(ChatFormatting.GOLD));
                } else
                    tooltips.add(Component.translatable("ars_nouveau.tooltip.waiting").withStyle(ChatFormatting.GOLD));
            } else {

                tooltips.add(Component.translatable("ars_nouveau.tooltip.running"));
            }
            if (!ritual.getConsumedItems().isEmpty()) {
                tooltips.add(Component.translatable("ars_nouveau.tooltip.consumed"));
                for (String i : ritual.getFormattedConsumedItems()) {
                    tooltips.add(Component.literal( i));
                }
            }
            if (ritual.needsSourceNow())
                tooltips.add(Component.translatable("ars_nouveau.wixie.need_mana").withStyle(ChatFormatting.GOLD));
        }
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar animatableManager) {
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return manager;
    }

    @Override
    public void onLight(HitResult rayTraceResult, Level world, LivingEntity shooter, SpellStats stats, SpellContext spellContext) {
        this.color = spellContext.getColors();
        this.isDecorative = true;
        BlockState state = world.getBlockState(getBlockPos());
        world.setBlock(getBlockPos(), state.setValue(RitualBrazierBlock.LIT, true), 3);
        updateBlock();
    }

    @Override
    public boolean onDispel(@Nullable LivingEntity caster) {
        if(!isDecorative)
            return false;
        isDecorative = false;
        level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()).setValue(RitualBrazierBlock.LIT, false), 3);
        updateBlock();
        return true;
    }

    @Override
    public InventoryManager getInventoryManager() {
        return InventoryManager.fromTile(this);
    }
}
