package com.stardewbombers.client;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.FXGLMath;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.physics.BoundingBox;
import com.almasb.fxgl.physics.HitBox;
import com.stardewbombers.shared.entity.Block;
import com.stardewbombers.shared.entity.GameMap;
import com.stardewbombers.shared.enums.BlockType;
import com.stardewbombers.shared.util.SimpleMapLoader;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * 地图查看器应用程序
 * 用于在FXGL窗口中显示地图
 */
public class MapViewerApp extends GameApplication {

    private GameMap gameMap;
    
    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(800);
        settings.setHeight(600);
        settings.setTitle("StardewBombers - 地图查看器");
        settings.setVersion("1.0");
    }
    
    @Override
    protected void initGame() {
        // 加载地图
        gameMap = SimpleMapLoader.createFarmMap();
        
        // 设置游戏世界大小
        getGameWorld().setWidth(gameMap.getWidth() * gameMap.getTileWidth());
        getGameWorld().setHeight(gameMap.getHeight() * gameMap.getTileHeight());
        
        // 生成地图实体
        generateMapEntities();
        
        // 设置摄像机
        getGameScene().getViewport().setBounds(0, 0, 
            gameMap.getWidth() * gameMap.getTileWidth(), 
            gameMap.getHeight() * gameMap.getTileHeight());
    }
    
    private void generateMapEntities() {
        for (int y = 0; y < gameMap.getHeight(); y++) {
            for (int x = 0; x < gameMap.getWidth(); x++) {
                Block block = gameMap.getBlock(x, y);
                if (block != null && block.getType() != BlockType.FLOOR) {
                    spawnBlockEntity(x, y, block.getType());
                }
            }
        }
    }
    
    private void spawnBlockEntity(int x, int y, BlockType type) {
        Entity blockEntity = entityBuilder()
            .at(x * gameMap.getTileWidth(), y * gameMap.getTileHeight())
            .view(createBlockView(type))
            .build();
            
        // 如果是固体方块，添加碰撞组件
        if (type.isSolid()) {
            blockEntity.addComponent(new CollidableComponent(true));
            blockEntity.getBoundingBoxComponent().addHitBox(new HitBox(BoundingBox.box(
                gameMap.getTileWidth(), gameMap.getTileHeight())));
        }
        
        getGameWorld().addEntity(blockEntity);
    }
    
    private Rectangle createBlockView(BlockType type) {
        Rectangle rect = new Rectangle(gameMap.getTileWidth(), gameMap.getTileHeight());
        
        switch (type) {
            case BUSHES:
                rect.setFill(Color.GREEN);
                break;
            case PUMPKIN:
                rect.setFill(Color.ORANGE);
                break;
            case MELON:
                rect.setFill(Color.LIGHTGREEN);
                break;
            case STUMP:
                rect.setFill(Color.BROWN);
                break;
            default:
                rect.setFill(Color.GRAY);
        }
        
        // 添加边框
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(1);
        
        return rect;
    }
    
    @Override
    protected void initUI() {
        // 添加UI信息
        getGameScene().addUINode(
            getUIFactoryService().newText("地图大小: " + gameMap.getWidth() + "x" + gameMap.getHeight(), 20, 20)
        );
        getGameScene().addUINode(
            getUIFactoryService().newText("方块数量: " + gameMap.getDestructibleBlocks().size(), 20, 50)
        );
        getGameScene().addUINode(
            getUIFactoryService().newText("WASD移动视角, 鼠标滚轮缩放", 20, 80)
        );
    }
    
    @Override
    protected void initInput() {
        // 添加键盘控制
        onKeyDown("W", () -> {
            getGameScene().getViewport().translateY(-10);
        });
        onKeyDown("S", () -> {
            getGameScene().getViewport().translateY(10);
        });
        onKeyDown("A", () -> {
            getGameScene().getViewport().translateX(-10);
        });
        onKeyDown("D", () -> {
            getGameScene().getViewport().translateX(10);
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
