package com.lu.dfw.define;

import java.io.DataInputStream;
// 1-地图数据.xlsx
public class MapTab {
	public int Id;
	public String Desc;
	public int Num;
	public int[] Local;
	public int[] Grid;
	public int[] Golds;
	public int[] Build;

	public void LoadData(DataInputStream dis) throws Exception {

		Id = dis.readInt();

		int Desc1 = dis.readInt();
		byte[] str1 = new byte[Desc1];
		dis.read(str1, 0, Desc1);
		Desc = new String(str1);

		Num = dis.readInt();

		int Local1 = dis.readInt();
		Local = new int[Local1];
		for (int i = 0; i < Local1; i++) {
			Local[i] = dis.readInt();
		}


		int Grid1 = dis.readInt();
		Grid = new int[Grid1];
		for (int i = 0; i < Grid1; i++) {
			Grid[i] = dis.readInt();
		}


		int Golds1 = dis.readInt();
		Golds = new int[Golds1];
		for (int i = 0; i < Golds1; i++) {
			Golds[i] = dis.readInt();
		}


		int Build1 = dis.readInt();
		Build = new int[Build1];
		for (int i = 0; i < Build1; i++) {
			Build[i] = dis.readInt();
		}

	}
}