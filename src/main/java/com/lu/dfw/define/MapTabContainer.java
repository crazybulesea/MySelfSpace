package com.lu.dfw.define;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
// auto generation, Do not change!!! 文件名: 1-地图数据.xlsx

public class MapTabContainer extends ModelSuper<MapTab> {
	public void Load() throws Exception {
		String property = System.getProperty("user.dir");
		String os = System.getProperty("os.name");
		String path;
		if (os.indexOf("Linux") != -1) {
			path = property + "/input/" + MapTab.class.getSimpleName() + ".bytes";
		} else {
			path ="D:/project/dafuweng/dfw/src/main/java/com/lu/dfw/input/" + MapTab.class.getSimpleName() + ".bytes";
		}

		File file = new File(path);
		if (!file.isFile()) {
			throw new RuntimeException(" File not found exception: " + MapTab.class.getName());
		}
		FileInputStream fileInputStream = new FileInputStream(file);
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
		try {
			while (dataInputStream.available() != 0) {
				MapTab bean = new MapTab();
				bean.LoadData(dataInputStream);
				if (bean.Id == 0 && list.size() != 0) {
					throw new RuntimeException("model file exception:" + MapTab.class.getName());
				}
				map.put(bean.Id, bean);
				list.add(bean);
			}
		} finally {
			dataInputStream.close();
			fileInputStream.close();
		}
	}
}