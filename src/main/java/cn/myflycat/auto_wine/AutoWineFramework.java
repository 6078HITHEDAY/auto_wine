package cn.myflycat.auto_wine;

import com.mojang.logging.LogUtils;
import cn.myflycat.auto_wine.config.AutoWineConfigManager;
import cn.myflycat.auto_wine.feature.AutoWineFeatureRegistry;
import cn.myflycat.auto_wine.storage.AutoWineStorageManager;
import org.slf4j.Logger;

public final class AutoWineFramework {

    public static final String MOD_ID = "auto_wine";
    public static final String MOD_NAME = "auto_wine";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static boolean initialized;

    private AutoWineFramework() {
    }

    public static void initialize() {
        if (initialized) {
            LOGGER.debug("{} framework already initialized", MOD_NAME);
            return;
        }

        initialized = true;
        LOGGER.info("Initializing {} [{}]", MOD_NAME, MOD_ID);

        AutoWineConfigManager.initialize();
        AutoWineStorageManager.initialize();
        AutoWineFeatureRegistry.initializeAll();

        LOGGER.info("Debug mode: {}", AutoWineConfigManager.isDebugEnabled());
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean isDebugEnabled() {
        return AutoWineConfigManager.isDebugEnabled();
    }

    public static void setDebugEnabled(boolean debugEnabled) {
        AutoWineConfigManager.setDebugEnabled(debugEnabled);
        LOGGER.info("Debug mode changed to {}", debugEnabled);
    }

    public static void shutdown() {
        if (!initialized) {
            return;
        }

        AutoWineFeatureRegistry.shutdownAll();
        AutoWineStorageManager.save();
        AutoWineConfigManager.save();
        initialized = false;
    }
}

