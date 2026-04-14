package cn.myflycat.auto_wine.client;

import net.fabricmc.api.ClientModInitializer;

public class Auto_wineClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoWineClientFramework.initialize();
    }
}
