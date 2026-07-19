package com.hollingsworth.arsnouveau.api.particle.timelines;

import com.hollingsworth.arsnouveau.api.registry.ParticleTimelineRegistry;
import com.hollingsworth.arsnouveau.api.spell.Spell;
import com.hollingsworth.arsnouveau.api.spell.SpellContext;
import com.hollingsworth.arsnouveau.api.spell.SpellResolver;
import com.hollingsworth.arsnouveau.common.entity.EntityWallSpell;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public final class WallTimelinePreview implements ParticleTimelinePreview {
    private final Vec3 position;
    private final WallTimeline timeline;
    private EntityWallSpell wall;
    private int age;

    public WallTimelinePreview(WallTimeline timeline, Level level, Vec3 origin) {
        this.timeline = timeline;
        this.position = origin.add(0, -1, 0);
        TimelineMap timelineMap = new TimelineMap().put(ParticleTimelineRegistry.WALL_TIMELINE.get(), timeline);
        Spell spell = new Spell().withTimeline(timelineMap);
        wall = new EntityWallSpell(level, position.x, position.y, position.z);
        wall.setDirection(Direction.NORTH);
        wall.setShouldFall(false);
        wall.setLanded(true);
        wall.setResolver(new SpellResolver(new SpellContext(level, spell, null, null)));
        wall.setOldPosAndRot();
    }

    @Override
    public boolean tick(Level level) {
        if (age >= 40) {
            return false;
        }
        wall.playParticles();
        age++;
        if (age == 40) {
            wall.sendResolveParticles();
            timeline.resolveSound.sound.playSound(level, position);
            wall = null;
        }
        return true;
    }

    @Override
    public void renderEntities(EntityRenderCallback callback) {
        if (wall != null) {
            callback.renderEntity(wall);
        }
    }

    @Override
    public float scale() {
        return 10f;
    }

    @Override
    public void renderWorldBlocks(BlockRenderCallback callback) {
        ParticleTimelinePreview.renderGrassField(callback, 1, 5);
        for (int y = -1; y <= 4; y++) {
            for (int z = -5; z <= 5; z++) {
                callback.renderBlock(Blocks.STONE_BRICKS.defaultBlockState(), new BlockPos(1, y, z));
            }
        }
    }
}
