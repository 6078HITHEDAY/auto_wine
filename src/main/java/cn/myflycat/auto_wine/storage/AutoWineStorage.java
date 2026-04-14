package cn.myflycat.auto_wine.storage;

import java.util.LinkedHashMap;
import java.util.Map;

public class AutoWineStorage {

    public static final int CURRENT_VERSION = 1;

    private int version = CURRENT_VERSION;
    private Map<String, String> values = new LinkedHashMap<>();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Map<String, String> getValues() {
        if (values == null) {
            values = new LinkedHashMap<>();
        }
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values == null ? new LinkedHashMap<>() : new LinkedHashMap<>(values);
    }

    public String getValue(String key) {
        return getValues().get(key);
    }

    public void setValue(String key, String value) {
        getValues().put(key, value);
    }

    public void removeValue(String key) {
        getValues().remove(key);
    }

    public void normalize() {
        if (version <= 0) {
            version = CURRENT_VERSION;
        }

        if (values == null) {
            values = new LinkedHashMap<>();
        }
    }
}

