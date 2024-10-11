package com.lu.dfw.entity;

import com.lu.dfw.define.DataConfigManager;
import com.lu.dfw.define.EventTab;
import com.lu.dfw.define.MapTab;
import com.lu.dfw.utils.RandomUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 卢博文 
 * @version 1.0
 * @description 一个房间中, 地图的数据
 */
public class MapData {
    public List<Grid> grids;//地图格子数量
    public int[] local;// 角色所在位置 10，15
    public int[] golds;// 每个玩家所拥有的 钱;如果小于0 表示已经死亡 10000，50000
    public int[] good; // 好运剩余回合

    public void init(List<Player> players, MapTab tab) {
        //初始位置
        local = new int[players.size()];
        for (int i = 0; i < players.size(); i++) {
            local[i] = tab.Local[i];//玩家初始所在地方
        }

        //初始格子
        grids = new ArrayList<>(tab.Grid.length);
        for (int i = 0; i < tab.Grid.length; i++) {
            Grid grid = new Grid(tab.Grid[i]);//从配置表里拿格子数据
            grids.add(grid);
        }

        //初始钱
        golds = new int[players.size()];
        for (int i = 0; i < golds.length; i++) {
            golds[i] = tab.Golds[i];//从配置表里拿钱
        }

        //好运卡回合
        good = new int[players.size()];
    }

    public Grid getPlayerGrid(int playerIndex) {
        return grids.get(local[playerIndex]);
    }

    /**
     * 当 某个玩家移动后
     *
     * @param playerIndex 玩家在 Players 中的 下标
     * @param move
     */
    public void playerMove(int playerIndex, int move) {
        local[playerIndex] += move;
        local[playerIndex] %= grids.size();//地图24格，24 % 24 = 0回到起点
    }

    /**
     * 某个玩家的金币发生了变化; 减少就传负数
     *
     * @param playerIndex
     * @param gold
     * @return 如果不够 扣除,就会返回 false
     */
    public void addGold(int playerIndex, int gold) {
        // 给某个角色 增加金币,如果处于死亡状态,则不会增加
        if (gold > 0) {
            if (golds[playerIndex] < 0) {
                return;
            }
        }

        golds[playerIndex] += gold;
        // return golds[playerIndex] >= 0;
    }

    public int getGold(int playerIndex) {
        return golds[playerIndex];
    }

    /**
     * 获得一个 下标的玩家的存活状态;
     *
     * @param playerIndex
     * @return 活着就返回true
     */
    public boolean getPlayerDieStatus(int playerIndex) {
        if (playerIndex < 0 || playerIndex > golds.length - 1) {
            return false;
        }
        return golds[playerIndex] >= 0;
    }


    public int getNextEventMove(int index) {
        return getNextType(index, 1);
    }

    public int getNextShopMove(int index) {
        return getNextType(index, 2);
    }

    /**
     * 获取一个玩家 的 下一个 类型(事件点,商店)的位置
     *
     * @return
     */
    private int getNextType(int index, int type) {
        int nowLocal = local[index];
        int move = 1;
        while (true) {
            if (grids.get((nowLocal + move) % grids.size()).type == type) {
                return move;
            }
            move++;
        }
    }

    /**
     * 好运回合，角色行动结束后,处理一次
     *
     * @param index
     */
    public void MoveEnd(int index) {
        if (good[index] > 0) {
            good[index]--;
        }
    }

    /**
     * 给一个玩家设置好运 回合; 不叠加,直接覆盖
     */
    public void addGood(int index, int count) {
        good[index] = count;
    }

    public int getGood(int index) {
        if (index < 0 || index >= good.length) {
            return -1;
        }
        return good[index];
    }

    /**
     * 返回 事件表的id
     *
     * @param index
     * @return
     */
    public int goodLuck(int index) {
        List<EventTab> list = DataConfigManager.EventTabContainer.getList();
        int eventId = 0;
        if (good[index] <= 0) {
            int count = 0;  // 总概率
            for (EventTab tab : list) {
                count += tab.Prob;
            }
            int random = RandomUtil.getRandom(1, count);// 1-count
            for (; eventId < list.size(); eventId++) {
                random -= list.get(eventId).Prob;
                if (random <= 0) {
                    break;
                }
            }
        } else {
            int count = 0;  // 总概率
            for (EventTab tab : list) {
                count += tab.ProbUp;
            }
            int random = RandomUtil.getRandom(1, count);// 1 - count
            for (; eventId < list.size(); eventId++) {
                random -= list.get(eventId).ProbUp;
                if (random <= 0) {
                    break;
                }
            }
        }
        return eventId;
    }

}
