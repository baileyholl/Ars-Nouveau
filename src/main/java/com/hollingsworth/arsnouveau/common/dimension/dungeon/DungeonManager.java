package com.hollingsworth.arsnouveau.common.dimension.dungeon;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.api.util.BlockUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class DungeonManager extends WorldSavedData {
    public static final String ID = "an_dungeon";
    public static BlockPos HOME_POS = new BlockPos(0, 101, 0);
    public DungeonEvent event;
    private ServerWorld world;

    public void tick(){
        if(world.getGameTime() % 100 == 0){
            trackPlayers();
        }
        tickDungeonEvent();
    }

    public void tickDungeonEvent(){
        if(event == null)
            return;
        event.tick();
        if(world.getGameTime() % 200 == 0)
            setDirty();
    }


    public void trackPlayers(){
        for(Entity e : world.getAllEntities()){
            if(e instanceof PlayerEntity){
                if(BlockUtil.distanceFrom(e.blockPosition(), BlockPos.ZERO.above(100)) > 300){
                    e.teleportTo(0, 101, 0);
                }
            }
        }
    }


    private DungeonManager(ServerWorld world){
        super(ID);
        this.world = world;
        setDirty();
    }


    @Override
    public void load(CompoundNBT tag) {
        System.out.println("LOAD");
        System.out.println(tag.toString());
        this.event = tag.contains("event") ? new DungeonEvent(world, tag.getCompound("event")) : new DungeonEvent(world);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        if(event != null)
            tag.put("event", event.save(new CompoundNBT()));
        return tag;
    }

    public DungeonEvent getEvent(){
        if(event == null)
            event = new DungeonEvent(world);
        return event;
    }

    public static DungeonManager from(ServerWorld world) {
        return world.getServer().getLevel(world.dimension())
                .getDataStorage()
                .computeIfAbsent(() -> new DungeonManager(world), DungeonManager.ID);
    }

    @SubscribeEvent
    public static void serverLoad(WorldEvent.Load loadEvent){
        if(!loadEvent.getWorld().isClientSide()){
            ServerWorld world = (ServerWorld) loadEvent.getWorld();
            if(world.dimension().location().toString().equals("ars_nouveau:dungeon")){
                canTrack = true;
            }
        }
    }
    public static boolean canTrack = false;
    @SubscribeEvent
    public static void serverTick(TickEvent.WorldTickEvent e) {
        if (e.phase != TickEvent.Phase.END || !(e.world instanceof ServerWorld) || !e.world.dimension().location().toString().equals("ars_nouveau:dungeon"))
            return;
        if(canTrack)
            from((ServerWorld) e.world).tick();
    }

    @SubscribeEvent
    public static void entityLoad(EntityJoinWorldEvent e) {
        if (e.getWorld().isClientSide || !e.getWorld().dimension().location().toString().equals("ars_nouveau:dungeon"))
            return;
        if(!e.getEntity().getPersistentData().contains("an_dungeon") || !(e.getEntity() instanceof LivingEntity))
            return;

        if(canTrack)
            from((ServerWorld) e.getWorld()).getEvent().addAttacker((LivingEntity) e.getEntity(), false);
    }
}