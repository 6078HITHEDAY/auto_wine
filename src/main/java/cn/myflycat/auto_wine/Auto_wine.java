package cn.myflycat.auto_wine;

import net.fabricmc.api.ModInitializer;

public class Auto_wine implements ModInitializer {

    @Override
    public void onInitialize() {
        AutoWineFramework.initialize();
    }
}
