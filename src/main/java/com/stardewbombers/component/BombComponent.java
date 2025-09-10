package com.stardewbombers.component;

import com.stardewbombers.shared.entity.Bomb;

/**
 * 炸弹组件类
 * 负责炸弹的爆炸逻辑和状态更新
 */
public class BombComponent {

    private Bomb bomb;

    public BombComponent(Bomb bomb) {
        this.bomb = bomb;
    }

    /**
     * 更新炸弹状态
     * @param deltaTime 时间增量（秒）
     */
    public void update(double deltaTime) {
        if (bomb.getState() == Bomb.BombState.TICKING) {
            double newFuseTime = bomb.getFuseTime() - deltaTime;
            bomb.setFuseTime(newFuseTime);

            // 检查是否应该爆炸
            if (newFuseTime <= 0) {
                bomb.explode();
            }
        } else if (bomb.getState() == Bomb.BombState.EXPLODING) {
            // 检查爆炸是否结束
            long currentTime = System.currentTimeMillis();
            if (currentTime - bomb.getExplosionStartTime() >= bomb.getExplosionDuration() * 1000) {
                bomb.setState(Bomb.BombState.EXPLODED);
                bomb.setHasExploded(true);
            }
        }
    }

    /**
     * 获取爆炸影响的范围（十字形）
     * @return 返回受影响的位置数组 [x1, y1, x2, y2, ...]
     */
    public int[] getExplosionRange() {
        int radius = bomb.getExplosionRadius();
        int centerX = bomb.getX();
        int centerY = bomb.getY();

        // 计算十字形爆炸范围
        int[] affectedPositions = new int[(radius * 4 + 1) * 2]; // 上下左右各radius格 + 中心点

        int index = 0;

        // 中心点
        affectedPositions[index++] = centerX;
        affectedPositions[index++] = centerY;

        // 上方
        for (int i = 1; i <= radius; i++) {
            affectedPositions[index++] = centerX;
            affectedPositions[index++] = centerY - i;
        }

        // 下方
        for (int i = 1; i <= radius; i++) {
            affectedPositions[index++] = centerX;
            affectedPositions[index++] = centerY + i;
        }

        // 左方
        for (int i = 1; i <= radius; i++) {
            affectedPositions[index++] = centerX - i;
            affectedPositions[index++] = centerY;
        }

        // 右方
        for (int i = 1; i <= radius; i++) {
            affectedPositions[index++] = centerX + i;
            affectedPositions[index++] = centerY;
        }

        return affectedPositions;
    }

    /**
     * 检查位置是否在爆炸范围内
     */
    public boolean isInExplosionRange(int targetX, int targetY) {
        int[] explosionRange = getExplosionRange();

        for (int i = 0; i < explosionRange.length; i += 2) {
            if (explosionRange[i] == targetX && explosionRange[i + 1] == targetY) {
                return true;
            }
        }

        return false;
    }

    /**
     * 强制立即爆炸
     */
    public void forceExplode() {
        bomb.explode();
    }

    /**
     * 重置炸弹状态
     */
    public void reset() {
        bomb.setState(Bomb.BombState.PLACED);
        bomb.setFuseTime(bomb.getTotalFuseTime());
        bomb.setHasExploded(false);
    }

    /**
     * 获取炸弹实例
     */
    public Bomb getBomb() {
        return bomb;
    }

    /**
     * 设置炸弹实例
     */
    public void setBomb(Bomb bomb) {
        this.bomb = bomb;
    }
}