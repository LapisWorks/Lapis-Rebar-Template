package top.mc506lw.rebar.template.blocks

import io.github.pylonmc.rebar.block.RebarBlock
import io.github.pylonmc.rebar.block.context.BlockCreateContext
import io.github.pylonmc.rebar.block.interfaces.InteractRebarBlockHandler
import io.github.pylonmc.rebar.block.interfaces.TickingRebarBlock
import io.github.pylonmc.rebar.config.adapter.ConfigAdapter
import io.github.pylonmc.rebar.i18n.RebarArgument
import net.kyori.adventure.text.Component
import org.bukkit.block.Block
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataContainer
import org.bukkit.persistence.PersistentDataType
import top.mc506lw.rebar.template.TemplateKeys

/**
 * 示例方块：一个自己数 tick 的方块。
 *
 * 常用接口（按需实现，不用全上）：
 *  - [TickingRebarBlock]            定时 tick，用 setTickInterval 控制间隔
 *  - [InteractRebarBlockHandler]    右键交互
 *  - GuiRebarBlock / VirtualInventoryRebarBlock   GUI 与虚拟库存（配合 InvUI）
 *  - LogisticRebarBlock             接入 Rebar 物流组
 *  - SimpleElectricRebarBlock       接入 Rebar 电力网络（生产/消费端口）
 *  - EntityHolderRebarBlock         挂展示实体（贴图、面板、电线接头等）
 *  - BlockBreakRebarBlockHandler    被破坏时清理
 *
 * 持久化：Rebar 只给一个 PDC 入口 —— 构造函数里读、[write] 里写，运行期用字段缓存。
 */
class ExampleBlock : RebarBlock, TickingRebarBlock, InteractRebarBlockHandler {

    private val interval = getSettingOrThrow("tick-interval", ConfigAdapter.INTEGER)
    private var counter = 0

    constructor(block: Block, context: BlockCreateContext) : super(block, context)

    constructor(block: Block, pdc: PersistentDataContainer) : super(block, pdc) {
        counter = pdc.getOrDefault(TemplateKeys.EXAMPLE_COUNTER, PersistentDataType.INTEGER, 0)
    }

    init {
        setTickInterval(interval)
    }

    override fun write(pdc: PersistentDataContainer) {
        super.write(pdc)
        pdc.set(TemplateKeys.EXAMPLE_COUNTER, PersistentDataType.INTEGER, counter)
    }

    override fun tick() {
        counter++
    }

    override fun onInteractedWith(event: PlayerInteractEvent, priority: EventPriority) {
        if (priority != EventPriority.NORMAL) return
        if (!event.action.isRightClick || event.hand != EquipmentSlot.HAND) return

        event.isCancelled = true
        event.player.sendMessage(
            Component.translatable(
                "template.message.counter",
                RebarArgument.of("count", counter)
            )
        )
    }
}
