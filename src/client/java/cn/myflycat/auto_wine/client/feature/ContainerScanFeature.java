package cn.myflycat.auto_wine.client.feature;

import cn.myflycat.auto_wine.AutoWineFramework;
import cn.myflycat.auto_wine.client.scan.ContainerPreviewScanner;
import cn.myflycat.auto_wine.feature.AutoWineFeature;
import cn.myflycat.auto_wine.scan.ContainerScanResult;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

import java.util.Optional;

public final class ContainerScanFeature implements AutoWineFeature {

    public static final String FEATURE_ID = "container_scan";

    private static final ContainerScanFeature INSTANCE = new ContainerScanFeature();

    private volatile boolean active;
    private volatile boolean listenerRegistered;
    private volatile ContainerScanResult latestScan;

    private ContainerScanFeature() {
    }

    public static ContainerScanFeature instance() {
        return INSTANCE;
    }

    public static Optional<ContainerScanResult> getLatestScan() {
        return Optional.ofNullable(INSTANCE.latestScan);
    }

    public static void refreshNow() {
        MinecraftClient client = MinecraftClient.getInstance();
        INSTANCE.latestScan = ContainerPreviewScanner.scanCurrentScreen(client).orElse(null);
    }

    @Override
    public String id() {
        return FEATURE_ID;
    }

    @Override
    public void initialize() {
        active = true;
        if (!listenerRegistered) {
            ClientTickEvents.END_CLIENT_TICK.register(ContainerScanFeature::onClientTick);
            listenerRegistered = true;
        }

        refreshNow();
        AutoWineFramework.LOGGER.info("Container scan feature initialized");
    }

    @Override
    public void shutdown() {
        active = false;
        latestScan = null;
        AutoWineFramework.LOGGER.info("Container scan feature stopped");
    }

    private static void onClientTick(MinecraftClient client) {
        if (!INSTANCE.active) {
            return;
        }

        INSTANCE.latestScan = ContainerPreviewScanner.scanCurrentScreen(client).orElse(null);
    }
}

