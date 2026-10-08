# AGENTS.md

Rebar 附属插件模板（Kotlin / Paper 26.2 / Java 25）。**这个文件是给 AI 助手和未来的自己看的工作手册：先读这里，再去翻源码。**

## 这是什么

一个可以直接开写的 Rebar 附属骨架：构建配置、依赖、目录约定、示例物品/方块、语言与设置的加载方式都配好了。
Rebar 和 Paper API 都是 `compileOnly` —— 打出来的 jar 只能跑在**装了 Rebar 的服务器**上，不要试图独立运行。

复制模板后要改：
1. `settings.gradle.kts` 的 `rootProject.name`
2. `gradle.properties` 的 `group` / `main-class` / `version`
3. 包名，以及 `TemplateKeys.NAMESPACE` 与 `TemplateAddon.getKey()`
4. `lang/` 与 `settings/` 里的内容
5. `README.md` 换成这个附属自己的说明

## 构建 / 验证

| 目的 | 命令 |
|---|---|
| 编译 + 单测 + 打包 | `./gradlew build` → `build/libs/<项目名>-<版本>.jar` |
| 只编译（快） | `./gradlew compileKotlin` |
| 只跑测试 | `./gradlew test` |

- `gradle.properties` 里写死了本机 `org.gradle.java.home=D:\Java\zulu25`。换机器要么改这里，要么删掉让它用 `JAVA_HOME`。
  CI（`.github/workflows/build.yml`）会自动删掉这一行，所以别依赖它。
- 源码、资源、语言文件统一 UTF-8（`gradle.properties` 已强制），**别改编码相关配置**。

## 怎么查 Rebar / Pylon 源码（重点）

Rebar 是 `compileOnly` 依赖，**没有本地源码**，遇到不确定的 API 必须自己查。按效率排序：

**1. 直接查 GitHub（最快，优先用这个）**

```bash
# 搜某个类/方法在哪个文件
gh search code "SimpleElectricRebarBlock" --repo pylonmc/rebar
# 列出整个仓库的文件树，再 grep 关键字
gh api "repos/pylonmc/pylon/git/trees/HEAD?recursive=1" --jq '.tree[].path' | grep -i wire
# 直接读某个文件
gh api "repos/pylonmc/pylon/contents/<路径>" --jq '.content' | base64 -d
```

- 框架本体：`pylonmc/rebar`
- 官方内容实现（**最佳写法参考**）：`pylonmc/pylon` —— 电炉、发电机、电容、导线、多方块都在这里

**2. 本地 gradle 缓存里的 jar（看真实签名和字节码）**

```
~/.gradle/caches/modules-2/files-2.1/io.github.pylonmc/rebar/<版本>/<hash>/rebar-<版本>.jar
```

解包后用 `javap`：

```bash
D:/Java/zulu25/bin/javap.exe -p <类>        # 看 API 签名
D:/Java/zulu25/bin/javap.exe -p -c <类>     # 看字节码：常量、默认值、调用顺序都在这
D:/Java/zulu25/bin/javap.exe -v -p <类>     # 常量池里的字符串（找配置路径、节点名特别有用）
```

**3. Maven Central 直接下 jar**（缓存里没有时）

```
https://repo1.maven.org/maven2/io/github/pylonmc/rebar/<版本>/rebar-<版本>.jar
```

**4. 原版 Minecraft 代码**（要抄原版机制时，比如方块行为、结构图案）

```
~/.gradle/caches/fabric-loom/<MC版本>/minecraft-merged.jar
```

同样用 `javap` 反查。本机还有 paperweight 的反编译产物可看。

**5. ⚠️ 仓库里的 `参考/` 目录**（如果有）只是某个时间点的源码副本，可能落后于 gradle 依赖。
**API 一律以 `gradle.properties` 里的 rebar 版本为准。**

## 这个生态里踩过的坑（写代码前扫一眼）

- **`RebarUtils` 是 Kotlin 文件门面**（`@file:JvmName`）：Kotlin 里不能写 `RebarUtils.XXX`，
  要么 import 顶层名字，要么自己定义常量。
- **展示实体的坐标是相对方块中心的**（方块表面在 ±0.5）：贴面实体（面板、端口、贴图）的外表面必须
  **凸出 0.5 一点点**，否则会被不透明的方块本体完全挡住、根本看不见；但也别凸太多（看起来像浮板）。
  - 例：Rebar 的电力端口 `ElectricPortEntity` 化简后 **外表面 ≈ radius**（相对方块中心），
    所以 `createSimpleElectricPort(type, face)` 的默认值 0.5 才是"贴着方块表面"。
- **Rebar 方块本体就是注册时给的那个原版方块**，原版机制会认它：例如"铁块"会被原版铁傀儡图案吃掉。
  想避免就自己在事件里拦。
- **电力（`SimpleElectricRebarBlock`）**：
  - 节点名约定 `producer_0` / `consumer_0` / `connector_0` / `acceptor_0`；
    `powerProduced` / `requiredPower` / `isPowered` 都按这些名字找节点。
  - `addElectricNode(node)` 会自动创建/复用一个名为 `master` 的 CONNECTOR 节点并把新节点连上去。
    所以"一个端口既能输入又能输出" = 单端口 + 内部再挂生产/消费节点。
  - **限流在电线物品上**（`WireRebarItem.getMaxPower()`），网络按每条边算负载；
    线径不够时机器拿到的是 `isPowered = false`，表现为直接停机。
  - 节点随方块 PDC 持久化，**没有公开 API 能删除节点** → 端口方案变更时旧存档无法迁移。
- **`WireRebarItem` 只有 `maxPower` 和 `displayMaterial`**：想做损耗 / 电压档 / 变压器，
  必须另加方块，别指望在"线物品"上做。
- **电力网络只认显式连接**：`ElectricNetwork` 的边全部来自 `ElectricNode.getConnections()`
  —— 电线实体，或 `ElectricNode.connect()`。**没有"相邻方块自动连电"这回事**，
  所以"线缆方块铺成网络""按颜色分网""控制器主动按通道路由"这类设计在 Rebar 里做不了。
  每条边的限流在 `ElectricNetwork.Edge.powerLimit` 上，由电线的 `maxPower` 决定。
- **内容必须注册指南页，否则游戏内手册里看不到**：`SimpleStaticGuidePage(key)` →
  `PAGE.addItem(物品)` → `RebarGuide.rootPage.addPage(图标物品, PAGE)`，
  页面标题取 lang 的 `guide.page.<key 的 path>`。漏了这一步，物品照样能拿到、能用，
  但玩家翻手册找不到它（新加内容也要记得加进页面列表）。
- **Kotlin 的块注释是可嵌套的**：文档注释里出现 `/*`（例如写 `lang/*.yml`）会再开一层注释，
  编译直接报 `Unclosed comment`。注释里别写 `/*`。
- **设置**：`getSettingOrThrow("key", ConfigAdapter.X)` 读 `resources/settings/<内容键>.yml`
  （也支持 `settings/<namespace>/<内容键>.yml`）。**文件不存在会直接抛异常**，新增设置记得同步建文件。
  数值/倍率不要写死在代码里。
- **语言**：`lang/en.yml` 和 `lang/zh.yml` 的 key **必须完全一致**（本模板带测试兜底）。
  显示名和描述放 lang，不要在 `ItemStackBuilder` 里写死；`getPlaceholders()` 的名字要和 `%xxx%` 对上。
- **占位符必须用 `RebarArgument` 传**：Rebar 的翻译器只替换 `RebarArgument` 类型的参数
  （内部是 `instanceof RebarArgument` + `getName()` 做文本替换）。`Component.translatable(key, 某个Component)`
  里的 `%name%` **不会**被替换，会原样显示在界面/聊天里。要写
  `Component.translatable(key, RebarArgument.of("name", 值))`。
- **InvUI**：结构里没被用到的 ingredient 不会报错（可以放心复用同一段 builder 代码）；
  但结构里某个字符的槽数**不能超过**对应 `VirtualInventory` 的容量，否则 `InventorySlotElementSupplier` 抛异常。
- **改显示名不要动 key**：`XxxKeys` 里的 key 一旦发布就别改，否则玩家已有物品/方块会失效。

## 目录约定

```
src/main/kotlin/<包>/          XxxAddon.kt（入口）/ XxxKeys.kt / XxxItems.kt / XxxBlocks.kt / XxxPages.kt
                               items/ blocks/ 放具体实现
src/main/resources/lang/       en.yml + zh.yml（key 必须一致）
src/main/resources/settings/   <内容键>.yml，数值配置
src/test/kotlin/<包>/          纯函数与资源校验测试（lang 一致性、数值换算）
```

- 内容模块（Items / Blocks / Entities / Fluids / Recipes / Pages …）各自暴露 `initialize()`，
  在 `onEnable` 里**按顺序**调用；新模块记得加进去。
- 有换算逻辑（倍率、等级表、升级组合）就抽成**纯函数 + 单测**，不要塞进 tick 热路径里。
- 一个内容 = 一个 key + 一个 ItemStack 原型 + `RebarItem.register(...)`；
  方块还要 `RebarBlock.register(key, 放置用 Material, 实现类)`。

## 发布流程

1. 改 `gradle.properties` 的 `version`
2. 提交（见下面的提交约定）
3. `git tag -a v1.0.0 -m "v1.0.0"` → `git push origin main v1.0.0`
4. CI 自动构建并发布 GitHub Release（`.github/workflows/build.yml` 里 `on.push.tags: v*`）

注意：**CI 不会在普通 push 时构建**（私有仓库要省 Actions 分钟），日常验证就本地跑
`./gradlew build`；需要出包时推标签，或在 GitHub Actions 页面手动触发。

## 提交信息约定

中文，正文按「做了什么 / 为什么 / 内容 / 影响面 / 验证」组织：

```
fix(1.2.1): 一句话说清改了什么

做了什么
- ...

为什么
- ...

内容
- ...

影响面
- 需要重新放置已有方块 / 数值变化 / 无

验证
- ./gradlew build 通过
- 新增或更新了哪些单测
```

## 本机 git 备注（换了机器可删）

这台机器上 `git push/fetch` 访问 github.com 会因 schannel 吊销检查失败
（`CRYPT_E_NO_REVOCATION_CHECK`）。仓库操作优先用 `gh`；需要 `git push` 时：

```bash
git -c http.sslBackend=openssl -c http.sslCAInfo=C:/Users/mc506lw/.workbuddy-ai/win-ca.pem push
```
