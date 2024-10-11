package com.lu.dfw.define;

import java.io.DataInputStream;
// 2-建筑升级表.xlsx
public class BuildType {
	public int Id;
	public String Desc;
	public String Name;
	public int[] BuildGold;
	public int[] MoveGold;

	public void LoadData(DataInputStream dis) throws Exception {

		Id = dis.readInt();

		int Desc1 = dis.readInt();
		byte[] str1 = new byte[Desc1];
		dis.read(str1, 0, Desc1);
		Desc = new String(str1);

		int Name1 = dis.readInt();
		byte[] str2 = new byte[Name1];
		dis.read(str2, 0, Name1);
		Name = new String(str2);

		int BuildGold1 = dis.readInt();
		BuildGold = new int[BuildGold1];
		for (int i = 0; i < BuildGold1; i++) {
			BuildGold[i] = dis.readInt();
		}


		int MoveGold1 = dis.readInt();
		MoveGold = new int[MoveGold1];
		for (int i = 0; i < MoveGold1; i++) {
			MoveGold[i] = dis.readInt();
		}

	}
}