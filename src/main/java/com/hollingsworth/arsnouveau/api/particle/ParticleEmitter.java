package com.hollingsworth.arsnouveau.api.particle;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.function.Supplier;

public class ParticleEmitter {

    public Vec3 offset;
    public Supplier<Vec3> position;
    public IParticleConfig particleConfig;
    public Vec3 previousPosition;
    public int age;
    public Supplier<Vec2> rotation;
    public Vec2 rotationOffset;

    public ParticleEmitter(Supplier<Vec3> getPosition, Supplier<Vec2> rot, IParticleConfig particleConfig){
        this.position = getPosition;
        this.offset = Vec3.ZERO;
        this.particleConfig = particleConfig;
        this.rotation = rot;
        this.rotationOffset = Vec2.ZERO;
        particleConfig.init(this);
    }

    public void setPositionOffset(Vec3 offset){
        this.offset = offset;
    }

    public void setPositionOffset(double x, double y, double z){
        this.offset = new Vec3(x, y, z);
    }

    public Vec3 getPositionOffset(){
        return offset;
    }

    public Vec3 getPosition(){
        return position.get();
    }

    public Vec3 getAdjustedPosition(){
        return position.get().add(offset);
    }

    public Vec2 getRotation(){
        return rotation.get();
    }

    public Vec2 getAdjustedRotation(){
        return rotation.get().add(rotationOffset);
    }

    public void setRotationOffset(Vec2 offset){
        this.rotationOffset = offset;
    }

    public void setRotationOffset(float x, float y){
        this.rotationOffset = new Vec2(x, y);
    }

    public void tick(Level level){
        if(this.previousPosition == null){
            this.previousPosition = this.getAdjustedPosition();
        }
        particleConfig.tick(level, getAdjustedPosition().x, getAdjustedPosition().y, getAdjustedPosition().z, previousPosition.x, previousPosition.y, previousPosition.z);
        this.previousPosition = this.getAdjustedPosition();
        this.age++;
    }


}
