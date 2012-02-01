package com.blastedstudios.crittercaptors.ui.worldmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.blastedstudios.crittercaptors.CritterCaptors;

public class BuildBaseWindow extends Window {

	public BuildBaseWindow(final CritterCaptors game, final Skin skin) {
		super("Build base", skin);
		final String[] items = new String[]{"Are you sure you want","to build a base here?"};
		final Button buildBaseButton = new TextButton("Build", skin.getStyle(TextButtonStyle.class), "build");
		final Button cancelButton = new TextButton("Cancel", skin.getStyle(TextButtonStyle.class), "cancel");
		final List textList = new List(items,skin);
		buildBaseButton.setClickListener(new ClickListener() {
			@Override public void click(Actor actor, float arg1, float arg2) {
				game.addBase();
				actor.getStage().removeActor(actor.parent);
			}
		});
		cancelButton.setClickListener(new ClickListener() {
			@Override public void click(Actor actor, float arg1, float arg2) {
				actor.getStage().removeActor(actor.parent);
			}
		});
		add(textList);
		row();
		add(buildBaseButton);
		row();
		add(cancelButton);
		pack();
		x = Gdx.graphics.getWidth()/2 - width/2;
		y = Gdx.graphics.getHeight()/2 - height/2;
	}
}