package com.lu.dfw.manager;

import com.lu.dfw.config.SpringBeanUtil;
import com.lu.dfw.Dao.PlayerMapper;
import com.lu.dfw.entity.Player;
import com.lu.dfw.entity.PlayerLocal;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 用来进行账号查询、账号数据缓存，不用频繁调用数据库
 */
public class PlayerManager {
    // 账号缓存
    private static final Map<String,Player> Players = new ConcurrentHashMap<>();

    //空账号缓存，防止数据库重复查询
    private static final Set<String> nullSet = new ConcurrentSkipListSet<>();

    public static void add(Player player){
        nullSet.remove(player.username);
        if(!Players.containsKey(player.username)){
            Players.put(player.username,player);
            PlayerMapper bean = SpringBeanUtil.getBean(PlayerMapper.class);
            bean.add(player);
        }
    }

    public static Player get(String username){
        if(nullSet.contains(username)){
            return null;
        }

        if(!Players.containsKey(username)){
            PlayerMapper bean = SpringBeanUtil.getBean(PlayerMapper.class);
            Player player = bean.getPlayerByName(username);
            if(player == null){
                nullSet.add(username);
                return null;
            }else{
                Players.put(username,player);
                player.local = PlayerLocal.Non;
                return player;
            }
        }else{
            return Players.get(username);
        }
    }
}
