package com.stardewbombers.shared.enums;

/**
 * 地图块类型枚举
 * 根据Tiled地图中的元素定义
 */
public enum BlockType {
    // 地板 - 玩家可以行走，无碰撞，不可炸，无奖励
    FLOOR(0, "floor", false, false, false),
    
    // 南瓜 - 玩家可以行走，无碰撞，可炸，有奖励
    PUMPKIN(1, "pumpkin", false, true, true),
    
    // 瓜 - 玩家可以行走，无碰撞，可炸，有奖励  
    MELON(2, "melon", false, true, true),
    
    // 灌木 - 有碰撞，可炸，无奖励
    BUSHES(3, "bushes", true, true, false),
    
    // 木桩 - 有碰撞，不可炸，无奖励
    STUMP(4, "stump", true, false, false),
    
    // 新添加的家具类方块
    // 柜子1 - 有碰撞，可炸，有奖励
    CABINET1(5, "cabinet1", true, true, true),
    
    // 柜子2 - 有碰撞，可炸，有奖励
    CABINET2(6, "cabinet2", true, true, true),
    
    // 桌子 - 有碰撞，可炸，无奖励
    TABLE(7, "table", true, true, false),
    
    // 凳子 - 有碰撞，可炸，无奖励
    STOOL(8, "stool", true, true, false),
    
    // 椅子 - 有碰撞，可炸，无奖励
    CHAIR(9, "chair", true, true, false),
    
    // 壁炉1 - 有碰撞，不可炸，无奖励
    FIREPLACE1(10, "fireplace1", true, false, false),
    
    // 壁炉2 - 有碰撞，不可炸，无奖励
    FIREPLACE2(11, "fireplace2", true, false, false),
    
    // 地毯 - 无碰撞，不可炸，无奖励
    RUG(12, "rug", false, false, false);
    
    private final int id;
    private final String name;
    private final boolean solid;        // 是否有碰撞体积
    private final boolean explorable;   // 是否能被炸掉
    private final boolean treat;        // 被炸掉后是否有奖励
    
    BlockType(int id, String name, boolean solid, boolean explorable, boolean treat) {
        this.id = id;
        this.name = name;
        this.solid = solid;
        this.explorable = explorable;
        this.treat = treat;
    }
    
    public int getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public boolean isSolid() {
        return solid;
    }
    
    public boolean isExplorable() {
        return explorable;
    }
    
    public boolean isTreat() {
        return treat;
    }
    
    /**
     * 检查方块是否可以被玩家穿过
     */
    public boolean isWalkable() {
        return !solid;
    }
    
    /**
     * 根据ID获取BlockType
     */
    public static BlockType fromId(int id) {
        for (BlockType type : values()) {
            if (type.id == id) {
                return type;
            }
        }
        return FLOOR; // 默认返回地板
    }
    
    /**
     * 根据名称获取BlockType
     */
    public static BlockType fromName(String name) {
        for (BlockType type : values()) {
            if (type.name.equals(name)) {
                return type;
            }
        }
        return FLOOR; // 默认返回地板
    }
}