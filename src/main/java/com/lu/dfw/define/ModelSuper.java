package com.lu.dfw.define;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// // auto generate
public class ModelSuper<T> {
	protected final Map<Integer, T> map = new HashMap<>();

	protected final List<T> list = new ArrayList<>();

	public T get(int id) {
		return map.get(id);
	}//get单条数据，将数据放进map中方便拿单挑

	public List<T> getList() {
		return list;
	}//get所有数据，将数据放进list中方便拿所有数据
}
