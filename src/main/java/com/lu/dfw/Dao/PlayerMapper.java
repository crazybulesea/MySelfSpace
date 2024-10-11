package com.lu.dfw.Dao;

import com.lu.dfw.entity.Player;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PlayerMapper {
    @Insert("insert into t_player(`username`,`password`,`nickname`,`lv`,`createTime`)" +
            "values (#{username},#{password},#{nickname},#{lv},now())")
    @Options(useGeneratedKeys = true, keyProperty = "uid", keyColumn = "uid")
    void add(Player player);

    @Select("select * from t_player where `uid` = #{uid}")
    Player getPlayerById(int uid);

    @Select("select * from t_player where `username` = #{username}")
    Player getPlayerByName(String username);

    @Select("select count(`uid`) from `t_player` where `nickname` = #{nickname}")
    int getNickname(String nickname);

    @Update("update `t_player` set `password` = #{password},`nickname` = #{nickname},`lv` = #{lv},`model`= #{model} where `uid` = #{uid}")
    void update(Player player);

}
