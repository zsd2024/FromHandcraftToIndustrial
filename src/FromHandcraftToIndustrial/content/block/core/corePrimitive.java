package FromHandcraftToIndustrial.content.block.core;

import FromHandcraftToIndustrial.content.item.ModItems;
import mindustry.content.UnitTypes;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.blocks.storage.CoreBlock;

public class corePrimitive {
    public static CoreBlock core_primitive;

    public static void load() {
        core_primitive = new CoreBlock("core-primitive-wooden") {
            {
                requirements(Category.effect, new ItemStack[] { new ItemStack(ModItems.log, 100) });
                alwaysUnlocked = true;
                isFirstTier = true;
                unitType = UnitTypes.alpha;
                health = 300;
                itemCapacity = 300;
                size = 2;
                unitCapModifier = 4;
            }
        };
    }
}
