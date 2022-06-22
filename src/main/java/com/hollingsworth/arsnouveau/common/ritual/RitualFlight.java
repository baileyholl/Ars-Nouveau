package com.hollingsworth.arsnouveau.common.ritual;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.event.FlightRefreshEvent;
import com.hollingsworth.arsnouveau.api.ritual.AbstractRitual;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import com.hollingsworth.arsnouveau.common.block.tile.RitualBrazierTile;
import com.hollingsworth.arsnouveau.common.lib.RitualLib;
import com.hollingsworth.arsnouveau.common.network.Networking;
import com.hollingsworth.arsnouveau.common.network.PacketUpdateFlight;
import com.hollingsworth.arsnouveau.common.potions.ModPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RitualFlight extends AbstractRitual {
    @Override
    protected void tick() {
        if(!getWorld().isClientSide && getWorld().getGameTime() % 20 == 0){
            RitualFlightHandler.activePositions.add(getPos());
        }
    }

    @Override
    public int getManaCost() {
        return 200;
    }
    
    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation(ArsNouveau.MODID, RitualLib.FLIGHT);
    }

    @Override
    public String getLangDescription() {
        return "Grants nearby players the Flight effect when they jump, allowing them to creatively fly for a short time. If the player is nearby, this ritual will refresh their flight buff. Each time this ritual grants or refreshes flight, it will expend source from nearby jars.";
    }

    @Override
    public String getLangName() {
        return "Flight";
    }

    @Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
    public static class RitualFlightHandler{
        public static Set<BlockPos> activePositions = new HashSet<>();

        public static @Nullable RitualFlight getFlightRitual(Level world, BlockPos pos){
            BlockEntity entity = world.getBlockEntity(pos);
            if(entity instanceof RitualBrazierTile){
                if(((RitualBrazierTile) entity).ritual instanceof RitualFlight)
                    return (RitualFlight) ((RitualBrazierTile) entity).ritual;
            }
            return null;
        }

        public static void grantFlight(LivingEntity entity){
            BlockPos pos = getValidPosition(entity.level, entity.blockPosition());
            if(pos == null)
                return;
            BlockEntity tileEntity = entity.level.getBlockEntity(pos);
            if(tileEntity instanceof RitualBrazierTile){
                if(((RitualBrazierTile) tileEntity).ritual instanceof RitualFlight) {
                    ((RitualBrazierTile) tileEntity).ritual.setNeedsMana(true);
                    entity.addEffect(new MobEffectInstance(ModPotions.FLIGHT_EFFECT.get(), 90 * 20));
                }
            }
        }

        public static BlockPos getValidPosition(Level world, BlockPos fromPos){
            List<BlockPos> stalePositions = new ArrayList<>();
            BlockPos foundPos = null;
            for(BlockPos p : activePositions){
                if(BlockUtil.distanceFrom(p, fromPos) <= 60){
                    RitualFlight ritualFlight = getFlightRitual(world, p);
                    if(ritualFlight == null){
                        stalePositions.add(p);
                        continue;
                    }
                    if(!ritualFlight.needsManaNow()) {
                        foundPos = p;
                        break;
                    }
                }
            }
            stalePositions.forEach(activePositions::remove);
            return foundPos;
        }

        public static @Nullable BlockPos canPlayerStillFly(LivingEntity entity){
            return getValidPosition(entity.level, entity.blockPosition());
        }

        @SubscribeEvent
        public static void refreshFlight(FlightRefreshEvent e){
            if(!e.getEntityLiving().level.isClientSide) {
                BlockPos validPos = canPlayerStillFly(e.getEntityLiving());
                boolean wasFlying = e.getPlayer().abilities.flying;
                if (validPos != null && wasFlying) {
                    e.getEntityLiving().addEffect(new MobEffectInstance(ModPotions.FLIGHT_EFFECT.get(), 60 * 20));
                    e.getPlayer().abilities.mayfly = true;
                    e.getPlayer().abilities.flying = wasFlying;
                    Networking.sendToPlayer(new PacketUpdateFlight(true, wasFlying), e.getPlayer());
                    BlockEntity tile = e.getPlayer().level.getBlockEntity(validPos);
                    if(tile instanceof RitualBrazierTile && ((RitualBrazierTile) tile).ritual instanceof RitualFlight){
                        ((RitualBrazierTile) tile).ritual.setNeedsMana(true);
                    }
                }
            }
        }
    }
}
