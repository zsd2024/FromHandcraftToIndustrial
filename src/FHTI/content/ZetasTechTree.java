package FHTI.content;

import static mindustry.content.TechTree.*;

public class ZetasTechTree {
    public static void load() {
        ModPlanets.zetas.techTree = nodeRoot("zetas", ModBlocks.core_primitive, () -> {
            node(ModBlocks.log_wall, () -> {
                node(ModBlocks.wooden_wall);
            });
            node(ModBlocks.log_cutter, () -> {
                node(ModBlocks.plank_cutter);
            });
            node(ModItems.log, () -> {
                node(ModItems.wood_block);
                node(ModItems.wood_chip);
            });
        });
    }
}
