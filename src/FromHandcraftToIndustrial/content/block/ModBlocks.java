package FromHandcraftToIndustrial.content.block;

import FromHandcraftToIndustrial.content.block.core.corePrimitive;
import FromHandcraftToIndustrial.content.item.ModItems;
import mindustry.content.Fx;
import mindustry.content.Items;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.meta.BuildVisibility;

import static mindustry.type.ItemStack.*;

public class ModBlocks {
    public static Wall wood_wall;
    public static GenericCrafter log_cutter;

    public static void load() {
        corePrimitive.load();
        wood_wall = new Wall("log-wall") {
            {
                requirements(Category.defense, BuildVisibility.shown, with(ModItems.log, 10));
                health = 100;
                size = 1;
                buildCostMultiplier = 2f;
            }
        };
        log_cutter = new GenericCrafter("log-cutter") {
            {
                requirements(Category.crafting, with(ModItems.log, 10));

                craftEffect = Fx.pulverizeMedium;
                outputItem = new ItemStack(Items.graphite, 1);
                craftTime = 90f;
                size = 4;
                hasItems = true;
                consumeItem(ModItems.log, 2);
            }
        };
    }
}
