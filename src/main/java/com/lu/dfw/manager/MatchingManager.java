package com.lu.dfw.manager;

import com.lu.dfw.config.Const;
import com.lu.dfw.define.DataConfigManager;
import com.lu.dfw.define.MapTab;
import com.lu.dfw.entity.Player;
import com.lu.dfw.entity.PlayerLocal;
import com.lu.dfw.network.Connection;
import com.lu.dfw.network.ConnectionManager;
import com.lu.dfw.thread.EThreadType;
import com.lu.dfw.thread.ThreadManager;
import com.lu.dfw.thread.ThreadTask;
import com.lu.dfw.thread.Threads;

import java.util.*;

/**
 * @Author qzj
 * @Data 2023/9/23 23:06
 * @Description 匹配管理器, 该管理器由 匹配线程 负责;
 * @Version 1.0
 */
public class MatchingManager {

    private static Threads threads;// 匹配线程
    // 所有匹配的玩家; key对应地图ID; Value对应 匹配的玩家列表
    private static Map<Integer, List<Integer>> matchPlayers = new HashMap<>();
    private static int indexAdd = 1;        // 房间自增id

    /**
     * 对局房间;
     */
    private static List<RoomManager> rooms = new ArrayList<>();

    // 下标代表 线程id; 值表示该线程负责的房间数量
    private static int[] roomNum = new int[Const.Game];


    public static void init(Threads threads) {
        MatchingManager.threads = threads;
        // 读取配置表 中的地图标
        List<MapTab> mapTabs = DataConfigManager.MapTabContainer.getList();
        for (MapTab tab : mapTabs) {
            matchPlayers.put(tab.Id, new LinkedList<>());
        }
    }

    /***
     * 不停的循环 所有的房间;
     */
    public static void update() {
        threads.push(new ThreadTask() {
            @Override
            public void execute() {
                long now = System.currentTimeMillis();
                for (RoomManager room : rooms) {
                    Threads threads = ThreadManager.getThreads(EThreadType.Battle, room.threadId);
                    threads.push(new ThreadTask() {
                        @Override
                        public void execute() {
                            if (now >= room.nextRun) {
                                room.Update();
                            }
                        }
                    });
                }
            }
        });
    }

    public static void openBattle() {
        threads.push(new ThreadTask() {
            @Override
            public void execute() {
                for (Map.Entry<Integer, List<Integer>> entry : matchPlayers.entrySet()) {
                    List<Integer> players = matchPlayers.get(entry.getKey());
                    MapTab tab = DataConfigManager.MapTabContainer.get(entry.getKey());
                    // 创建房间
                    while (players.size() >= tab.Num) {
                        // 检查是 是否 有空 房间可以使用
                        RoomManager room = getRoom();
                        // 没有 空房间/线程全满了
                        if (room == null) {
                            // 通知管理员,没有房间可用了,检查系统资源是否足够开启更多线程/房间,如果不够,增加配置
                            return;
                        }
                        List<Player> ps = new ArrayList<>();

                        for (int i = 0; i < tab.Num; i++) {
                            int p1 = players.remove(0);
                            Connection conn = ConnectionManager.getNetConn(p1);
                            ps.add(conn.player);
                        }
                        room.init(ps, entry.getKey());
                    }
                }
            }
        });
    }

    public static void add(Player player, int mapId) {
        /* if(player.point != PlayerLocal.Hall){
                return;
            }*/

        if (player.battleData.mid != 0) {
            return;
        }
        // 将一个玩家 加入到 对应的地图 匹配队列中
        if (!matchPlayers.containsKey(mapId) || matchPlayers.get(mapId).contains(player.uid)) {
            return;
        }

        player.local = PlayerLocal.Match;
        player.battleData.mid = mapId;
        matchPlayers.get(mapId).add(player.uid);

    }


    /**
     * 离开 匹配队列,但是并不一定能成功(已经在创建房间了,则不能离开
     *
     * @param player
     * @return
     */
    public static boolean leave(Player player) {
        // 没有在对局状态
        if (player.local != PlayerLocal.Battle) {
            player.local = PlayerLocal.Hall;
            matchPlayers.get(player.battleData.mid).remove(Integer.valueOf(player.uid));
            player.battleData.mid = 0;
            return true;
        }
        return false;
    }


    /**
     * 获取 管理房间最少的 线程id
     *
     * @return
     */
    private static RoomManager getRoom() {
        int thread = 0;
        int count = Const.RoomNum;
        for (int i = 0; i < roomNum.length; i++) {
            if (roomNum[i] < count) {
                count = roomNum[i];
                thread = i;
            }
        }
        // 全部线程都满了
        if (count == Const.RoomNum) {
            return null;
        }

        for (RoomManager room : rooms) {
            if (room.roomStatus == RoomStatus.Non) {
                room.threadId = thread;
                roomNum[thread] += 1;
                return room;
            }
        }

        RoomManager rmg = new RoomManager(indexAdd++);
        rmg.threadId = thread;
        roomNum[thread] += 1;
        rooms.add(rmg);
        return rmg;
    }

}
