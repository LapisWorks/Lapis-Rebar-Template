package top.mc506lw.rebar.template

import io.github.pylonmc.rebar.block.RebarBlock
import org.bukkit.Material
import top.mc506lw.rebar.template.blocks.ExampleBlock

/**
 * 方块注册。
 *
 * [Material] 是"放置用的原版方块"：Rebar 方块本体就是它，所以贴图要么用它本身的，
 * 要么用 EntityHolderRebarBlock 挂展示实体自己画（见 AGENTS.md 里的坑清单）。
 */
object TemplateBlocks {

    fun initialize() {
        RebarBlock.register(TemplateKeys.EXAMPLE_BLOCK, Material.STONE, ExampleBlock::class.java)
    }
}
