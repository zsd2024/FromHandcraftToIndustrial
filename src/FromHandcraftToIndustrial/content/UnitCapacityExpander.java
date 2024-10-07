package FromHandcraftToIndustrial.content;

import mindustry.gen.Building;
import mindustry.world.Block;

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

    public class UnitCapacityExpanderBuild extends Building {
        
    }
}
