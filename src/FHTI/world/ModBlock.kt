package FHTI.world;

import java.util.Arrays
import kotlin.math.*

import FHTI.world.blocks.kinetic.KineticBlock
import FHTI.world.blocks.kinetic.KineticConductor
import FHTI.world.blocks.kinetic.KineticConductor.KineticConductorBuild
import arc.Core
import arc.graphics.Color
import arc.math.Mathf
import arc.struct.IntSet
import mindustry.Vars
import mindustry.gen.Building
import mindustry.ui.Bar
import mindustry.world.Block
import mindustry.world.meta.Stat

open class ModBlock(name: String) : Block(name) {
    /**
     * 该可磨损方块的使用寿命
     */
    @JvmField
    var serviceLife: Float = 0f

    init {
        update = true;
    }

    override fun setBars() {
        super.setBars()
        addBar("wearlevel") { entity: ModBuilding ->
            Bar(
                { Core.bundle.get("stat.from-handwork-to-industrial-wearlevel") },
                { Color.HSVtoRGB((1.0f - entity.wearLevelf()) * 0.5f * 360, 100f, 100f) },
                { entity.wearLevelf() }
            )
        }
    }

    override fun setStats() {
        super.setStats()
        when {
            serviceLife < 60f -> {
                stats.add(
                    Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-seconds"),
                    serviceLife
                )
            }

            serviceLife < 3600f -> {
                stats.add(
                    Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-minutes"),
                    (serviceLife / 60f).toInt(),
                    serviceLife % 60f
                )
            }

            serviceLife < 86400f -> {
                stats.add(
                    Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-hours"),
                    (serviceLife / 3600f).toInt(),
                    (serviceLife % 3600f / 60f).toInt(),
                    serviceLife % 60f
                )
            }

            serviceLife < 604800f -> {
                stats.add(
                    Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-days"),
                    (serviceLife / 86400f).toInt(),
                    (serviceLife % 86400f / 3600f).toInt(),
                    (serviceLife % 3600f / 60f).toInt(),
                    serviceLife % 60f
                )
            }

            serviceLife < 31536000f -> {
                stats.add(
                    Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-weeks"),
                    (serviceLife / 604800f).toInt(),
                    (serviceLife % 604800f / 86400f).toInt(),
                    (serviceLife % 86400f / 3600f).toInt(),
                    (serviceLife % 3600f / 60f).toInt(),
                    serviceLife % 60f
                )
            }

            else -> {
                stats.add(
                    Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-years"),
                    (serviceLife / 31536000f).toInt(),
                    (serviceLife % 31536000f / 604800f).toInt(),
                    (serviceLife % 604800f / 86400f).toInt(),
                    (serviceLife % 86400f / 3600f).toInt(),
                    (serviceLife % 3600f / 60f).toInt(),
                    serviceLife % 60f
                )
            }
        }
    }

    open inner class ModBuilding : Building() {

        /// WearableBlockBuild Code Start

        /**
         * 该可磨损方块的使用时间
         */
        var serviceTime: Double = 0.0

        /**
         * 上次更新时间
         */
        private var lastUpdate: Long = -1

        /**
         * 该可磨损方块的磨损百分比
         *
         * @return 磨损百分比
         */
        fun wearLevelf(): Float = (serviceTime / serviceLife).toFloat()

        /**
         * 获取基础使用寿命增量
         *
         * @return 基础使用寿命增量
         */
        fun getUsageIncrementBesic(): Float = 1.0f

        /**
         * 获取每秒使用寿命增量
         *
         * @return 每秒使用寿命增量
         */
        fun getUsageIncrementPerSecond(): Float =
            getUsageIncrementBesic() / healthf() * ((1.0f / Core.graphics.framesPerSecond) / Core.graphics.deltaTime)

        /**
         * 更新使用寿命
         */
        fun updateServiceTime() {
            if (lastUpdate == -1L) {
                lastUpdate = System.nanoTime()
            } else {
                // Log.info("lastUpdate: " + lastUpdate)
                serviceTime += (System.nanoTime() - lastUpdate) * 1e-9 * getUsageIncrementPerSecond()

                lastUpdate = System.nanoTime()
            }
            // Log.info("Current serviceTime: " + serviceTime)
            if (serviceTime >= serviceLife) {
                kill()
            }
        }

        /// WearableBlockBuild Code End

        /// Kinetic Calc Code Start

        fun calculateKinetic(sideKinetic: FloatArray): Float {
            return calculateKinetic(sideKinetic, null)
        }

        fun calculateKinetic(sideKinetic: FloatArray, cameFrom: IntSet?): Float {
            Arrays.fill(sideKinetic, 0f)
            cameFrom?.clear()

            var kinetic = 0f

            for (build in proximity) {
                if (build != null && build.team == team && build is KineticBlock) {
                    val kineticer = build as KineticBlock
                    // boolean split = build.block instanceof KineticConductor cond &&
                    // cond.splitKinetic;
                    var split = build.block is KineticConductor
                    if (split) {
                        val cond = build.block as KineticConductor
                        split = cond.splitKinetic
                    }
                    // 非路由器必须面向我们，路由器必须面向相反 - 在重定向器旁边，它们将被强制面向相反方向
                    if (!build.block.rotate || (!split && (relativeTo(build) + 2) % 4 == build.rotation)
                        || (split && relativeTo(build).toInt() != build.rotation)
                    ) { // TODO hacky

                        // 如果存在环路，忽略其动能
                        if (!(build is KineticConductorBuild
                                    && build.cameFrom.contains(id()))
                        ) {
                            // x/y 坐标差异在接触点
                            val diff = min(abs(build.x - x), abs(build.y - y)) / Vars.tilesize
                            // 该块与其他块的接触点数
                            val contactPoints = min(
                                (block.size / 2f + build.block.size / 2f - diff).toInt(),
                                min(build.block.size, block.size)
                            )

                            // 动能被分配到建筑的尺寸
                            var add = kineticer.kinetic() / build.block.size * contactPoints
                            if (split) {
                                // 动能路由器将动能分配到 3 个表面
                                add /= 3f
                            }

                            sideKinetic[Mathf.mod(relativeTo(build).toInt(), 4)] += add
                            kinetic += add
                        }

                        // 记录遍历的环路
                        cameFrom?.add(build.id)
                        if (build is KineticConductorBuild) {
                            cameFrom?.addAll(build.cameFrom)
                        }

                        // 一个巨大的 hack 但是 我不在乎了
                        if (build is KineticConductorBuild) {
                            build.updateKinetic()
                        }
                    }
                }
            }
            return kinetic
        }

        /// Kinetic Calc Code End

        override fun updateTile() {
            super.updateTile()
            updateServiceTime()
        }
    }
}
