package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectBlink extends AbstractEffect {
    public static EffectBlink INSTANCE = new EffectBlink();

    private EffectBlink() {
        super(GlyphLib.EffectBlinkID, "Blink");
    }

    @Override
    public void onResolveEntity(EntityHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Vec3 vec = safelyGetHitPos(rayTraceResult);
        double distance = GENERIC_INT.get() + AMP_VALUE.get() * spellStats.getAmpMultiplier();

        if(spellContext.castingTile instanceof IInventoryResponder){
            ItemStack scroll = ((IInventoryResponder) spellContext.castingTile).getItem(new ItemStack(ItemsRegistry.WARP_SCROLL));
            if(!scroll.isEmpty()){
                BlockPos pos = WarpScroll.getPos(scroll);
                if(pos != null){
                    warpEntity(rayTraceResult.getEntity(), pos);
                    return;
                }
            }
        }

        if((rayTraceResult).getEntity().equals(shooter)) {
            blinkForward(world, shooter, distance);
            return;
        }

        if(isRealPlayer(shooter) && spellContext.castingTile == null && shooter != null) {
            if(shooter.getOffhandItem().getItem() instanceof WarpScroll){
                BlockPos warpPos = WarpScroll.getPos(shooter.getOffhandItem());
                if(warpPos != null && !warpPos.equals(BlockPos.ZERO)){
                    warpEntity(rayTraceResult.getEntity(), warpPos);

                }

            }else
                shooter.teleportTo(vec.x(), vec.y(), vec.z());


        }else if(spellContext.getType() == SpellContext.CasterType.RUNE && rayTraceResult.getEntity() instanceof LivingEntity){
            blinkForward(world, (LivingEntity) rayTraceResult.getEntity(), distance);
        }
    }

    public static void warpEntity(Entity entity, BlockPos warpPos){
        if(entity == null)
            return;
        Level world = entity.level;
        ((ServerLevel) entity.level).sendParticles(ParticleTypes.PORTAL, entity.getX(),  entity.getY() + 1,  entity.getZ(),
                4,(world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);

        entity.teleportTo(warpPos.getX() +0.5, warpPos.getY(), warpPos.getZ() +0.5);
        Networking.sendToNearby(world, entity, new PacketWarpPosition(entity.getId(), entity.getX(), entity.getY(), entity.getZ(), entity.xRot, entity.yRot));
        entity.level.playSound(null, warpPos, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundSource.NEUTRAL, 1.0f, 1.0f);
        ((ServerLevel) entity.level).sendParticles(ParticleTypes.PORTAL, warpPos.getX() +0.5,  warpPos.getY() + 1.0,  warpPos.getZ() +0.5,
                4,(world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
    }

    @Override
    public void onResolveBlock(BlockHitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        Vec3 vec = rayTraceResult.getLocation();
        if(isRealPlayer(shooter) && isValidTeleport(world, (rayTraceResult).getBlockPos().relative((rayTraceResult).getDirection()))){
            warpEntity(shooter, new BlockPos(vec));
        }
    }

    public static void blinkForward(Level world, LivingEntity shooter, double distance){
        Vec3 lookVec = new Vec3(shooter.getLookAngle().x(), 0, shooter.getLookAngle().z());
        Vec3 vec = shooter.position().add(lookVec.scale(distance));

        BlockPos pos = new BlockPos(vec);
        if (!isValidTeleport(world, pos)){
            pos = getForward(world, pos, shooter, distance) == null ? getForward(world, pos.above(2), shooter, distance) : getForward(world, pos, shooter, distance);
        }
        if(pos == null)
            return;
        warpEntity(shooter, pos);
    }

    public static BlockPos getForward(Level world, BlockPos pos,LivingEntity shooter, double distance){
        Vec3 lookVec = new Vec3(shooter.getLookAngle().x(), 0, shooter.getLookAngle().z());
        Vec3 oldVec = new Vec3(pos.getX(), pos.getY(), pos.getZ()).add(lookVec.scale(distance));
        Vec3 vec;
        BlockPos sendPos;
        for(double i = distance; i >= 0; i--){
            vec = oldVec.add(lookVec.scale(i));
            sendPos = new BlockPos(vec);

            if(i <= 0){
                return null;
            }
            if (isValidTeleport(world, sendPos)){
                return sendPos;
            }

        }
        return null;
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addGenericInt(builder, 8, "Base teleport distance", "distance");
        addAmpConfig(builder, 3.0);
    }

    @Override
    public boolean dampenIsAllowed() {
        return true;
    }

    /**
     * Checks is a player can be placed at a given position without suffocating.
     */
    public static boolean isValidTeleport(Level world, BlockPos pos){
        return !world.getBlockState(pos).canOcclude() &&  !world.getBlockState(pos.above()).canOcclude() && !world.getBlockState(pos.above(2)).canOcclude();
    }

    @Override
    public int getManaCost() {
        return 50;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.ENDER_PEARL;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Teleports the caster to a location. If an entity is hit and the caster is holding a Warp Scroll in the offhand, the entity will be warped to the location on the Warp Scroll. When used on Self, the caster blinks forward. Spell Turrets and Runes can warp entities using Warp Scrolls from adjacent inventories without consuming the scroll.";
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.MANIPULATION);
    }
}
