package com.lu.dfw.define;


import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
// auto generation, Do not change!!! 文件名: 1-事件表.xlsx

public class EventTabContainer extends ModelSuper<EventTab> {
	public void Load() throws Exception {
		String property = System.getProperty("user.dir");
		String os = System.getProperty("os.name");
		String path;
		if (os.indexOf("Linux") != -1) {
			path = property + "/input/" + EventTab.class.getSimpleName() + ".bytes";
		} else {
			path ="D:/project/dafuweng/dfw/src/main/java/com/lu/dfw/input/" + EventTab.class.getSimpleName() + ".bytes";
		}

		File file = new File(path);
		if (!file.isFile()) {
			throw new RuntimeException(" File not found exception: " + EventTab.class.getName());
		}
		FileInputStream fileInputStream = new FileInputStream(file);
		DataInputStream dataInputStream = new DataInputStream(fileInputStream);
		try {
			while (dataInputStream.available() != 0) {
				EventTab bean = new EventTab();
				bean.LoadData(dataInputStream);
				if (bean.Id == 0 && list.size() != 0) {
					throw new RuntimeException("model file exception:" + EventTab.class.getName());
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