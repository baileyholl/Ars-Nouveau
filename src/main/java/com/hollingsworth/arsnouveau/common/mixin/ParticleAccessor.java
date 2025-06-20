package com.hollingsworth.arsnouveau.common.mixin;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Particle.class)
public interface ParticleAccessor {
    @Invoker
    int callGetLightColor(float partialTick);

    @Invoker
    void callSetLocationFromBoundingbox();

    @Invoker
    void callSetSize(float width, float height);

    @Invoker
    void callSetAlpha(float alpha);

    @Invoker("<init>")
    static Particle createParticle(ClientLevel level, double x, double y, double z) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    boolean isSpeedUpWhenYMotionIsBlocked();

    @Accessor
    void setSpeedUpWhenYMotionIsBlocked(boolean speedUpWhenYMotionIsBlocked);

    @Accessor
    float getFriction();

    @Accessor
    void setFriction(float friction);

    @Accessor
    float getORoll();

    @Accessor
    void setORoll(float oRoll);

    @Accessor
    float getRoll();

    @Accessor
    void setRoll(float roll);

    @Accessor
    float getAlpha();

    @Accessor
    void setAlpha(float alpha);

    @Accessor
    float getBCol();

    @Accessor
    void setBCol(float bCol);

    @Accessor
    float getGCol();

    @Accessor
    void setGCol(float gCol);

    @Accessor
    float getRCol();

    @Accessor
    void setRCol(float rCol);

    @Accessor
    float getGravity();

    @Accessor
    void setGravity(float gravity);

    @Accessor
    int getLifetime();

    @Accessor
    void setLifetime(int lifetime);

    @Accessor
    int getAge();

    @Accessor
    void setAge(int age);

    @Accessor
    RandomSource getRandom();

    @Mutable
    @Accessor
    void setRandom(RandomSource random);

    @Accessor
    float getBbHeight();

    @Accessor
    void setBbHeight(float bbHeight);

    @Accessor
    float getBbWidth();

    @Accessor
    void setBbWidth(float bbWidth);

    @Accessor
    boolean isRemoved();

    @Accessor
    void setRemoved(boolean removed);

    @Accessor
    boolean isStoppedByCollision();

    @Accessor
    void setStoppedByCollision(boolean stoppedByCollision);

    @Accessor
    boolean isHasPhysics();

    @Accessor
    void setHasPhysics(boolean hasPhysics);

    @Accessor
    boolean isOnGround();

    @Accessor
    void setOnGround(boolean onGround);

    @Accessor
    AABB getBb();

    @Accessor
    void setBb(AABB bb);

    @Accessor
    double getZd();

    @Accessor
    void setZd(double zd);

    @Accessor
    double getYd();

    @Accessor
    void setYd(double yd);

    @Accessor
    double getXd();

    @Accessor
    void setXd(double xd);

    @Accessor
    double getZ();

    @Accessor
    void setZ(double z);

    @Accessor
    double getY();

    @Accessor
    void setY(double y);

    @Accessor
    double getX();

    @Accessor
    void setX(double x);

    @Accessor
    double getZo();

    @Accessor
    void setZo(double zo);

    @Accessor
    double getYo();

    @Accessor
    void setYo(double yo);

    @Accessor
    double getXo();

    @Accessor
    void setXo(double xo);

    @Accessor
    ClientLevel getLevel();

    @Mutable
    @Accessor
    void setLevel(ClientLevel level);

    @Accessor
    static double getMAXIMUM_COLLISION_VELOCITY_SQUARED() {
        throw new UnsupportedOperationException();
    }

    @Mutable
    @Accessor
    static void setMAXIMUM_COLLISION_VELOCITY_SQUARED(double MAXIMUM_COLLISION_VELOCITY_SQUARED) {
        throw new UnsupportedOperationException();
    }

    @Accessor
    static AABB getINITIAL_AABB() {
        throw new UnsupportedOperationException();
    }

    @Mutable
    @Accessor
    static void setINITIAL_AABB(AABB INITIAL_AABB) {
        throw new UnsupportedOperationException();
    }
}
