package com.stardewbombers.shared.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stardewbombers.shared.entity.Block;
import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.shared.enums.BlockType;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 地图加载器
 * 负责从Tiled导出的JSON文件加载地图数据
 */
public class MapLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    // 瓦片ID到BlockType的映射
    private static final Map<Integer, BlockType> TILE_ID_MAPPING = new HashMap<>();
    
    static {
        // 根据Tiled地图的瓦片ID进行映射
        // 农场地图的映射（基于farm_map.json）
        TILE_ID_MAPPING.put(1, BlockType.BUSHES);   // bush1
        TILE_ID_MAPPING.put(2, BlockType.FLOOR);    // floor
        TILE_ID_MAPPING.put(3, BlockType.MELON);    // melon
        TILE_ID_MAPPING.put(4, BlockType.PUMPKIN);  // pumpkin
        TILE_ID_MAPPING.put(5, BlockType.STUMP);    // stump
        TILE_ID_MAPPING.put(6, BlockType.BUSHES);   // bush1
        TILE_ID_MAPPING.put(7, BlockType.BUSHES);   // bush2
        TILE_ID_MAPPING.put(8, BlockType.BUSHES);   // bush3
        
        // 家具地图的映射（基于home_map.json，使用不同的ID范围避免冲突）
        TILE_ID_MAPPING.put(100, BlockType.CHAIR);    // chair
        TILE_ID_MAPPING.put(101, BlockType.TABLE);    // table
        TILE_ID_MAPPING.put(102, BlockType.CABINET1); // cabinet
        TILE_ID_MAPPING.put(103, BlockType.CABINET2); // cabinet2
        TILE_ID_MAPPING.put(104, BlockType.RUG);      // rug
        TILE_ID_MAPPING.put(105, BlockType.CHAIR);    // chair
        TILE_ID_MAPPING.put(106, BlockType.FIREPLACE1); // fireplace1
        TILE_ID_MAPPING.put(107, BlockType.STOOL);    // stool
        TILE_ID_MAPPING.put(108, BlockType.FIREPLACE2); // fireplace2
    }
    
    /**
     * 从资源文件加载地图
     */
    public static GameMap loadMap(String mapFileName) throws IOException {
        String resourcePath = "maps/" + mapFileName + ".json";
        InputStream inputStream = MapLoader.class.getClassLoader().getResourceAsStream(resourcePath);
        
        if (inputStream == null) {
            throw new IOException("地图文件未找到: " + resourcePath);
        }
        
        JsonNode rootNode = objectMapper.readTree(inputStream);
        return parseMap(rootNode);
    }
    
    /**
     * 解析JSON地图数据
     */
    private static GameMap parseMap(JsonNode rootNode) {
        // 获取地图基本信息
        int width = rootNode.get("width").asInt();
        int height = rootNode.get("height").asInt();
        int tileWidth = rootNode.get("tilewidth").asInt();
        int tileHeight = rootNode.get("tileheight").asInt();
        
        GameMap gameMap = new GameMap(width, height, tileWidth, tileHeight);
        
        // 解析图层数据
        JsonNode layersNode = rootNode.get("layers");
        if (layersNode != null && layersNode.isArray()) {
            for (JsonNode layerNode : layersNode) {
                String layerName = layerNode.get("name").asText();
                JsonNode dataNode = layerNode.get("data");
                
                if (dataNode != null && dataNode.isArray()) {
                    parseLayerData(gameMap, dataNode, layerName);
                }
            }
        }
        
        return gameMap;
    }
    
    /**
     * 解析图层数据
     */
    private static void parseLayerData(GameMap gameMap, JsonNode dataNode, String layerName) {
        int width = gameMap.getWidth();
        int height = gameMap.getHeight();
        
        for (int i = 0; i < dataNode.size(); i++) {
            int tileId = dataNode.get(i).asInt();
            
            if (tileId > 0) { // 0表示空瓦片
                int x = i % width;
                int y = i / width;
                
                BlockType blockType = TILE_ID_MAPPING.get(tileId);
                if (blockType != null) {
                    gameMap.setBlock(x, y, blockType);
                } else {
                    // 如果找不到映射，默认使用地板
                    System.out.println("警告: 未找到瓦片ID " + tileId + " 的映射，使用默认地板");
                    gameMap.setBlock(x, y, BlockType.FLOOR);
                }
            }
        }
    }
    
    /**
     * 设置瓦片ID映射
     * 这个方法允许您在运行时动态设置映射关系
     */
    public static void setTileIdMapping(int tileId, BlockType blockType) {
        TILE_ID_MAPPING.put(tileId, blockType);
    }
    
    /**
     * 获取当前瓦片ID映射
     */
    public static Map<Integer, BlockType> getTileIdMapping() {
        return new HashMap<>(TILE_ID_MAPPING);
    }
    
    /**
     * 创建默认测试地图
     */
    public static GameMap createDefaultMap() {
        GameMap map = new GameMap(16, 16, 32, 32);
        
        // 创建一些测试方块
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                if (x == 0 || x == 15 || y == 0 || y == 15) {
                    // 边界使用木桩
                    map.setBlock(x, y, BlockType.STUMP);
                } else if ((x + y) % 3 == 0) {
                    // 一些灌木
                    map.setBlock(x, y, BlockType.BUSHES);
                } else if ((x + y) % 5 == 0) {
                    // 一些南瓜
                    map.setBlock(x, y, BlockType.PUMPKIN);
                } else if ((x + y) % 7 == 0) {
                    // 一些瓜
                    map.setBlock(x, y, BlockType.MELON);
                }
                // 其他位置保持为地板
            }
        }
        
        return map;
    }
}
