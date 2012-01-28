package com.blastedstudios.crittercaptors.ui.battle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.blastedstudios.crittercaptors.CritterCaptors;
import com.blastedstudios.crittercaptors.ExperienceManager;
import com.blastedstudios.crittercaptors.creature.Creature;
import com.blastedstudios.crittercaptors.ui.AbstractScreen;
import com.blastedstudios.crittercaptors.ui.worldmap.WorldMap;

public class BattleScreen extends AbstractScreen {
	private Creature enemy;
    private Camera camera;
    private CreatureInfoWindow creatureInfoWindow, enemyInfoWindow;

	public BattleScreen(CritterCaptors game, Creature enemy) {
		super(game);
		this.enemy = enemy;
		creatureInfoWindow = new CreatureInfoWindow(game, skin, game.getCharacter().getActiveCreature(), 0, (int)stage.height()-200);
		enemyInfoWindow = new CreatureInfoWindow(game, skin, enemy, (int)stage.width()-200, 200);
		stage.addActor(creatureInfoWindow);
		stage.addActor(enemyInfoWindow);
		stage.addActor(new BottomMenu(game, skin, this));
	}
	
	@Override public void render(float arg0){
		if (Gdx.input.isKeyPressed(Keys.A))
			camera.rotate(100 * Gdx.graphics.getDeltaTime(), 0, 1, 0);
		if (Gdx.input.isKeyPressed(Keys.D))
			camera.rotate(-100 * Gdx.graphics.getDeltaTime(), 0, 1, 0);
		
		Gdx.gl10.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		Gdx.gl10.glEnable(GL10.GL_TEXTURE_2D);
		Gdx.gl10.glEnable(GL10.GL_DEPTH_TEST);
		Gdx.gl10.glFrontFace(GL10.GL_CW);

		//render enemy
		Gdx.gl10.glPushMatrix();
		Gdx.gl10.glTranslatef(2, 0, 6);
		Gdx.gl10.glScalef(100f, 100f, 100f);
		game.getModel(enemy.getName()).render();
		Gdx.gl10.glPopMatrix();

		//render own
		Gdx.gl10.glPushMatrix();
		Gdx.gl10.glTranslatef(-2, 0, -6);
		Gdx.gl10.glScalef(100f, 100f, 100f);
		game.getModel(game.getCharacter().getActiveCreature().getName()).render();
		Gdx.gl10.glPopMatrix();
		
		stage.act(arg0);
		stage.draw();
	}

	public void fight(String name) {
		int enemyChoice = CritterCaptors.random.nextInt(enemy.getActiveAbilities().size());
		Creature playerCreature = game.getCharacter().getActiveCreature(); 
		if(enemy.receiveDamage(playerCreature.attack(enemy, name))){//enemy is dead
			//show window indicating victory!
			playerCreature.addExperience(ExperienceManager.getKillExperience(enemy));
			game.setScreen(new WorldMap(game));
		}else
			playerCreature.receiveDamage(enemy.attack(playerCreature, enemy.getActiveAbilities().get(enemyChoice).name));
		creatureInfoWindow.update();
		enemyInfoWindow.update();
		stage.addActor(new BottomMenu(game, skin, this));
	}

	@Override public void resize (int width, int height) {
        float aspectRatio = (float) width / (float) height;
        camera = new PerspectiveCamera(67, 2f * aspectRatio, 2f);
        camera.translate(40,40,100);
        camera.lookAt(0, 0, 0);
        camera.update();
        camera.apply(Gdx.gl10);
	}
}
