package FHTI.content;

import static mindustry.Vars.tilesize;
import static mindustry.type.ItemStack.with;

import FHTI.world.blocks.defense.Wall;
import FHTI.world.blocks.kinetic.KineticProducer;
import FHTI.world.blocks.production.KineticCrafter;
import FHTI.world.blocks.storage.CoreBlock;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.environment.StaticWall;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.draw.DrawHeatOutput;
import mindustry.world.draw.DrawMulti;
import mindustry.world.draw.DrawRegion;
import mindustry.world.meta.BuildVisibility;

public class ModBlocks {

    public static Block log_wall, wooden_wall, // 墙
            log_cutter, plank_cutter, // 工厂
            kinetic_source, // 动能
            core_primitive, // 核心
            grass, // 环境方块
            tree; // 矿石

    public static void load() {
        // region 墙
        log_wall = new Wall("log-wall") {
            {
                requirements(Category.defense, BuildVisibility.shown, with(ModItems.log, 10));
                health = 100;
                size = 1;
                buildCostMultiplier = 2f;
                serviceLife = 600;
            }
        };
        wooden_wall = new Wall("wooden-wall") {
            {
                requirements(Category.defense, BuildVisibility.shown, with(ModItems.wood_block, 10));
                health = 175;
                size = 1;
                buildCostMultiplier = 1.5f;
                serviceLife = 800;
            }
        };
        // region 工厂
        log_cutter = new KineticCrafter("log-cutter") {
            {
                requirements(Category.crafting, with(ModItems.log, 10));

                craftEffect = Fx.pulverizeMedium;
                outputItems = with(ModItems.wood_block, 6, ModItems.wood_chip, 30);
                itemCapacity = 30;
                craftTime = 350f;
                size = 2;
                hasItems = true;
                consumeItem(ModItems.log, 2);
                serviceLife = 300;

                kineticRequirement = 20f;
                overkineticScale = 0.5f;
                maxEfficiency = 2f;

                drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawBlock() {
                    public TextureRegion blade, log;

                    @Override
                    public void draw(Building build) {
                        super.draw(build);
                        if (!(build instanceof GenericCrafterBuild)) {
                            return;
                        }
                        if (((GenericCrafterBuild) build).progress <= 0.5f) {
                            Draw.rect(blade, build.x,
                                    build.y - build.block.size * ((GenericCrafterBuild) build).progress * 2 * tilesize
                                            * (30.0f / 64.0f),
                                    0.0f);
                            if (build.items.has(ModItems.log)) {
                                Draw.rect(log, build.x, build.y, 0.0f);
                            }
                        } else {
                            if (build.items.has(ModItems.log)) {
                                Draw.rect(log, build.x, build.y, 0.0f);
                            }
                            Draw.rect(blade, build.x, build.y - (build.block.size - build.block.size
                                    * (((GenericCrafterBuild) build).progress - 0.5f) * 2) * tilesize * (30.0f / 64.0f),
                                    0.0f);
                        }
                    }

                    @Override
                    public TextureRegion[] icons(Block block) {
                        return new TextureRegion[] { blade, log };
                    }

                    @Override
                    public void load(Block block) {
                        super.load(block);
                        blade = Core.atlas.find(block.name + "-blade");
                        log = Core.atlas.find(block.name + "-log");
                    }
                }, new DrawDefault());
            }
        };
        plank_cutter = new KineticCrafter("plank-cutter") {
            {
                requirements(Category.crafting, with(ModItems.log, 5, ModItems.wood_block, 8));

                craftEffect = Fx.pulverizeMedium;
                outputItems = with(ModItems.wood_plank, 4, ModItems.wood_chip, 10);
                itemCapacity = 15;
                craftTime = 200f;
                size = 2;
                hasItems = true;
                consumeItem(ModItems.wood_block, 1);
                serviceLife = 200;

                kineticRequirement = 2f;
                overkineticScale = 0.5f;
                maxEfficiency = 2f;

                drawer = new DrawMulti(new DrawRegion("-bottom"), new DrawBlock() {
                    public TextureRegion blade, wood_block;

                    @Override
                    public void draw(Building build) {
                        super.draw(build);
                        if (!(build instanceof GenericCrafterBuild)) {
                            return;
                        }
                        if (((GenericCrafterBuild) build).progress <= 0.5f) {
                            Draw.rect(blade, build.x,
                                    build.y - build.block.size * ((GenericCrafterBuild) build).progress * 2 * tilesize
                                            * (30.0f / 64.0f),
                                    0.0f);
                            if (build.items.has(ModItems.wood_block)) {
                                Draw.rect(wood_block, build.x, build.y, 0.0f);
                            }
                        } else {
                            if (build.items.has(ModItems.wood_block)) {
                                Draw.rect(wood_block, build.x, build.y, 0.0f);
                            }
                            Draw.rect(blade, build.x, build.y - (build.block.size - build.block.size
                                    * (((GenericCrafterBuild) build).progress - 0.5f) * 2) * tilesize * (30.0f / 64.0f),
                                    0.0f);
                        }
                    }

                    @Override
                    public TextureRegion[] icons(Block block) {
                        return new TextureRegion[] { blade, wood_block };
                    }

                    @Override
                    public void load(Block block) {
                        super.load(block);
                        blade = Core.atlas.find(block.name + "-blade");
                        wood_block = Core.atlas.find(block.name + "-wood-block");
                    }
                }, new DrawDefault());
            }
        };
        // region 动能
        kinetic_source = new KineticProducer("kinetic-source") {
            {
                requirements(Category.crafting, BuildVisibility.sandboxOnly, ItemStack.with());
                drawer = new DrawMulti(new DrawDefault(), new DrawHeatOutput());
                rotateDraw = false;
                size = 1;
                kineticOutput = 1000f;
                energyProductionRate = 1000f;
                regionRotated1 = 1;
                ambientSound = Sounds.none;
                serviceLife = 114514;
            }
        };
        // region 核心
        core_primitive = new CoreBlock("core-primitive") {
            {
                requirements(Category.effect, new ItemStack[] { new ItemStack(ModItems.log, 100) });
                alwaysUnlocked = true;
                isFirstTier = true;
                unitType = ModUnits.primitive_silicon_based_life;
                health = 300;
                itemCapacity = 300;
                size = 2;
                unitCapModifier = 4;
                serviceLife = 900;
            }
        };
        // region 环境方块
        grass = new Floor("grass") {
            {
                speedMultiplier = 0.9f;
            }
        };
        // region 矿石
        tree = new StaticWall("tree") {
            {
                itemDrop = ModItems.log;
                variants = 1;
            }
        };
        // Events.run(EventType.Trigger.update, () -> {
        // if (Vars.state.isPlaying() && Vars.state.isGame()) {
        // }
        // });
    }
}
