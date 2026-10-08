# Lapis-Rebar-Template

写 **Rebar 附属插件**用的模板：克隆下来就能直接开写，构建配置、依赖、目录约定、示例内容都配好了。

> 🤖 **先读 [AGENTS.md](./AGENTS.md)** —— 里面写了怎么查 Rebar / Pylon 源码、这个生态里踩过的坑、
> 构建与发布流程、提交信息约定。AI 助手和未来的自己都从那里开始。

## 前置要求

| 要求 | 版本 |
|---|---|
| Minecraft | 26.2（Paper 或其分支）|
| Rebar | 0.44.4+（本插件是它的附属，运行时必须有）|
| JDK | 25 |

Rebar 和 Paper API 都是 `compileOnly`：**打出来的 jar 不能独立运行**，必须放进装了 Rebar 的服务器。

## 快速开始

1. 复制这个模板（GitHub 上点 **Use this template**，或直接 clone）
2. 改名四件套：
   - `settings.gradle.kts` → `rootProject.name`
   - `gradle.properties` → `group` / `main-class` / `version`
   - 包名（现在是 `top.mc506lw.rebar.template`）
   - `TemplateKeys.NAMESPACE` 和 `TemplateAddon.getKey()`（内容命名空间）
3. 构建：`./gradlew build` → `build/libs/<项目名>-<版本>.jar`
4. 放进服务器 `plugins/`，重启或 `/reload`

## 目录结构

```
src/main/kotlin/<包>/
  XxxAddon.kt        入口：JavaPlugin + RebarAddon，onEnable 里按顺序初始化各内容模块
  XxxKeys.kt         所有 NamespacedKey 集中在这里（内容键发布后不要改名）
  XxxItems.kt        物品注册（key + ItemStack 原型 + RebarItem.register）
  XxxBlocks.kt       方块注册（RebarBlock.register(key, 放置用 Material, 实现类)）
  items/             具体物品实现
  blocks/            具体方块实现
src/main/resources/
  lang/en.yml        英文文案（和 zh.yml 的 key 必须完全一致）
  lang/zh.yml        中文文案
  settings/          数值配置，文件名 = 内容键名（例：settings/example_item.yml）
src/test/kotlin/<包>/ 纯函数与资源校验测试（自带一个 lang 一致性测试）
.github/workflows/    推 v* 标签自动构建并发 Release
```

## 自带的示例

| 内容 | 演示了什么 |
|---|---|
| `example_item` | 物品注册、从 `settings/example_item.yml` 读数值、`getPlaceholders()` 与 lang 占位符对应 |
| `example_block` | 方块注册、`TickingRebarBlock` + `InteractRebarBlockHandler`、PDC 持久化 |

两个示例都写满了注释，照着复制改就行。常用接口清单（GUI、物流、电力、展示实体）写在
`blocks/ExampleBlock.kt` 的类注释里。

## 本机说明

- `gradle.properties` 里写死了本机 JDK 路径 `org.gradle.java.home=D:\Java\zulu25`；
  换机器请改掉或删除。CI 会自动删这一行。
- 中文资源统一 UTF-8，相关配置不要改。

## 发布

```bash
# 1. 改 gradle.properties 里的 version
# 2. 提交
# 3. 打标签推送
git tag -a v1.0.0 -m "v1.0.0"
git push origin main v1.0.0
# → CI 自动构建并发布 GitHub Release
```
