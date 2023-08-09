package com.hollingsworth.arsnouveau.common.block.tile;

import com.hollingsworth.arsnouveau.api.client.ITooltipProvider;
import com.hollingsworth.arsnouveau.api.event.SpellCastEvent;
import com.hollingsworth.arsnouveau.api.event.SpellResolveEvent;
import com.hollingsworth.arsnouveau.api.item.IWandable;
import com.hollingsworth.arsnouveau.api.spell.AbstractSpellPart;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.api.util.LevelPosMap;
import com.hollingsworth.arsnouveau.common.block.ITickable;
import com.hollingsworth.arsnouveau.common.datagen.BlockTagProvider;
import com.hollingsworth.arsnouveau.common.items.SpellParchment;
import com.hollingsworth.arsnouveau.setup.BlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

public class SpellSensorTile extends ModdedTile implements ITickable, IWandable, ITooltipProvider {

    public static LevelPosMap SENSOR_MAP = new LevelPosMap((level, pos) -> !(level.getBlockEntity(pos) instanceof SpellSensorTile));

    public int outputDuration;
    public int outputStrength;
    public boolean onCooldown;
    public int listenRange = 8;
    public boolean isOnResolve = false;
    public ItemStack parchment = ItemStack.EMPTY;

    public SpellSensorTile(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
        super(tileEntityTypeIn, pos, state);
    }

    public SpellSensorTile(BlockPos pos, BlockState state) {
        super(BlockRegistry.SPELL_SENSOR_TILE.get(), pos, state);
    }

    public static void onSpellCast(SpellCastEvent event){
        SENSOR_MAP.applyForRange(event.getWorld(), new BlockPos(event.context.getCaster().getPosition()), 8, (pos) ->{
            if(event.getWorld().getBlockEntity(pos) instanceof SpellSensorTile tile && !tile.isOnResolve){
                tile.onSignal(event.context.getCaster().getPosition(), event.spell);
            }
            return false;
        });
    }

    public static void onSpellResolve(SpellResolveEvent.Post event){
        HitResult resolveSource = event.resolver.hitResult;
        if(resolveSource == null)
            return;
        SENSOR_MAP.applyForRange(event.world, resolveSource.getLocation(), 8, (pos) ->{
            if(event.world.getBlockEntity(pos) instanceof SpellSensorTile tile && tile.isOnResolve){
                tile.onSignal(resolveSource.getLocation(), event.spell);
            }
            return false;
        });
    }

    @Override
    public void onWanded(Player playerEntity) {
        this.isOnResolve = !this.isOnResolve;
        this.updateBlock();
    }

    public void onSignal(Vec3 pos, Spell spell){
        if(this.onCooldown || outputDuration > 0){
            return;
        }
        Vec3 thisPos = new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
        if(BlockUtil.distanceFrom(pos, thisPos) > listenRange || isOccluded(level, thisPos, pos)){
            return;
        }
        if(!this.parchment.isEmpty()){
            // Compare spell to parchment
            if(this.parchment.getItem() instanceof SpellParchment spellParchment){
                Spell listeningSpell = spellParchment.getSpellCaster(parchment.getOrCreateTag()).getSpell();
                List<AbstractSpellPart> spellParts = listeningSpell.recipe.stream().filter(Objects::nonNull).toList();
                List<AbstractSpellPart> spellParts1 = spell.recipe.stream().filter(Objects::nonNull).toList();
                if(!spellParts.equals(spellParts1)){
                    return;
                }
            }
        }
        outputDuration = 20;
        outputStrength = 15;
        updateBlock();
        level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
    }

    private static boolean isOccluded(Level pLevel, Vec3 pFrom, Vec3 pTo) {
        Vec3 vec3 = new Vec3((double) Mth.floor(pFrom.x) + 0.5D, (double)Mth.floor(pFrom.y) + 0.5D, (double)Mth.floor(pFrom.z) + 0.5D);
        Vec3 vec31 = new Vec3((double)Mth.floor(pTo.x) + 0.5D, (double)Mth.floor(pTo.y) + 0.5D, (double)Mth.floor(pTo.z) + 0.5D);

        for(Direction direction : Direction.values()) {
            Vec3 vec32 = vec3.relative(direction, (double)1.0E-5F);
            if (pLevel.isBlockInLine(new ClipBlockStateContext(vec32, vec31, (p_223780_) -> {
                return p_223780_.is(BlockTagProvider.OCCLUDES_SPELL_SENSOR);
            })).getType() != HitResult.Type.BLOCK) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void tick() {
        if(level.isClientSide){
            return;
        }
        if(outputDuration > 0){
            outputDuration--;
            if(outputDuration <= 0){
                outputStrength = 0;
                onCooldown = true;
                // 1 tick cooldown
                updateBlock();
                level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
                level.scheduleTick(worldPosition, getBlockState().getBlock(), 1);
            }
        }
        if(level.getGameTime() % 20 == 0){
            SENSOR_MAP.addPosition(level, worldPosition);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("outputDuration", outputDuration);
        tag.putInt("outputStrength", outputStrength);
        tag.putBoolean("isOnResolve", isOnResolve);
        tag.put("parchment", parchment.save(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        this.outputDuration = pTag.getInt("outputDuration");
        this.outputStrength = pTag.getInt("outputStrength");
        this.isOnResolve = pTag.getBoolean("isOnResolve");
        this.parchment = ItemStack.of(pTag.getCompound("parchment"));
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if(isOnResolve) {
            tooltip.add(Component.translatable("ars_nouveau.sensor.on_resolve"));
        }else{
            tooltip.add(Component.translatable("ars_nouveau.sensor.on_cast"));
        }
        if(!this.parchment.isEmpty() && parchment.getItem() instanceof SpellParchment spellParchment){
            spellParchment.getInformation(parchment, level, tooltip, TooltipFlag.Default.NORMAL);
        }
    }
}
