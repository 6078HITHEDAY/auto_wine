package cn.myflycat.auto_wine.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class AutoWineConfig {

    public static final int CURRENT_VERSION = 1;

    private int version = CURRENT_VERSION;
    private boolean debugEnabled = false;
    private Map<String, Boolean> featureEnabled = new LinkedHashMap<>();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isDebugEnabled() {
        return debugEnabled;
    }

    public void setDebugEnabled(boolean debugEnabled) {
        this.debugEnabled = debugEnabled;
    }

    public Map<String, Boolean> getFeatureEnabled() {
        if (featureEnabled == null) {
            featureEnabled = new LinkedHashMap<>();
        }
        return featureEnabled;
    }

    public void setFeatureEnabled(Map<String, Boolean> featureEnabled) {
        this.featureEnabled = featureEnabled == null ? new LinkedHashMap<>() : new LinkedHashMap<>(featureEnabled);
    }

    public boolean isFeatureEnabled(String featureId, boolean defaultValue) {
        return getFeatureEnabled().getOrDefault(featureId, defaultValue);
    }

    public void setFeatureEnabled(String featureId, boolean enabled) {
        getFeatureEnabled().put(featureId, enabled);
    }

    public void removeFeatureEnabled(String featureId) {
        getFeatureEnabled().remove(featureId);
    }

    public void normalize() {
        if (version <= 0) {
            version = CURRENT_VERSION;
        }

        if (featureEnabled == null) {
            featureEnabled = new LinkedHashMap<>();
        }
    }
}

