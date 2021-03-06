package com.blastedstudios.crittercaptors.character;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.math.Vector3;
import com.blastedstudios.crittercaptors.creature.Creature;
import com.blastedstudios.crittercaptors.creature.CreatureManager;
import com.blastedstudios.crittercaptors.creature.Stats;
import com.blastedstudios.crittercaptors.creature.StatusEffectEnum;
import com.blastedstudios.crittercaptors.util.XMLUtil;

public class Character {
	private static final String SAVE_PATH = "Games/CritterCaptors/saves.xml";
	private String name;
	private int cash = 0;
	private List<Creature> ownedCreatures;
	private List<Base> bases;
	
	public Character(String name, int cash, List<Creature> ownedCreatures, 
			List<Base> bases){
		this.name = name;
		this.cash = cash;
		this.ownedCreatures = ownedCreatures;
		this.bases = bases;
	}
	
	public List<Creature> getOwnedCreatures(){
		return ownedCreatures;
	}

	public HashMap<Integer,Creature> getActiveCreatures(){
		HashMap<Integer,Creature> creatures = new HashMap<Integer,Creature>();
		for(Creature creature : ownedCreatures)
			if(creature.getActive() != -1)
				creatures.put(creature.getActive(), creature);
		return creatures;
	}
	
	public String getName(){
		return name;
	}
	
	public int getCash(){
		return cash;
	}
	
	public void addCash(int cash){
		this.cash += cash;
	}
	
	public List<Base> getBases(){
		return bases;
	}
	
	public Creature getNextActiveCreature(){
		HashMap<Integer, Creature> creatures = getActiveCreatures();
		for(int i : creatures.keySet())
			if(creatures.containsKey(i) && creatures.get(i).getHPCurrent() > 0)
				return creatures.get(i);
		return null;
	}
	
	public int getNextEmptyActiveIndex(){
		for(int i=0; i<6; i++){
			boolean used = false;
			for(int creatureIndex = 0; creatureIndex<ownedCreatures.size(); creatureIndex++)
				if(ownedCreatures.get(creatureIndex).getActive() == i)
					used = true;
			if(!used)
				return i;
		}
		return -1;
	}
	
	public static String[] getSavedCharactersNames(){
		Document saveFile = getCharacterSaveDocument();
		NodeList saveNodes = saveFile.getDocumentElement().getElementsByTagName("save");
		String[] names = new String[saveNodes.getLength()];
		int i=0;
		for(Element saveElement : XMLUtil.iterableElementList(saveNodes))
			names[i++] = saveElement.getAttribute("name");
		return names;
	}
	
	public static Character load(CreatureManager creatureManager, String name){
		ArrayList<Creature> ownedCreatures = new ArrayList<Creature>();
		int cash = 0;
		Document saveFile = getCharacterSaveDocument();
		for(Element saveElement : XMLUtil.iterableElementList(saveFile.getDocumentElement().getElementsByTagName("save")))
			if(saveElement.getAttribute("name").equals(name)){
				cash = Integer.parseInt(saveElement.getAttribute("cash"));
				for(Element creatureElement : XMLUtil.iterableElementList(saveElement.getElementsByTagName("creature"))){
					Creature creature = creatureManager.create(creatureElement.getAttribute("name"));
					creature.setExperience(Integer.parseInt(creatureElement.getAttribute("experience")));
					creature.setIV(Stats.fromXML((Element)creatureElement.getElementsByTagName("ivStats").item(0)));
					creature.setEV(Stats.fromXML((Element)creatureElement.getElementsByTagName("evStats").item(0)));
					creature.setHappiness(Integer.parseInt(creatureElement.getAttribute("happiness")));
					creature.setStatus(StatusEffectEnum.valueOf(creatureElement.getAttribute("status")));
					creature.heal();
					if(creatureElement.hasAttribute("active"))
						creature.setActive(Integer.parseInt(creatureElement.getAttribute("active")));
					ownedCreatures.add(creature);
				}
				List<Base> bases = new ArrayList<Base>();
				for(Element baseElement : XMLUtil.iterableElementList(saveElement.getElementsByTagName("base")))
					bases.add(Base.fromXML(baseElement));
				return new Character(name, cash, ownedCreatures, bases);
			}
		return null;
	}
	
	public void save(){
		Document saveFile = getCharacterSaveDocument();
		for(Element saveElement : XMLUtil.iterableElementList(saveFile.getDocumentElement().getElementsByTagName("save")))
			if(saveElement.getAttribute("name").equals(name))
				saveFile.getDocumentElement().removeChild(saveElement);
		Element saveElement = saveFile.createElement("save");
		saveElement.setAttribute("name", name);
		saveElement.setAttribute("cash", Integer.toString(cash));
		for(Creature owned : ownedCreatures){
			Node element = saveElement.appendChild(owned.asXML(saveFile));
			element.appendChild(owned.getIV().asXML(saveFile, "ivStats"));
			element.appendChild(owned.getEV().asXML(saveFile, "evStats"));
		}
		for(Base base : bases)
			saveElement.appendChild(base.asXML(saveFile));
		saveFile.getDocumentElement().appendChild(saveElement);
		XMLUtil.writeToFile(saveFile, SAVE_PATH);
	}
	
	private static Document getCharacterSaveDocument(){
		Document saveFile = XMLUtil.parse(SAVE_PATH, FileType.External);
		if(saveFile == null){
			saveFile = XMLUtil.create();
			Element optionsEle = saveFile.createElement("saves");
			saveFile.appendChild(optionsEle);
		}
		return saveFile;
	}

	public void sell(Creature creature) {
		cash += creature.getWorth();
		ownedCreatures.remove(creature);
	}

	public int blackout() {
		int lost = cash/10;
		cash -= lost;
		for(Creature creature : getOwnedCreatures()){
			creature.addHappiness(-1);
			creature.heal();
		}
		return lost;
	}
	
	public float getClosestRetardantDistance(Vector3 position){
		float closestDistance = Float.MAX_VALUE;
		for(Base base : bases)
			if(base.isRetardantEnabled() && base.getCachedPosition() != null){
				float distance =  base.getCachedPosition().tmp().dst(position);
				if(distance < closestDistance)
					closestDistance = distance;
			}
		return closestDistance;
	}
	
	public int getActiveRetardantCount(){
		int count = 0;
		for(Base base : bases)
			if(base.isRetardantEnabled())
				count++;
		return count;
	}
}
