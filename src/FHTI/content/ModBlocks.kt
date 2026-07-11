package FHTI.content

import mindustry.Vars.tilesize
import mindustry.type.ItemStack.with
import FHTI.world.blocks.defense.Wall
import FHTI.world.blocks.kinetic.KineticProducer
import FHTI.world.blocks.production.KineticCrafter
import FHTI.world.blocks.storage.CoreBlock
import arc.Core
import arc.graphics.g2d.Draw
import arc.graphics.g2d.TextureRegion
import mindustry.content.Fx
import mindustry.gen.Building
import mindustry.gen.Sounds
import mindustry.type.Category
import mindustry.type.ItemStack
import mindustry.world.Block
import mindustry.world.blocks.environment.Floor
import mindustry.world.blocks.environment.StaticWall
import mindustry.world.draw.DrawBlock
import mindustry.world.draw.DrawDefault
import mindustry.world.draw.DrawHeatOutput
import mindustry.world.draw.DrawMulti
import mindustry.world.draw.DrawRegion
import mindustry.world.meta.BuildVisibility

// 主类
object ModBlocks {
    // 静态字段
    lateinit var log_wall: Block      // 墙
    lateinit var wooden_wall: Block   // 墙
    lateinit var log_cutter: Block    // 工厂
    lateinit var plank_cutter: Block  // 工厂
    lateinit var kinetic_source: Block // 动能
    lateinit var core_primitive: Block // 核心
    lateinit var grass: Floor         // 环境方块
    lateinit var tree: StaticWall     // 矿石

    fun load() {
        // 墙
        log_wall = Wall("log-wall").apply {
            requirements(Category.defense, BuildVisibility.shown, with(ModItems.log, 10))
            health = 100
            size = 1
            buildCostMultiplier = 2f
            serviceLife = 600f
        }

        wooden_wall = Wall("wooden-wall").apply {
            requirements(Category.defense, BuildVisibility.shown, with(ModItems.wood_block, 10))
            health = 175
            size = 1
            buildCostMultiplier = 1.5f
            serviceLife = 800f
        }

        // 工厂
        log_cutter = KineticCrafter("log-cutter").apply {
            requirements(Category.crafting, with(ModItems.log, 10))

            craftEffect = Fx.pulverizeMedium
            outputItems = with(ModItems.wood_block, 6, ModItems.wood_chip, 30)
            itemCapacity = 30
            craftTime = 350f
            size = 2
            hasItems = true
            consumeItem(ModItems.log, 2)
            serviceLife = 300f

            kineticRequirement = 20f
            overkineticScale = 0.5f
            maxEfficiency = 2f

            drawer = DrawMulti(
                DrawRegion("-bottom"),
                object : DrawBlock() {
                    lateinit var blade: TextureRegion
                    lateinit var log: TextureRegion

                    override fun draw(build: Building) {
                        super.draw(build)
                        if (build !is mindustry.world.blocks.production.GenericCrafter.GenericCrafterBuild) {
                            return
                        }
                        if (build.progress <= 0.5f) {
                            Draw.rect(
                                blade, build.x,
                                build.y - build.block.size * build.progress * 2 * tilesize * (30.0f / 64.0f),
                                0.0f
                            )
                            if (build.items.has(ModItems.log)) {
                                Draw.rect(log, build.x, build.y, 0.0f)
                            }
                        } else {
                            if (build.items.has(ModItems.log)) {
                                Draw.rect(log, build.x, build.y, 0.0f)
                            }
                            Draw.rect(
                                blade, build.x, build.y - (build.block.size - build.block.size
                                        * (build.progress - 0.5f) * 2) * tilesize * (30.0f / 64.0f),
                                0.0f
                            )
                        }
                    }

                    override fun icons(block: Block): Array<TextureRegion> {
                        return arrayOf(blade, log)
                    }

                    override fun load(block: Block) {
                        super.load(block)
                        blade = Core.atlas.find(block.name + "-blade")
                        log = Core.atlas.find(block.name + "-log")
                    }
                },
                DrawDefault()
            )
        }

        plank_cutter = KineticCrafter("plank-cutter").apply {
            requirements(Category.crafting, with(ModItems.log, 5, ModItems.wood_block, 8))

            craftEffect = Fx.pulverizeMedium
            outputItems = with(ModItems.wood_plank, 4, ModItems.wood_chip, 10)
            itemCapacity = 15
            craftTime = 200f
            size = 2
            hasItems = true
            consumeItem(ModItems.wood_block, 1)
            serviceLife = 200f

            kineticRequirement = 2f
            overkineticScale = 0.5f
            maxEfficiency = 2f

            drawer = DrawMulti(
                DrawRegion("-bottom"),
                object : DrawBlock() {
                    lateinit var blade: TextureRegion
                    lateinit var wood_block: TextureRegion

                    override fun draw(build: Building) {
                        super.draw(build)
                        if (build !is mindustry.world.blocks.production.GenericCrafter.GenericCrafterBuild) {
                            return
                        }
                        if (build.progress <= 0.5f) {
                            Draw.rect(
                                blade, build.x,
                                build.y - build.block.size * build.progress * 2 * tilesize * (30.0f / 64.0f),
                                0.0f
                            )
                            if (build.items.has(ModItems.wood_block)) {
                                Draw.rect(wood_block, build.x, build.y, 0.0f)
                            }
                        } else {
                            if (build.items.has(ModItems.wood_block)) {
                                Draw.rect(wood_block, build.x, build.y, 0.0f)
                            }
                            Draw.rect(
                                blade, build.x, build.y - (build.block.size - build.block.size
                                        * (build.progress - 0.5f) * 2) * tilesize * (30.0f / 64.0f),
                                0.0f
                            )
                        }
                    }

                    override fun icons(block: Block): Array<TextureRegion> {
                        return arrayOf(blade, wood_block)
                    }

                    override fun load(block: Block) {
                        super.load(block)
                        blade = Core.atlas.find(block.name + "-blade")
                        wood_block = Core.atlas.find(block.name + "-wood-block")
                    }
                },
                DrawDefault()
            )
        }

        // 动能
        kinetic_source = KineticProducer("kinetic-source").apply {
            requirements(Category.crafting, BuildVisibility.sandboxOnly, ItemStack.with())
            drawer = DrawMulti(DrawDefault(), DrawHeatOutput())
            rotateDraw = false
            size = 1
            kineticOutput = 1000f
            energyProductionRate = 1000f
            regionRotated1 = 1
            ambientSound = Sounds.none
            serviceLife = 114514f
        }

        // 核心
        core_primitive = CoreBlock("core-primitive").apply {
            requirements(Category.effect, arrayOf(ItemStack(ModItems.log, 100)))
            alwaysUnlocked = true
            isFirstTier = true
            unitType = ModUnits.primitive_silicon_based_life
            health = 300
            itemCapacity = 300
            size = 2
            unitCapModifier = 4
            serviceLife = 900f
        }

        // 环境方块
        grass = object : Floor("grass") {
            init {
                speedMultiplier = 0.9f
            }
        }

        // 矿石
        tree = object : StaticWall("tree") {
            init {
                itemDrop = ModItems.log
                variants = 1
            }
        }

        // Events.run(EventType.Trigger.update) {
        //     if (Vars.state.isPlaying() && Vars.state.isGame()) {
        //     }
        // }
    }
}

fun Float.foo(a: Float) {
    this.toInt()
}
