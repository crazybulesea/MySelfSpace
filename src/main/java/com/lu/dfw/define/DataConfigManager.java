package com.lu.dfw.define;


// auto generation, Do not change!!!
public class DataConfigManager {

	public static EventTabContainer EventTabContainer = new EventTabContainer();
	public static MapTabContainer MapTabContainer = new MapTabContainer();
	public static BuildTypeContainer BuildTypeContainer = new BuildTypeContainer();
	public static ItemTabContainer ItemTabContainer = new ItemTabContainer();

	public static void init() throws Exception {
		System.out.println("Start load all config data...");

		System.out.println("EventTabContainer load...");
		EventTabContainer.Load();
		System.out.println("MapTabContainer load...");
		MapTabContainer.Load();
		System.out.println("BuildTypeContainer load...");
		BuildTypeContainer.Load();
		System.out.println("ItemTabContainer load...");
		ItemTabContainer.Load();

		System.out.println("DataConfigManager.init() over...");

	}
}
