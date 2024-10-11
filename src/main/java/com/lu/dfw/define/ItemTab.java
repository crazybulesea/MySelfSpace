package com.lu.dfw.define;

import java.io.DataInputStream;
// 3-道具表.xlsx
public class ItemTab {
	public int Id;
	public String Desc;
	public String Name;
	public int Price;
	public boolean CanMove;
	public int Type;
	public int[] Value;

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

		Price = dis.readInt();

		CanMove = dis.readInt() != 0;

		Type = dis.readInt();

		int Value1 = dis.readInt();
		Value = new int[Value1];
		for (int i = 0; i < Value1; i++) {
			Value[i] = dis.readInt();
		}

	}
}