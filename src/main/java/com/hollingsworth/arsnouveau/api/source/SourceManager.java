package com.hollingsworth.arsnouveau.api.source;

import com.hollingsworth.arsnouveau.ArsNouveau;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.*;

@Mod.EventBusSubscriber(modid = ArsNouveau.MODID)
public class SourceManager {
    private Map<String, Set<ISpecialSourceProvider>> posMap = new HashMap<>();

    public void addInterface(Level world, ISpecialSourceProvider pos) {
        String key = world.dimension().location().toString();
        if (!posMap.containsKey(key))
            posMap.put(key, new HashSet<>());

        posMap.get(key).add(pos);
    }

    public Set<ISpecialSourceProvider> getSetForLevel(Level world){
        String key = world.dimension().location().toString();
        return posMap.computeIfAbsent(key, k -> new HashSet<>());
    }

    @Nullable
    public ISpecialSourceProvider takeSourceNearby(BlockPos pos, Level world, int range, int amount){
        for(ISpecialSourceProvider sourceInterface : getSetForLevel(world)){
            if(sourceInterface.isValid() && sourceInterface.getCurrentPos().closerThan(pos, range)){
                sourceInterface.getSource().removeSource(amount);
                return sourceInterface;
            }
        }
        return null;
    }

    @Nullable
    public ISpecialSourceProvider hasSourceNearby(BlockPos pos, Level world, int range, int amount){
        for(ISpecialSourceProvider sourceInterface : getSetForLevel(world)){
            if(sourceInterface.isValid() && sourceInterface.getCurrentPos().closerThan(pos, range) && sourceInterface.getSource().getSource() >= amount){
                return sourceInterface;
            }
        }
        return null;
    }

    public List<ISpecialSourceProvider> canGiveSourceNearby(BlockPos pos, Level world, int range){
        List<ISpecialSourceProvider> list = new ArrayList<>();
        for(ISpecialSourceProvider sourceInterface : getSetForLevel(world)){
            if(sourceInterface.isValid() && sourceInterface.getCurrentPos().closerThan(pos, range) && sourceInterface.getSource().canAcceptSource()){
                list.add(sourceInterface);
            }
        }
        return list;
    }

    public List<ISpecialSourceProvider> canTakeSourceNearby(BlockPos pos, Level world, int range){
        List<ISpecialSourceProvider> list = new ArrayList<>();
        for(ISpecialSourceProvider sourceInterface : getSetForLevel(world)){
            if(sourceInterface.isValid() && sourceInterface.getCurrentPos().closerThan(pos, range) && sourceInterface.getSource().getSource() >= 0){
                list.add(sourceInterface);
            }
        }
        return list;
    }

    public void tick(Level level){
        if(level.getGameTime() % 60 == 0){
            getSetForLevel(level).removeIf(iSourceInterface -> !iSourceInterface.isValid());
        }
    }

    public static SourceManager INSTANCE = new SourceManager();

    private SourceManager() {}


    @SubscribeEvent
    public static void serverTick(TickEvent.LevelTickEvent e) {

        if (e.phase != TickEvent.Phase.END)
            return;

        INSTANCE.tick(e.level);
    }
}
