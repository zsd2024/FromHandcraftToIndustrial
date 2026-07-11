package FHTI.world.blocks.kinetic;

/** 用于描述任何产生动能的方块的基本接口。 */
public interface KineticBlock {
    float kinetic();

    /** @return 动能占最大动能的比例 */
    float kineticFrac();
}
