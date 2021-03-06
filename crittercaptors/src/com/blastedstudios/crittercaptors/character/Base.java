package com.blastedstudios.crittercaptors.character;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.badlogic.gdx.math.Vector3;
import com.blastedstudios.crittercaptors.CritterCaptors;
import com.blastedstudios.crittercaptors.util.RenderUtil;
import com.blastedstudios.crittercaptors.util.LocationStruct;
import com.blastedstudios.crittercaptors.util.XMLUtil;

public class Base {
	public static final float BASE_DISTANCE = 10f;
	public static final int BASE_COST = 1500, RETARDANT_COST = 1000,
			VR_SIMULATOR_COST = 2000, VR_SIMULATOR_UPGRADE = 400;
	private static final String BASE_NAME = "base";
	public final LocationStruct loc;
	private Vector3 cachedPosition;//used to skip mercator proj every frame
	private final HashMap<BaseUpgradeEnum, Integer> upgrades;
	
	public Base(LocationStruct loc, HashMap<BaseUpgradeEnum, Integer> upgrades){
		this.loc = loc;
		this.upgrades = upgrades;
	}
	
	public Vector3 getCachedPosition(){
		return cachedPosition;
	}
	
	public void setCachedPosition(Vector3 cachedPosition){
		this.cachedPosition = cachedPosition;
	}
	
	public void render(){
		RenderUtil.drawModel(CritterCaptors.getModel(BASE_NAME), CritterCaptors.getTexture(BASE_NAME),
				cachedPosition, new Vector3());
	}

	public static Base fromXML(Element baseElement) {
		HashMap<BaseUpgradeEnum, Integer> upgrades = new HashMap<BaseUpgradeEnum, Integer>();
		for(Element upgradeElement : XMLUtil.iterableElementList(baseElement.getElementsByTagName("upgrade"))){
			BaseUpgradeEnum key = BaseUpgradeEnum.valueOf(upgradeElement.getAttribute("name"));
			upgrades.put(key, Integer.parseInt(upgradeElement.getAttribute("value")));
		}
		LocationStruct loc = new LocationStruct(Double.parseDouble(baseElement.getAttribute("lat")), 
				Double.parseDouble(baseElement.getAttribute("lon")));
		return new Base(loc, upgrades);
	}
	
	public Element asXML(Document doc){
		Element baseEle = doc.createElement("base");
		baseEle.setAttribute("lat", Double.toString(loc.lat));
		baseEle.setAttribute("lon", Double.toString(loc.lon));
		for(BaseUpgradeEnum key : upgrades.keySet()){
			Element upgradeElement = doc.createElement("upgrade");
			upgradeElement.setAttribute("name", key.name());
			upgradeElement.setAttribute("value", upgrades.get(key).toString());
			baseEle.appendChild(upgradeElement);
		}
		return baseEle;
	}

	public void upgrade(BaseUpgradeEnum upgrade) {
		upgrades.put(upgrade, upgrades.containsKey(upgrade) ? upgrades.get(upgrade)+1 : 0);
	}

	public boolean hasUpgrade(BaseUpgradeEnum upgrade) {
		return upgrades.containsKey(upgrade);
	}

	public Integer getUpgrade(BaseUpgradeEnum upgrade) {
		return upgrades.get(upgrade);
	}

	public void setRetardantEnabled(boolean enabled){
		upgrades.put(BaseUpgradeEnum.MonsterRetardant, enabled ? 1 : 0);
	}

	public boolean isRetardantEnabled(){
		return hasUpgrade(BaseUpgradeEnum.MonsterRetardant) && 
			getUpgrade(BaseUpgradeEnum.MonsterRetardant) == 1;
	}

	public int getVRLevel(){
		return getUpgrade(BaseUpgradeEnum.VirtualReality);
	}
}