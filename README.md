# auto_wine

# 第一个mc的项目，感觉方向就是错的，开了，半吊子，纪念吧
`auto_wine` 是一个基于 **Fabric** 的 Minecraft 客户端模组工程骨架，当前目标是先搭建一个干净、可扩展的基础框架，方便后续继续开发：

- 容器/箱子内容识别
- 配方记忆与匹配
- 客户端自动化交互
- 数据生成与资源管理

## 当前状态

这个仓库目前已经具备最小可运行的 Fabric Client Mod 结构：

- `main` 入口：`cn.myflycat.auto_wine.Auto_wine`
- `client` 入口：`cn.myflycat.auto_wine.client.Auto_wineClient`
- 数据生成入口：`cn.myflycat.auto_wine.client.Auto_wineDataGenerator`
- 容器预览扫描：`cn.myflycat.auto_wine.client.feature.ContainerScanFeature`
- 扫描数据模型：`cn.myflycat.auto_wine.scan`
- Mixins 配置：
  - `src/main/resources/auto_wine.mixins.json`
  - `src/client/resources/auto_wine.client.mixins.json`

目前已经具备容器 / 背包预览扫描的第一版能力，后续可以直接接入配方识别与自动化执行。

## 开发环境

根据当前工程配置：

- Minecraft：`1.20.4`
- Java：`17`
- Fabric Loader：`0.19.1`
- Fabric API：`0.97.3+1.20.4`
- Yarn：`1.20.4+build.3`

## 运行方式

如果你本机已经安装了 `gradle`，可以在项目根目录执行：

```bash
gradle runClient
```

构建成可分发产物：

```bash
gradle build
```

> 当前仓库里还没有提交 `gradlew` / `gradlew.bat`，所以上面的命令基于本机已安装 Gradle 的情况。如果你后续补了 wrapper，也可以直接改回 `./gradlew`。

## 工程结构

```text
src/
├── main/
│   ├── java/cn/myflycat/auto_wine/
│   │   ├── Auto_wine.java
│   │   ├── AutoWineFramework.java
│   │   ├── config/
│   │   ├── feature/
│   │   ├── scan/
│   │   └── storage/
│   └── resources/
│       ├── fabric.mod.json
│       └── auto_wine.mixins.json
└── client/
    ├── java/cn/myflycat/auto_wine/client/
    │   ├── Auto_wineClient.java
    │   ├── Auto_wineDataGenerator.java
    │   ├── AutoWineClientFramework.java
    │   ├── feature/
    │   │   └── ContainerScanFeature.java
    │   └── scan/
    │       └── ContainerPreviewScanner.java
    └── resources/
        └── auto_wine.client.mixins.json
```

## 基础框架约定

### 1. 公共初始化层

`AutoWineFramework` 负责：

- 模组公共常量
- 日志输出
- 公共启动流程
- 未来功能模块注册与初始化

### 2. 客户端初始化层

`AutoWineClientFramework` 负责：

- 客户端专属初始化
- 客户端状态与开关
- 后续 GUI / 热键 / 渲染相关逻辑的入口

### 3. 功能模块层

`feature` 包下用于存放后续模块，例如：

- 容器扫描
- 配方匹配
- 自动点击
- 物品识别
- 配置读取

其中容器扫描的第一版已经实现，会在客户端打开 `HandledScreen` 时基于屏幕预览快照读取槽位、堆叠数量和基础 NBT 字符串。

建议每个功能模块都实现统一接口，便于统一注册和启停管理。

## 后续开发建议

如果你接下来要继续做“箱子识别 + 配方管理 + 自动化执行”，建议按下面顺序推进：

1. 先做物品识别与容器扫描
2. 再做本地配方数据结构
3. 然后做匹配引擎
4. 最后再接自动化点击与安全保护开关

## 说明

- 当前 `icon.png` 还是占位引用；如果你还没放图标，后续需要补一个文件到 `src/main/resources/assets/auto_wine/icon.png`
- `fabric.mod.json` 已经配置为客户端环境
- 如果之后要增加配置系统，建议优先补一个独立的 `config` 包

