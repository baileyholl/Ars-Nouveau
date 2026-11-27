package com.hollingsworth.arsnouveau.setup;

import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.transformer.IMixinTransformer;
import org.spongepowered.tools.agent.MixinAgent;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.security.ProtectionDomain;

// Fixes intellij hotswap not working when addons mixin into ars classes
public class MixinHotSwap implements ClassFileTransformer {
    private final IMixinTransformer mixins;

    private MixinHotSwap(IMixinTransformer mixins) {
        this.mixins = mixins;
    }

    public static void register() {
        try {
            Field field = ClassLoader.getSystemClassLoader().loadClass("org.spongepowered.tools.agent.MixinAgent").getDeclaredField("instrumentation");
            field.setAccessible(true);
            if (field.get(null) instanceof Instrumentation instrumentation && MixinEnvironment.getCurrentEnvironment().getActiveTransformer() instanceof IMixinTransformer mixins) {
                MixinAgent.init(instrumentation);
                instrumentation.addTransformer(new MixinHotSwap(mixins));
            }
        } catch (ReflectiveOperationException ignored) {
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        if (classBeingRedefined == null) {
            return null;
        }
        try {
            return mixins.transformClassBytes(null, className.replace('/', '.'), classfileBuffer);
        } catch (Throwable t) {
            return null;
        }
    }
}
