Repository for the Ars Nouveau mod.

https://www.arsnouveau.wiki/

For more information or to download: https://www.curseforge.com/minecraft/mc-mods/ars-nouveau

For developing an addon, consult this example addon project! https://github.com/baileyholl/Ars-Nouveau-Example-Addon

License: Textures are models are All Rights Reserved unless otherwise given permission. Addon authors are encouraged to reuse Ars Nouveau assets and to reach out in the discord. 

This code of this mod is licensed under LGPL V3.
Every push to this repository is built and published to the [BlameJared](https://maven.blamejared.com) maven, to use
these builds in your project, simply add the following code in your build.gradle

```gradle
repositories {
    maven { url 'https://maven.blamejared.com' }
}

dependencies {

    // Ars Nouveau
    implementation "com.hollingsworth.ars_nouveau:ars_nouveau-[MC_VERSION]:[ARS_VERSION]"

    // Ars dependencies, should be automatically downloaded by gradle but include them if they don't
    implementation "software.bernie.geckolib:geckolib-neoforge-[MC_VERSION]:[GECKOLIB_VERSION]"
    implementation 'top.theillusivec4.curios:curios-neoforge:[CURIO_VERSION]'

}
```

Current version (1.21.1):
[![Maven](https://img.shields.io/maven-metadata/v?label=&color=C71A36&metadataUrl=https%3A%2F%2Fmaven.blamejared.com%2Fcom%2Fhollingsworth%2Fars_nouveau%2Fars_nouveau-1.21.1%2Fmaven-metadata.xml&style=flat-square)](https://maven.blamejared.com/com/hollingsworth/ars_nouveau/ars_nouveau-1.21.1/)

(remove the v)
