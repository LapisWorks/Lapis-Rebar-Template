package top.mc506lw.rebar.template.items

import io.github.pylonmc.rebar.config.adapter.ConfigAdapter
import io.github.pylonmc.rebar.i18n.RebarArgument
import io.github.pylonmc.rebar.item.RebarItem
import io.github.pylonmc.rebar.util.gui.unit.UnitFormat
import org.bukkit.inventory.ItemStack

/**
 * 示例物品。
 *
 * 规则：
 *  - 数值不要写死在代码里，放到 `resources/settings/<key>.yml`，用 getSettingOrThrow 读；
 *    服务器可以用同名文件覆盖（Rebar 会优先读服务器侧的设置）。
 *  - getPlaceholders() 里的名字要和 lang 目录下两份 yml 里的 `%xxx%` 一一对应，否则游戏里会显示成字面量。
 */
class ExampleItem(stack: ItemStack) : RebarItem(stack) {

    private val power = getSettingOrThrow("power", ConfigAdapter.DOUBLE)

    override fun getPlaceholders(): List<RebarArgument> = listOf(
        RebarArgument.of("power", UnitFormat.WATTS.format(power))
    )
}

/** 方块对应的物品。方块的 key 和物品的 key 相同，玩家才能用物品把方块放下来。 */
class ExampleBlockItem(stack: ItemStack) : RebarItem(stack)
