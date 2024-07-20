package com.hollingsworth.arsnouveau.setup.config;

//
//public class ANModConfig extends ModConfig {
//    private static final ANConfigFileTypeHandler AN_TOML = new ANConfigFileTypeHandler();
//
//    public ANModConfig(ModConfig.Type type, IConfigSpec<?> iConfigSpec, ModContainer container, String fileName) {
//        super(type, iConfigSpec, container, fileName + ".toml");
//
//    }
////    @Override
////    public ConfigFileTypeHandler getHandler() {
////        return AN_TOML;
////    }
//
//    private static class ANConfigFileTypeHandler extends ConfigFileTypeHandler {
//
//        private static Path getPath(Path configBasePath) {
//            //Intercept server config path reading for ArsNouveau configs and reroute it to the normal config directory
//            if (configBasePath.endsWith("serverconfig")) {
//                return FMLPaths.CONFIGDIR.get();
//            }
//            return configBasePath;
//        }
//
//        @Override
//        public Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
//            return super.reader(getPath(configBasePath));
//        }
//
////        @Override
////        public void unload(Path configBasePath, ModConfig config) {
////            super.unload(getPath(configBasePath), config);
////        }
//    }
//}
