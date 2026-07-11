package FHTI.content;

import arc.graphics.Color;
import arc.struct.Seq;
import mindustry.type.Item;

public class ModItems {
    public static Item log, wood_chip, wood_block, wood_plank;

    public static final Seq<Item> zetasItems = new Seq<>();

    public static void load() {
        log = new Item("log", Color.valueOf("#8B4513")) {
            {
                hardness = 1;// 硬度（越高需要越高级的钻头）
                flammability = 0.3f; // 易燃性
                explosiveness = 0.1f; // 爆炸性
                cost = 1; // 建造时间
                alwaysUnlocked = true; // 始终解锁
            }
        };
        wood_chip = new Item("wood-chip", Color.valueOf("#A77756")) {
            {
                flammability = 0.5f; // 易燃性
                explosiveness = 0.3f; // 爆炸性
                buildable = false; // 不可作为核心中的物品
            }
        };
        wood_block = new Item("wood-block", Color.valueOf("#93572D")) {
            {
                flammability = 0.2f; // 易燃性
                cost = 2; // 建造时间
            }
        };
        wood_plank = new Item("wood-plank", Color.valueOf("#93572D")) {
            {
                flammability = 0.4f; // 易燃性
                cost = 1; // 建造时间
            }
        };
        zetasItems.addAll(log, wood_chip, wood_block, wood_plank);
    }
}
