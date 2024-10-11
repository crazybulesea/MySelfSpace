package com.lu.dfw.manager;

import com.lu.dfw.config.Const;
import com.lu.dfw.define.BuildType;
import com.lu.dfw.define.DataConfigManager;
import com.lu.dfw.define.MapTab;
import com.lu.dfw.entity.Grid;
import com.lu.dfw.entity.MapData;
import com.lu.dfw.entity.Player;
import com.lu.dfw.entity.PlayerLocal;
import com.lu.dfw.loggic.ATask;
import com.lu.dfw.loggic.ECode;
import com.lu.dfw.proto.Battle;
import com.lu.dfw.proto.Role;
import com.lu.dfw.utils.RandomUtil;

import java.util.ArrayList;
import java.util.List;

public class RoomManager {
    public int id;          // 房间唯一id
    public int threadId;    // 对局线程id
    public RoomStatus roomStatus;       // 房间当前状态
    public BattleStatus battleStatus;   // 对局状态

    /**
     * 对局的玩家,0为先手,list 0为第一个出手的玩家,list 1为第二个。。。。
     */
    private List<Player> Players = new ArrayList<>();

    /**
     * 观战玩家
     */
    private List<Player> LookPlayer = new ArrayList<>();

    /**
     * 当前操作的 玩家 标记,从0开始,每次+1; 也用于记录 本局 一共执行的轮数
     */
    private int Index;


    /**
     * 当前玩家
     */
    private Player nowPlayer;

    // 下次 执行 时间
    public long nextRun;

//    // 地图实时数据
    private final MapData mapData = new MapData();
//    // 按照 index 角色拥有的对局道具
//    private final BattleItemManager itemManager = new BattleItemManager();
    // 地图配置表
    private MapTab mapTab;

    /**
     * 当一个玩家可以 购买道具时,每次购买放入 这个list; 结束后,将所有道具放入玩家身上;
     */
    private List<Integer> nowBuyList = new ArrayList<>();

    public RoomManager(int id){
        this.id = id;
    }

    public void init(List<Player> ps ,int mapId){
        LoggerManager.info("房间初始化");
        long nowTime = System.currentTimeMillis();
        roomStatus = RoomStatus.Init;
        Index = 0;
        for (Player player : ps){
            player.room = this;
            player.local = PlayerLocal.Battle;
            player.battleData.rid = id;
        }

        Players.clear();
        //随机确定 出手顺序
        for (int i = 0,length = ps.size(); i < length; i++){
            int ran = 0;//RandomUtil.getRandom(player.size());
            Player remove = ps.remove(ran);
            Players.add(remove);
        }
        mapTab = DataConfigManager.MapTabContainer.get(mapId);

        mapData.init(Players, mapTab);

        Battle.BattleBeginResponse.Builder builder = Battle.BattleBeginResponse.newBuilder();
        for (Player player : Players){
            Role.RoleInfo build = player.build();
            builder.addInfos(build);
        }

        builder.setMap(mapId);
        byte[] bytes = builder.build().toByteArray();
        sendMsgToAll(ECode.BattleBegin,bytes);

        roomStatus = RoomStatus.Battle;
        battleStatus = BattleStatus.AwaitRoll;

        // 2秒后 通知客户端 开始 roll; 2秒是为了等待 客户端 加载进入战斗场景;也可以用其他方式来 解决
        nextRun = nowTime + 2000;
    }

    public void Update() {
        if(roomStatus == RoomStatus.Battle){
            Battle();
        }else if(roomStatus == RoomStatus.Over){
            //CLOSE ROOM
        }
    }

    private void Battle() {
        switch (battleStatus) {
            case AwaitRoll -> {
                sendRoll();
            }
            case AwaitRollSend -> {
                // 如果玩家在10秒内roll了,就不会执行这里; 这里自动roll
                playerRoll(nowPlayer, 0);
            }
            case MoveIng -> {
                moveEnd(nowPlayer);
            }
            case AwaitSel -> {
                awaitSel(nowPlayer, 0);
            }
            case AwaitShop -> {
//                battleShop(nowPlayer, 0);
            }

        }
    }

    /*
     * 客户端通知服务器，自己ROll了，0，就是默认，随机roll
     */
    public void playerRoll(Player player, int id) {
        if(!check(player, BattleStatus.AwaitRollSend)){
            return;
        }
        int roll = 0;
        //0是随机roll
        if(id == 0){
            roll = RandomUtil.getRoll();
        }else if(id == 1){
            roll = 1;
        }

        mapData.playerMove(getNowIndex(), roll);

        //假设 每走一格耗时0.4秒
        int time = (int)(roll * 0.4f) + 1;

        // 在 time事件内,玩家 通知服务器 自己移动完成;如果没通知,则会被 update执行
        nextRun = System.currentTimeMillis() + time * 1000L * 2;
        battleStatus = BattleStatus.MoveIng;

        Battle.RoleRollResponse.Builder builder = Battle.RoleRollResponse.newBuilder();
        Battle.RoleRollResponse build = builder.setUid(player.uid).setMove(roll).build();
        sendMsgToAll(ECode.RoleRoll, build.toByteArray());

    }

    public void moveEnd(Player player) {
        if(!check(player, BattleStatus.MoveIng)){
            return;
        }

        Grid grid = mapData.getPlayerGrid(getNowIndex());

        Battle.MoveTargetEndResponse.Builder builder = Battle.MoveTargetEndResponse.newBuilder();
        Battle.MoveTargetEndResponse build;
        int gold = mapData.getGold(getNowIndex());

        if(grid.type == 0){//空地，买地没有做判断，前端自己做了
            build = builder.setTime(Const.AwaitTime).setUid(player.uid).build();
            battleStatus = BattleStatus.AwaitSel;
            nextRun = System.currentTimeMillis() + Const.AwaitTime * 1000;
            sendMsgToAll(ECode.MoveTargetEnd, build.toByteArray());
        }else if(grid.type == 1){//事件
            actionEnd();
        }else if (grid.type == 2) {//商店
            actionEnd();
        }else {
            BuildType tab = DataConfigManager.BuildTypeContainer.get(grid.type);

            if(getNowIndex() != grid.index){//地块是否为自己的
                //交路费
                int moveGlod = tab.MoveGold[grid.lv - 1];
                //用地者扣钱
                mapData.addGold(getNowIndex(), -moveGlod);
                //地主赚钱
                mapData.addGold(grid.index, moveGlod);

                build = builder.setTime(0).setUid(nowPlayer.uid).build();
                sendMsgToAll(ECode.MoveTargetEnd, build.toByteArray());

                checkDie(nowPlayer);
                actionEnd();
            }else {//是，是否升级
                if(grid.lv < tab.BuildGold.length ){//没有满级
                    //可以升级
                    if(gold < tab.BuildGold[grid.lv]){//钱不够
                        actionEnd();
                    }else{//发送可以升级的信息
                        build = builder.setTime(Const.AwaitTime).setUid(nowPlayer.uid).build();
                        battleStatus = BattleStatus.AwaitSel;
                        nextRun = System.currentTimeMillis() + Const.AwaitTime * 1000;
                        sendMsgToAll(ECode.MoveTargetEnd, build.toByteArray());
                    }
                }else{
                    //满级
                    actionEnd();
                }
                //这个建筑是自己的，是否升级
            }
        }

    }

    //用来通知该谁来roll了
    private void sendRoll() {
        nowPlayer = Players.get(getNowIndex());
        Battle.SendRollResponse.Builder builder = Battle.SendRollResponse.newBuilder();
        Battle.SendRollResponse build =
                builder.setUid(nowPlayer.uid).setGood(0/*mapData.getGood(getNowIndex())*/).setTime(Const.AwaitTime).build();
        sendMsgToAll(ECode.SendRollRoll, build.toByteArray());

        battleStatus = BattleStatus.AwaitRollSend;
        nextRun = System.currentTimeMillis() + Const.AwaitTime * 1000;// 最多11秒内 必须roll

    }

    private void actionEnd() {
        // 下一个玩家开始 行动,先通知 MapData,该玩家行动结束了
//        mapData.MoveEnd(getNowIndex());

        Index++;
//        int sign = mapTab.Num; //
//        while (sign > 0) {
//            boolean dieStatus = mapData.getPlayerDieStatus(Index % Players.size());
//            if (!dieStatus) {
//                Index++;
//                sign--;
//            } else {
//                break;
//            }
//        }

        sendRoll();
    }

    /*
     * 获取当前行动玩家在Players中的下标,Players是存的是角色出手的顺序，所以也就是正在操作玩家的下标
     */
    private int getNowIndex() {
        // 0 % 4 = 0 1 % 4 = 1  2 % 4 = 2  3 % 4 = 3  4 % 4 = 0
        return Index % Players.size();
    }

    public void sendMsgToAll(ECode code,byte[] msg){
        LoggerManager.info("房间广播:房间内有人活动了一次");
        for(Player player : Players){
            if(player.battleData.rid == id){
                ATask.sendMsgToOne(player.uid,code,msg);
            }
        }
        ATask.sendMsgToList(LookPlayer, code, msg);
    }

    private boolean check(Player player, BattleStatus status) {
        if (player.uid != Players.get(getNowIndex()).uid) {
            return false;
        }
        // 如果不是 等待玩家 roll的状态,不处理
        if (battleStatus != status) {
            return false;
        }
        return true;
    }

    public void awaitSel(Player player, int event) {
        LoggerManager.info("进入等待玩家操作步骤中");
        if(!check(player, BattleStatus.AwaitSel)){
            return;
        }
        Grid grid = mapData.getPlayerGrid(getNowIndex());
        BuildType buildType = DataConfigManager.BuildTypeContainer.get(event);

        if (grid.type == 0){//判断是否为空地
            if(event != 0){//event购买事件，0为不购买，1为购买
                if(buildType == null){
                    return;
                }

                if(mapData.getGold(getNowIndex()) >= buildType.BuildGold[0]){
                    mapData.addGold(getNowIndex(), -buildType.BuildGold[0]);
                    grid.type = event;
                    grid.index = getNowIndex();
                    grid.lv = 1;
                }else{
                    actionEnd();
                    return;
                }
            }
        }else if(grid.type == 1){//事件
            actionEnd();
        }else if (grid.type == 2) {//商店
            actionEnd();
        }else {
            if(event != 0){//升级地块
                if(mapData.getGold(getNowIndex()) < buildType.BuildGold[0]){
                    return;
                }
                BuildType tab = DataConfigManager.BuildTypeContainer.get(grid.type);
                //扣钱，
                mapData.addGold(getNowIndex(), -tab.BuildGold[grid.lv]);
                grid.lv++;
            }
        }
        Battle.FuncResponse.Builder builder = Battle.FuncResponse.newBuilder();
        Battle.FuncResponse build = builder.setUid(nowPlayer.uid).setEvent(event).build();
        sendMsgToAll(ECode.FuncRequest, build.toByteArray());
        actionEnd();
    }

    public void leave(Player player) {
    }

    private void checkDie(Player player) {
        if (!mapData.getPlayerDieStatus(getIndex(player))) {
            LoggerManager.info(nowPlayer.nickname + "钱花完了,通知死亡");
            // 通知所有人,该玩家 失败了
            playerOver(nowPlayer);
        }
    }

    /**
     * 该玩家 对局结束了
     *
     * @param player
     */
    private void playerOver(Player player) {
        Battle.BattleOverResponse.Builder builder = Battle.BattleOverResponse.newBuilder();
        Battle.BattleOverResponse build = builder.setOver(false).setUid(player.uid).build();
        sendMsgToAll(ECode.BattleOver, build.toByteArray());

        // 检查对局是否 结束了; 当对局中只有一个玩家还 存活,就结束了
        int index = -1;// 存活的角色 下标;
        for (int i = 0; i < Players.size(); i++) {
            if (mapData.getPlayerDieStatus(i)) {
                if (index >= 0) {
                    // 超过1个人存活
                    return;
                } else {
                    index = i;
                }
            }
        }
        // 只有一个人存活了
        builder = Battle.BattleOverResponse.newBuilder();
        build = builder.setOver(true).setUid(Players.get(index).uid).build();
        sendMsgToAll(ECode.BattleOver, build.toByteArray());
        // 修改房间状态为 结束; 10秒后 关闭
        roomStatus = RoomStatus.Over;
        battleStatus = BattleStatus.Non;// 对局结束了,不再处理请求
        nextRun = System.currentTimeMillis() + 10 * 1000;
        // 做统计等数据;
    }

    private int getIndex(Player player) {
        for (int i = 0; i < Players.size(); i++) {
            if (Players.get(i).uid == player.uid) {
                return i;
            }
        }
        return -1;
    }

    public void battleShop(Player player, int cid) {
    }
}
