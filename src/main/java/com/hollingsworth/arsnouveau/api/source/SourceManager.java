package com.hollingsworth.arsnouveau.api.source;

import com.hollingsworth.arsnouveau.ArsNouveau;
import com.hollingsworth.arsnouveau.common.capability.SourceStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.neoforged.neoforge.event.tick.LevelTickEvent;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = ArsNouveau.MODID)
public class SourceManager {
    private Map<String, Set<ISpecialSourceProvider>> posMap = new ConcurrentHashMap<>();

    public void addInterface(Level world, ISpecialSourceProvider pos) {
        String key = world.dimension().location().toString();
        if (!posMap.containsKey(key))
            posMap.put(key, new HashSet<>());

        posMap.get(key).add(pos);
    }

    public @NotNull Set<ISpecialSourceProvider> getSetForLevel(Level world){
        String key = world.dimension().location().toString();
        return posMap.computeIfAbsent(key, k -> new HashSet<>());
    }

    public Set<ISpecialSourceProvider> getCopySetForLevel(Level world){
        return new HashSet<>(getSetForLevel(world));
    }

    @Nullable
    public ISpecialSourceProvider takeSourceNearby(BlockPos pos, Level world, int range, int amount){
        for(ISpecialSourceProvider sourceInterface : getCopySetForLevel(world)){
            if(sourceInterface.isValid() && sourceInterface.getCurrentPos().closerThan(pos, range)){
                ISourceCap storage = sourceInterface.getCapability();
                if (storage.canProvideSource(amount)) {
                    sourceInterface.getCapability().extractSource(amount, false);
                    return sourceInterface;
                }
            }
        }
        return null;
    }

    @Nullable
    public ISpecialSourceProvider hasSourceNearby(BlockPos pos, Level world, int range, int amount){
        for(ISpecialSourceProvider sourceInterface : getCopySetForLevel(world)){
            if(sourceInterface.isValid() && sourceInterface.getCurrentPos().closerThan(pos, range) && sourceInterface.getCapability().getSource() >= amount){
                return sourceInterface;
            }
        }
        return null;
    }

    public List<ISpecialSourceProvider> canGiveSourceNearby(BlockPos pos, Level world, int range){
        List<ISpecialSourceProvider> list = new ArrayList<>();
        for(ISpecialSourceProvider sourceInterface : getCopySetForLevel(world)){
            if(sourceInterface.isValid() && sourceInterface.getCurrentPos().closerThan(pos, range) && sourceInterface.getCapability().canAcceptSource(1)){
                list.add(sourceInterface);
            }
        }
        return list;
    }

    public List<ISpecialSourceProvider> canTakeSourceNearby(BlockPos pos, Level world, int range){
        List<ISpecialSourceProvider> list = new ArrayList<>();
        for(ISpecialSourceProvider sourceInterface : getCopySetForLevel(world)){
            if(sourceInterface.isValid() && sourceInterface.getCurrentPos().closerThan(pos, range) && sourceInterface.getCapability().getSource() >= 0){
                list.add(sourceInterface);
            }
        }
        return list;
    }

    public void tick(Level level){
        if(level.getGameTime() % 60 == 0){
            Set<ISpecialSourceProvider> stale = new HashSet<>();
            for(ISpecialSourceProvider iSourceInterface : getSetForLevel(level)){
                if(!iSourceInterface.isValid()){
                    stale.add(iSourceInterface);
                }
            }
            Set<ISpecialSourceProvider> set = getSetForLevel(level);
            for(ISpecialSourceProvider iSourceInterface : stale){
                set.remove(iSourceInterface);
            }
        }
    }

    public static SourceManager INSTANCE = new SourceManager();

    private SourceManager() {}


    @SubscribeEvent
    public static void serverTick(LevelTickEvent.Post e) {

        if (e.getLevel().isClientSide)
            return;

        INSTANCE.tick(e.getLevel());
    }
}
