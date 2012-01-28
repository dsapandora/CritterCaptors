package com.blastedstudios.crittercaptors.ui.battle;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.blastedstudios.crittercaptors.CritterCaptors;
import com.blastedstudios.crittercaptors.creature.Ability;

public class FightMenu extends Window {

	public FightMenu(final CritterCaptors game, final Skin skin, final BattleScreen battleScreen) {
		super("Fight", skin);
		final List<Button> attackButtons = new ArrayList<Button>();
		for(Ability ability : game.getCharacter().getActiveCreature().getActiveAbilities())
			attackButtons.add(new TextButton(ability.name, skin.getStyle(TextButtonStyle.class), ability.name));
		final Button cancelButton = new TextButton("Cancel", skin.getStyle(TextButtonStyle.class), "cancel");
		//final Button creaturesButton = new TextButton("Creatures", skin.getStyle(TextButtonStyle.class), "creatures");
		cancelButton.setClickListener(new ClickListener() {
			@Override public void click(Actor actor, float arg1, float arg2) {
				actor.getStage().addActor(new BottomMenu(game, skin, battleScreen));
				actor.getStage().removeActor(actor.parent);
			}
		});
		for(Button attackButton : attackButtons)
			attackButton.setClickListener(new ClickListener() {
				@Override public void click(Actor actor, float arg1, float arg2) {
					battleScreen.fight(actor.name);
					actor.getStage().removeActor(actor.parent);
				}
			});
		add(attackButtons.get(0));
		if(attackButtons.size()>2)
			add(attackButtons.get(2));
		row();
		if(attackButtons.size()>1)
			add(attackButtons.get(1));
		if(attackButtons.size()>3)
			add(attackButtons.get(3));
		//add(creaturesButton);
		//row();
		add(cancelButton);
		pack();
		x = Gdx.graphics.getWidth() / 2 - width / 2;
		y = Gdx.graphics.getHeight() / 2 - height / 2;
	}

}
