package com.example.rebartemplate

import org.bukkit.NamespacedKey

/**
 * 所有 NamespacedKey 集中在这里，方便统一改命名空间。
 *
 * 注意：内容键一旦发布就不要改名，否则玩家已有的物品/方块会失效（显示成原版物品或变成未知方块）。
 * 只想改显示名时，改 lang 文件即可，别动这里的 key。
 */
object TemplateKeys {

    /** 内容命名空间，要和 TemplateAddon.getKey() 保持一致 */
    const val NAMESPACE = "template"

    fun key(name: String) = NamespacedKey(NAMESPACE, name)

    val EXAMPLE_ITEM = key("example_item")
    val EXAMPLE_BLOCK = key("example_block")

    /** 需要持久化到方块 PDC 的数据键 */
    val EXAMPLE_COUNTER = key("example_counter")
}
