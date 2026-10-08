package top.mc506lw.rebar.template

import io.github.pylonmc.rebar.addon.RebarAddon
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import java.util.Locale

/**
 * Rebar 附属插件入口。
 *
 * 复制这个模板后要改的地方：
 *  1. settings.gradle.kts 的 rootProject.name
 *  2. gradle.properties 的 group / main-class / version
 *  3. 本文件与 TemplateKeys 里的命名空间（现在是 "template"）
 *  4. 包名、lang/、settings/ 里的内容
 *  5. 把 README.md 和 AGENTS.md 里跟本附属相关的说明改掉
 */
class TemplateAddon : JavaPlugin(), RebarAddon {

    companion object {
        lateinit var instance: TemplateAddon
            private set
    }

    override fun onEnable() {
        instance = this
        registerWithRebar()

        // 内容模块按顺序初始化；新模块记得加到这里
        TemplateItems.initialize()
        TemplateBlocks.initialize()
    }

    override val javaPlugin: JavaPlugin
        get() = this

    /**
     * Rebar 按 key 的 namespace 匹配附属，默认实现会从插件名推导（"lapis-rebar-template"），
     * 所以这里显式指定成内容键用的 namespace。
     */
    override fun getKey(): NamespacedKey = TemplateKeys.key(TemplateKeys.NAMESPACE)

    override val defaultLanguage: Locale
        get() = Locale.ENGLISH

    /** 附属在指南书里的图标 */
    override val material: Material
        get() = Material.CRAFTING_TABLE
}
