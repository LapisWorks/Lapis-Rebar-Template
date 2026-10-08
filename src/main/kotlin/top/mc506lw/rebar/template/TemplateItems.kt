package top.mc506lw.rebar.template

import io.github.pylonmc.rebar.item.RebarItem
import io.github.pylonmc.rebar.item.builder.ItemStackBuilder
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import top.mc506lw.rebar.template.items.ExampleBlockItem
import top.mc506lw.rebar.template.items.ExampleItem

/**
 * 物品注册。
 *
 * 每个内容都要：① 一个 NamespacedKey；② 一个 ItemStack 原型；③ RebarItem.register(...)。
 * 显示名/描述不在这里写，放到 `lang/en.yml` 与 `lang/zh.yml`（两边 key 必须一致，有测试兜底）。
 */
object TemplateItems {

    val EXAMPLE_ITEM: ItemStack = createItem(Material.DIAMOND, TemplateKeys.EXAMPLE_ITEM)
    val EXAMPLE_BLOCK_ITEM: ItemStack = createItem(Material.STONE, TemplateKeys.EXAMPLE_BLOCK)

    fun initialize() {
        RebarItem.register(ExampleItem::class.java, EXAMPLE_ITEM, TemplateKeys.EXAMPLE_ITEM)
        RebarItem.register(ExampleBlockItem::class.java, EXAMPLE_BLOCK_ITEM, TemplateKeys.EXAMPLE_BLOCK)
    }

    private fun createItem(material: Material, key: NamespacedKey): ItemStack =
        ItemStackBuilder.rebar(material, key).build()
}
