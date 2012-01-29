package com.blastedstudios.crittercaptors.ui.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.blastedstudios.crittercaptors.CritterCaptors;
import com.blastedstudios.crittercaptors.ExperienceManager;
import com.blastedstudios.crittercaptors.creature.Creature;
import com.blastedstudios.crittercaptors.ui.AbstractScreen;
import com.blastedstudios.crittercaptors.ui.worldmap.WorldMap;
import com.blastedstudios.crittercaptors.util.Renderer;

public class BattleScreen extends AbstractScreen {
	private Creature enemy;
    private Camera camera;
    private CreatureInfoWindow creatureInfoWindow, enemyInfoWindow;
    private SpriteBatch spriteBatch;

	public BattleScreen(CritterCaptors game, Creature enemy) {
		super(game);
		this.enemy = enemy;
		creatureInfoWindow = new CreatureInfoWindow(game, skin, game.getCharacter().getActiveCreature(), 0, (int)stage.height()-200);
		enemyInfoWindow = new CreatureInfoWindow(game, skin, enemy, (int)stage.width()-236, 200);
		stage.addActor(creatureInfoWindow);
		stage.addActor(enemyInfoWindow);
		stage.addActor(new BottomMenu(game, skin, this));
		spriteBatch = new SpriteBatch();
	}
	
	@Override public void render(float arg0){
        camera.update();
        camera.apply(Gdx.gl10);
		
		Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		Renderer.drawSky(game.getModel("skydome"), game.getTexture("skydome"), camera.position);
		Renderer.drawModel(game.getModel(enemy.getName()), new Vector3(-3, 0, 10), 
				new Vector3(-1,0,-1), new Vector3(100f,100f,100f));
		Renderer.drawModel(game.getModel(enemy.getName()), new Vector3(1, 0, 3.5f), 
				new Vector3(1,0,1), new Vector3(100f,100f,100f));

		spriteBatch.begin();
		spriteBatch.end();
		stage.act(arg0);
		stage.draw();
	}

	public void fight(String name) {
		int enemyChoice = CritterCaptors.random.nextInt(enemy.getActiveAbilities().size());
		Creature playerCreature = game.getCharacter().getActiveCreature(); 
		if(enemy.receiveDamage(playerCreature.attack(enemy, name))){//enemy is dead
			//show window indicating victory!
			playerCreature.addExperience(ExperienceManager.getKillExperience(enemy));
			playerCreature.getEV().add(enemy.getEVYield());
			game.setScreen(new WorldMap(game));
		}else
			playerCreature.receiveDamage(enemy.attack(playerCreature, enemy.getActiveAbilities().get(enemyChoice).name));
		creatureInfoWindow.update();
		enemyInfoWindow.update();
		stage.addActor(new BottomMenu(game, skin, this));
	}

	@Override public void resize (int width, int height) {
        camera = Renderer.resize(width, height);
	}
}
