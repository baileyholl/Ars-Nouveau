package com.hollingsworth.arsnouveau.common.entity.debug;

import com.hollingsworth.arsnouveau.setup.config.Config;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;

import java.io.PrintWriter;

public class EntityDebugger implements IDebugger{
    public FixedStack<EntityEvent> events = new FixedStack<>(Config.MAX_LOG_EVENTS.get());

    public final Entity entity;

    public EntityDebugger(Entity entity) {
        this.entity = entity;
    }

    @Override
    public void addEntityEvent(DebugEvent event, boolean storeDuplicate) {
        // Do not store duplicate events back to back with the same ID
        if(storeDuplicate || events.isEmpty() || !events.peek().id.equals(event.id)){
            events.push(new EntityEvent(entity, event.id, event.message));
        }
    }

    @Override
    public void writeFile(PrintWriter writer) {
        writer.print("Entity: " + " (" + entity.getClass().getSimpleName() + ")");
        // print current entity goal
        if(entity instanceof Mob mob){
            for(WrappedGoal goal : mob.goalSelector.availableGoals.stream().filter(WrappedGoal::isRunning).toList()){
                writer.println("Running Goal: " + goal.getGoal().getClass().getSimpleName());
            }
        }
        for(EntityEvent event : events){
            writer.println(event.toString());
        }
    }
}
