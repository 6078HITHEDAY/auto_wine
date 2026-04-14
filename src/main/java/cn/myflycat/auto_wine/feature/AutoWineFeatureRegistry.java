package cn.myflycat.auto_wine.feature;

import cn.myflycat.auto_wine.AutoWineFramework;
import cn.myflycat.auto_wine.config.AutoWineConfigManager;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AutoWineFeatureRegistry {

    private static final Map<String, AutoWineFeature> FEATURES = new LinkedHashMap<>();
    private static boolean initialized;

    private AutoWineFeatureRegistry() {
    }

    public static synchronized void register(AutoWineFeature feature) {
        String id = validateFeatureId(feature.id());
        AutoWineFeature previous = FEATURES.putIfAbsent(id, feature);
        if (previous != null) {
            throw new IllegalStateException("Duplicate feature id: " + id);
        }

        AutoWineFramework.LOGGER.info("Registered feature: {}", id);

        if (initialized && isEnabled(feature)) {
            initializeFeature(feature);
        }
    }

    public static synchronized Collection<AutoWineFeature> all() {
        return Collections.unmodifiableCollection(FEATURES.values());
    }

    public static synchronized void initializeAll() {
        initialized = true;

        for (AutoWineFeature feature : FEATURES.values()) {
            if (isEnabled(feature)) {
                initializeFeature(feature);
            }
        }
    }

    public static synchronized void shutdownAll() {
        for (AutoWineFeature feature : FEATURES.values()) {
            if (isEnabled(feature)) {
                AutoWineFramework.LOGGER.info("Shutting down feature: {}", feature.id());
                feature.shutdown();
            }
        }

        initialized = false;
    }

    public static synchronized boolean isEnabled(String featureId) {
        AutoWineFeature feature = FEATURES.get(featureId);
        return feature != null && isEnabled(feature);
    }

    public static synchronized void setEnabled(String featureId, boolean enabled) {
        AutoWineFeature feature = FEATURES.get(featureId);
        if (feature == null) {
            throw new IllegalArgumentException("Unknown feature id: " + featureId);
        }

        AutoWineConfigManager.setFeatureEnabled(featureId, enabled);

        if (enabled) {
            initializeFeature(feature);
        } else {
            AutoWineFramework.LOGGER.info("Disabling feature: {}", featureId);
            feature.shutdown();
        }
    }

    public static synchronized AutoWineFeature get(String featureId) {
        return FEATURES.get(featureId);
    }

    private static boolean isEnabled(AutoWineFeature feature) {
        return AutoWineConfigManager.isFeatureEnabled(feature.id(), feature.enabledByDefault());
    }

    private static void initializeFeature(AutoWineFeature feature) {
        AutoWineFramework.LOGGER.info("Initializing feature: {}", feature.id());
        feature.preInitialize();
        feature.initialize();
    }

    private static String validateFeatureId(String id) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Feature id must not be blank");
        }

        return id;
    }
}

