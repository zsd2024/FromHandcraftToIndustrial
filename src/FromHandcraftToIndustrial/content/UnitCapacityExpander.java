package FromHandcraftToIndustrial.content;

import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.consumers.ConsumeItems;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

public class UnitCapacityExpander extends Block {
    public float reload = 60f;// 重新加载时间
    public float range = 80f; // 范围
    public float useTime = 400f; // 使用时间
    public float phaseRangeBoost = 20f; // 阶段范围提升
    public boolean hasBoost = true; // 是否有提升
    public double exp = 10;// 增加数量

    public UnitCapacityExpander(String name) {
        super(name);
    }

    public UnitCapacityExpander(String name, int exp) {
        super(name);
        this.exp = exp;
    }
    @Override
    public void setStats(){
        stats.timePeriod = useTime;
        super.setStats();

        stats.add(Stat.speedIncrease, "+" + (int)(exp * 100f - 100) + "%");
        stats.add(Stat.range, range / tilesize, StatUnit.blocks);
        stats.add(Stat.productionTime, useTime / 60f, StatUnit.seconds);

        if(hasBoost && findConsumer(f -> f instanceof ConsumeItems) instanceof ConsumeItems items){
            stats.remove(Stat.booster);
            stats.add(Stat.booster, StatValues.itemBoosters("+{0}%", stats.timePeriod, speedBoostPhase * 100f, phaseRangeBoost, items.items, this::consumesItem));
        }
    }

    public class UnitCapacityExpanderBuild extends Building {
        
    }
}
