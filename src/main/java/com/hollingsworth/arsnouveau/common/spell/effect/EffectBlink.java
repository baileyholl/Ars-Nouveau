package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.AbstractAugment;
import com.hollingsworth.arsnouveau.api.spell.AbstractEffect;
import com.hollingsworth.arsnouveau.api.spell.IInventoryResponder;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.common.items.WarpScroll;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketWarpPosition;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentAmplify;
import com.hollingsworth.arsnouveau.common.spell.augment.AugmentDampen;
import com.hollingsworth.arsnouveau.setup.ItemsRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class EffectBlink extends AbstractEffect {
    public static EffectBlink INSTANCE = new EffectBlink();

    private EffectBlink() {
        super(GlyphLib.EffectBlinkID, "Blink");
    }

    @Override
    public void onResolveEntity(EntityRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        Vector3d vec = safelyGetHitPos(rayTraceResult);
        double distance = GENERIC_INT.get() + AMP_VALUE.get() *getAmplificationBonus(augments);

        if(spellContext.castingTile instanceof IInventoryResponder){
            ItemStack scroll = ((IInventoryResponder) spellContext.castingTile).getItem(new ItemStack(ItemsRegistry.warpScroll));
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


        }else if(spellContext.getType() == SpellContext.CasterType.RUNE && ((EntityRayTraceResult) rayTraceResult).getEntity() instanceof LivingEntity){
            blinkForward(world, (LivingEntity) ((EntityRayTraceResult) rayTraceResult).getEntity(), distance);
        }
    }

    public static void warpEntity(Entity entity, BlockPos warpPos){
        if(entity == null)
            return;
        World world = entity.level;
        ((ServerWorld) entity.level).sendParticles(ParticleTypes.PORTAL, entity.getX(),  entity.getY() + 1,  entity.getZ(),
                4,(world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);

        entity.teleportTo(warpPos.getX() +0.5, warpPos.getY(), warpPos.getZ() +0.5);
        Networking.sendToNearby(world, entity, new PacketWarpPosition(entity.getId(), entity.getX(), entity.getY(), entity.getZ()));
        entity.level.playSound(null, warpPos, SoundEvents.ILLUSIONER_MIRROR_MOVE, SoundCategory.NEUTRAL, 1.0f, 1.0f);
        ((ServerWorld) entity.level).sendParticles(ParticleTypes.PORTAL, warpPos.getX() +0.5,  warpPos.getY() + 1.0,  warpPos.getZ() +0.5,
                4,(world.random.nextDouble() - 0.5D) * 2.0D, -world.random.nextDouble(), (world.random.nextDouble() - 0.5D) * 2.0D, 0.1f);
    }

    @Override
    public void onResolveBlock(BlockRayTraceResult rayTraceResult, World world, @Nullable LivingEntity shooter, List<AbstractAugment> augments, SpellContext spellContext) {
        Vector3d vec = rayTraceResult.getLocation();
        if(isRealPlayer(shooter) && isValidTeleport(world, (rayTraceResult).getBlockPos().relative((rayTraceResult).getDirection()))){
            warpEntity(shooter, new BlockPos(vec));
        }
    }

    public static void blinkForward(World world, LivingEntity shooter, double distance){
        Vector3d lookVec = new Vector3d(shooter.getLookAngle().x(), 0, shooter.getLookAngle().z());
        Vector3d vec = shooter.position().add(lookVec.scale(distance));

        BlockPos pos = new BlockPos(vec);
        if (!isValidTeleport(world, pos)){
            pos = getForward(world, pos, shooter, distance) == null ? getForward(world, pos.above(2), shooter, distance) : getForward(world, pos, shooter, distance);
        }
        if(pos == null)
            return;
        warpEntity(shooter, pos);
    }

    public static BlockPos getForward(World world, BlockPos pos,LivingEntity shooter, double distance){
        Vector3d lookVec = new Vector3d(shooter.getLookAngle().x(), 0, shooter.getLookAngle().z());
        Vector3d oldVec = new Vector3d(pos.getX(), pos.getY(), pos.getZ()).add(lookVec.scale(distance));
        Vector3d vec;
        BlockPos sendPos = null;
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
    public static boolean isValidTeleport(World world, BlockPos pos){
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

    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        return augmentSetOf(AugmentAmplify.INSTANCE, AugmentDampen.INSTANCE);
    }

    @Override
    public String getBookDescription() {
        return "Teleports the caster to a location. If an entity is hit and the caster is holding a Warp Scroll in the offhand, the entity will be warped to the location on the Warp Scroll. When used on Self, the caster blinks forward. Spell Turrets and Runes can warp entities using Warp Scrolls from adjacent inventories.";
    }
}
