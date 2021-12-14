package com.hollingsworth.arsnouveau.common.spell.effect;

import com.hollingsworth.arsnouveau.GlyphLib;
import com.hollingsworth.arsnouveau.api.spell.*;
import com.hollingsworth.arsnouveau.common.entity.EntityDummy;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class EffectSummonDecoy extends AbstractEffect {
    public static EffectSummonDecoy INSTANCE = new EffectSummonDecoy();

    private EffectSummonDecoy() {
        super(GlyphLib.EffectDecoyID, "Summon Decoy");
    }

    @Override
    public void onResolve(HitResult rayTraceResult, Level world, @Nullable LivingEntity shooter, SpellStats spellStats, SpellContext spellContext) {
        if(isRealPlayer(shooter)){
            Vec3 pos = safelyGetHitPos(rayTraceResult);
            EntityDummy dummy = new EntityDummy(world);
            dummy.ticksLeft = (int) (20 * (GENERIC_INT.get() + spellStats.getDurationMultiplier() * EXTEND_TIME.get()));
            dummy.setPos(pos.x, pos.y + 1, pos.z);
            dummy.setOwnerID(shooter.getUUID());
            summonLivingEntity(rayTraceResult, world, shooter, spellStats, spellContext, dummy);
            world.getEntitiesOfClass(Mob.class, dummy.getBoundingBox().inflate(20, 10, 20)).forEach(l -> l.setTarget(dummy));
        }
    }

    @Override
    public void buildConfig(ForgeConfigSpec.Builder builder) {
        super.buildConfig(builder);
        addExtendTimeConfig(builder, 15);
        addGenericInt(builder, 30, "Base duration in seconds", "duration");
    }

    @Override
    public int getManaCost() {
        return 200;
    }

    @Override
    public Tier getTier() {
        return Tier.THREE;
    }

    @Nonnull
    @Override
    public Set<AbstractAugment> getCompatibleAugments() {
        // SummonEvent captures augments, but no uses of that field were found
        return SUMMON_AUGMENTS;
    }

    @Override
    public String getBookDescription() {
        return "Summons a decoy of yourself. Upon summoning, the decoy will attract any nearby mobs to attack it. Does not apply summoning sickness.";
    }

    @Nullable
    @Override
    public Item getCraftingReagent() {
        return Items.ARMOR_STAND;
    }

    @Nonnull
    @Override
    public Set<SpellSchool> getSchools() {
        return setOf(SpellSchools.CONJURATION);
    }
}
