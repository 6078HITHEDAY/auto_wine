# auto_wine 开发计划

> 记录项目当前进度、已完成工作和后续开发任务。此文件会随着实现进度持续更新。

## 1. 已完成

- [x] 搭建 Fabric 客户端模组基础骨架
  - `src/main/java/cn/myflycat/auto_wine/Auto_wine.java`
  - `src/client/java/cn/myflycat/auto_wine/client/Auto_wineClient.java`
- [x] 增加公共初始化框架 `AutoWineFramework`
- [x] 增加客户端初始化框架 `AutoWineClientFramework`
- [x] 增加功能模块接口与注册表
  - `feature/AutoWineFeature`
  - `feature/AutoWineFeatureRegistry`
- [x] 补充 `README.md`
- [x] 补充 `fabric.mod.json` 的基础描述信息
- [x] 实现决策系统骨架（状态机 + 执行层抽象）
  - `decision/DecisionState`（ACTIVE / DEGRADED / PENDING_RESUME）
  - `decision/ExecutionLayer`（Baritone 执行层接口）
  - `decision/DecisionGoal` / `DecisionPlanner` / `DecisionPlan` / `DecisionAction`
  - `decision/DecisionGoalRegistry`、`DefaultDecisionPlanner`
  - `client/decision/DecisionRuntime`（降级/探测/任务边界恢复）
  - `client/decision/goal/ContainerSnapshotGoal`、`IdleWatchGoal`
  - `client/AutoWineDecisionFeature`（已注册到客户端框架，接入 tick 驱动）

## 2. 当前基线

- 当前项目处于“基础框架 + 任务规划”阶段
- 已完成容器预览扫描第一版，实现了当前 `HandledScreen` 的槽位快照读取
- `mixin` 配置已存在，但尚未注入实际逻辑
- 数据生成入口已存在，但还没有实际的生成内容
- 目前最适合的开发顺序是：
  1. 物品/容器扫描
  2. 配方数据模型
  3. 配方匹配引擎
  4. 自动化操作
  5. 安全保护与测试

## 3. 阶段任务

### 阶段 A：基础设施完善

- [x] 添加配置系统
- [x] 添加持久化存储方案
- [x] 明确模块分层与目录约定
- [x] 统一功能模块的注册、启停与生命周期
- [x] 为调试功能增加开关
- [x] 决策系统三层模型（扫描 / 决策 / 执行）骨架落地
- [x] 执行层抽象（`ExecutionLayer`）与降级/恢复状态机（`DecisionRuntime`）

### 阶段 B：物品 / 容器扫描

- [x] 实现容器内容快照
- [x] 实现槽位与物品堆叠读取
- [x] 支持背包、箱子等常见容器
- [x] 标准化扫描结果数据结构
- [x] 预留 NBT / 元数据提取能力

### 阶段 C：配方数据模型

- [ ] 设计配方数据结构
- [ ] 支持材料、数量、标签、优先级等字段
- [ ] 支持输入 / 输出的统一表达
- [ ] 为本地缓存和导入导出预留接口
- [ ] 为数据生成器预留输出结构

### 阶段 D：配方匹配引擎

- [ ] 实现基础匹配逻辑
- [ ] 支持完全匹配与部分匹配
- [ ] 支持缺料提示
- [ ] 支持候选配方排序
- [ ] 为模糊匹配和替代材料保留扩展点
- [ ] **验收点（降级兼容）**：匹配逻辑在 `DecisionState.DEGRADED` 下可独立运行，不依赖执行层

### 阶段 E：自动化操作

- [ ] 设计客户端交互执行层
- [ ] 实现点击 / 拖拽 / 放置 / 收取等动作封装
- [ ] 将匹配结果转换为操作序列
- [ ] 先实现低风险、可回退的最小闭环流程
- [ ] 预留暂停 / 中止机制
- [ ] 实现 `ExecutionLayer` 接口（Baritone 绑定）
- [ ] **验收点（降级）**：`ExecutionLayer` 抛出首个异常后，`DecisionRuntime` 立即切换为 `DEGRADED`，扫描与本地决策继续运行
- [ ] **验收点（探测）**：降级后每 200 tick 自动探测 `ExecutionLayer.isAvailable()`，成功则进入 `PENDING_RESUME`
- [ ] **验收点（恢复）**：`PENDING_RESUME` 状态下，仅在当前 goal 到达任务边界（`isAtBoundary() == true`）后才切回 `ACTIVE`

### 阶段 F：安全保护

- [ ] 增加自动化总开关
- [ ] 增加速率限制与随机延迟
- [ ] 增加场景校验，避免误操作
- [ ] 增加错误状态下的自动停止机制
- [ ] 避免在高风险界面执行自动化
- [ ] **验收点（风控）**：`DecisionRuntime` 状态机在降级/恢复切换时均记录日志，任何切换不触发中途操作中断

### 阶段 G：测试与验证

- [ ] 为核心数据结构补单元测试
- [ ] 为匹配逻辑补回归测试
- [ ] 为扫描结果补验证用例
- [ ] 为自动化流程补最小可复现测试
- [ ] 每次新增功能后进行回归检查

## 4. 建议推进顺序

1. 先完成容器扫描与物品识别
2. 再完成配方数据模型
3. 然后实现匹配引擎
4. 最后接入自动化操作与安全保护
5. 全程补充测试与回归验证
6. `docs` 采用“先总纲、后细节、边实现边更新”的方式同步推进
   - 现在先维护架构、术语和阶段边界
   - 到阶段 B / C 再补扫描与配方的实现细节
   - 到阶段 D / E / F / G 期间同步完善执行、安全和测试说明

## 5. 备注

- 如果后续出现更多功能模块，可以继续在 `feature` 下拆分子包。
- 如果配置变多，建议单独增加 `config` 包。
- 如果自动化行为复杂，建议把“扫描、决策、执行”拆成三个独立层。

