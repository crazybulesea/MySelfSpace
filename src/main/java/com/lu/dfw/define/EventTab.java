package com.lu.dfw.define;

import java.io.DataInputStream;
// 1-事件表.xlsx
public class EventTab {
	public int Id;
	public String Title;
	public String Desc;
	public int Type;
	public int Value;
	public int Prob;
	public int ProbUp;

	public void LoadData(DataInputStream dis) throws Exception {

		Id = dis.readInt();

		int Title1 = dis.readInt();
		byte[] str1 = new byte[Title1];
		dis.read(str1, 0, Title1);
		Title = new String(str1);

		int Desc1 = dis.readInt();
		byte[] str2 = new byte[Desc1];
		dis.read(str2, 0, Desc1);
		Desc = new String(str2);

		Type = dis.readInt();

		Value = dis.readInt();

		Prob = dis.readInt();

		ProbUp = dis.readInt();
	}
}