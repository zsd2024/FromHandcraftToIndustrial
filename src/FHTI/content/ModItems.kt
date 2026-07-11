package FHTI.content

import arc.graphics.Color
import arc.struct.Seq
import mindustry.type.Item

object ModItems {
    lateinit var log: Item
    lateinit var wood_chip: Item
    lateinit var wood_block: Item
    lateinit var wood_plank: Item

    val zetasItems: Seq<Item> = Seq()

    fun load() {
        log = Item("log", Color.valueOf("#8B4513")).apply {
            hardness = 1 // 硬度（越高需要越高级的钻头）
            flammability = 0.3f // 易燃性
            explosiveness = 0.1f // 爆炸性
            cost = 1f // 建造时间
            alwaysUnlocked = true // 始终解锁
        }
        wood_chip = Item("wood-chip", Color.valueOf("#A77756")).apply {
            flammability = 0.5f // 易燃性
            explosiveness = 0.3f // 爆炸性
            buildable = false // 不可作为核心中的物品
        }
        wood_block = Item("wood-block", Color.valueOf("#93572D")).apply {
            flammability = 0.2f // 易燃性
            cost = 2f // 建造时间
        }
        wood_plank = Item("wood-plank", Color.valueOf("#93572D")).apply {
            flammability = 0.4f // 易燃性
            cost = 1f // 建造时间
        }
        zetasItems.addAll(log, wood_chip, wood_block, wood_plank)
    }
}
