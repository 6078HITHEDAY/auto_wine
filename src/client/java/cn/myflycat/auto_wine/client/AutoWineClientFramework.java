package cn.myflycat.auto_wine.client;

import cn.myflycat.auto_wine.AutoWineFramework;
import cn.myflycat.auto_wine.client.feature.ContainerScanFeature;
import cn.myflycat.auto_wine.feature.AutoWineFeatureRegistry;

public final class AutoWineClientFramework {

    private static boolean initialized;

    private AutoWineClientFramework() {
    }

    public static void initialize() {
        AutoWineFramework.initialize();

        if (initialized) {
            AutoWineFramework.LOGGER.debug("Client framework already initialized");
            return;
        }

        initialized = true;
        AutoWineFramework.LOGGER.info("Initializing client framework for {}", AutoWineFramework.MOD_ID);

        if (AutoWineFeatureRegistry.get(ContainerScanFeature.FEATURE_ID) == null) {
            AutoWineFeatureRegistry.register(ContainerScanFeature.instance());
        }

        if (AutoWineFeatureRegistry.get(AutoWineDecisionFeature.FEATURE_ID) == null) {
            AutoWineFeatureRegistry.register(AutoWineDecisionFeature.instance());
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }
}


