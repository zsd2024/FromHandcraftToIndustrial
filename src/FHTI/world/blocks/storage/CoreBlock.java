package FHTI.world.blocks.storage;

import arc.Core;
import arc.graphics.Color;
import mindustry.ui.Bar;
import mindustry.world.meta.Stat;

public class CoreBlock extends mindustry.world.blocks.storage.CoreBlock {

    /**
     * 该可磨损方块的使用寿命
     */
    public float serviceLife;

    public CoreBlock(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        super.setStats();
        if (serviceLife < 60f)
            stats.add(new Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-seconds"), serviceLife);
        else if (serviceLife < 3600f)
            stats.add(new Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-minutes"),
                    (int) (serviceLife / 60f), serviceLife % 60f);
        else if (serviceLife < 86400f)
            stats.add(new Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-hours"),
                    (int) (serviceLife / 3600f), (int) (serviceLife % 3600f / 60f), serviceLife % 60f);
        else if (serviceLife < 604800f)
            stats.add(new Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-days"),
                    (int) (serviceLife / 86400f), (int) (serviceLife % 86400f / 3600f),
                    (int) (serviceLife % 3600f / 60f), serviceLife % 60f);
        else if (serviceLife < 31536000f)
            stats.add(new Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-weeks"),
                    (int) (serviceLife / 604800f), (int) (serviceLife % 604800f / 86400f),
                    (int) (serviceLife % 86400f / 3600f), (int) (serviceLife % 3600f / 60f), serviceLife % 60f);
        else
            stats.add(new Stat("from-handwork-to-industrial-service-life"),
                    Core.bundle.get("stat.from-handwork-to-industrial-service-life-years"),
                    (int) (serviceLife / 31536000f), (int) (serviceLife % 31536000f / 604800f),
                    (int) (serviceLife % 604800f / 86400f), (int) (serviceLife % 86400f / 3600f),
                    (int) (serviceLife % 3600f / 60f), serviceLife % 60f);
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("wearlevel", (CoreBuild entity) -> new Bar(
                () -> Core.bundle.get("stat.from-handwork-to-industrial-wearlevel"),
                () -> Color.HSVtoRGB((1.0f - entity.wearLevelf()) * 0.5f * 360, 100,
                        100),
                entity::wearLevelf));
    }

    class CoreBuild extends mindustry.world.blocks.storage.CoreBlock.CoreBuild {
        /// WearableBlockBuild Code Start

        /**
         * 该可磨损方块的使用时间
         */
        public double serviceTime;

        /**
         * 上次更新时间
         */
        private long lastUpdate = -1;

        /**
         * 该可磨损方块的磨损百分比
         *
         * @return 磨损百分比
         */
        public float wearLevelf() {
            return (float) (serviceTime / serviceLife);
        }

        /**
         * 获取基础使用寿命增量
         *
         * @return 基础使用寿命增量
         */
        public float getUsageIncrementBesic() {
            return 1.0f;
        }

        /**
         * 获取每秒使用寿命增量
         *
         * @return 每秒使用寿命增量
         */
        public float getUsageIncrementPerSecond() {
            return getUsageIncrementBesic() / healthf();
        }

        /**
         * 更新使用寿命
         */
        public void updateServiceTime() {
            if (lastUpdate == -1) {
                lastUpdate = System.nanoTime();
            } else {
                // Log.info("lastUpdate: " + lastUpdate);
                serviceTime += (System.nanoTime() - lastUpdate) * 1e-9 * getUsageIncrementPerSecond();

                lastUpdate = System.nanoTime();
            }
            // Log.info("Current serviceTime: " + serviceTime);
            if (serviceTime >= serviceLife) {
                kill();
            }
        }

        /// WearableBlockBuild Code End

        @Override
        public void updateTile() {
            super.updateTile();
            updateServiceTime();
        }
    }

}
