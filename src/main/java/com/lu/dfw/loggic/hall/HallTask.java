package com.lu.dfw.loggic.hall;

import com.lu.dfw.loggic.ATask;
import com.lu.dfw.thread.EThreadType;
import com.lu.dfw.thread.ThreadManager;

//为什么写这层抽象，为什么登录和注册不用写
//因为方便管理，管理在哪一场景下的功能，同时规范写法，后续大厅还有很多方法，所以我们直接写个父类，后面继承它就行了
//为什么登录注册不用，因为登录和注册就两个方法
public abstract class HallTask<T> extends ATask<T> {
    public int getThreadIndex() {
        return ThreadManager.getThreadIndex(EThreadType.Hall, player.uid);
    }
}
