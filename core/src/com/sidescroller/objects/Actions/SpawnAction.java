package com.sidescroller.objects.Actions;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.utils.Array;
import com.sidescroller.game.SideScrollerGameV2;
import com.sidescroller.objects.Shape;

import java.util.ArrayList;

public class SpawnAction extends Action
{
    private Shape shape;
    private BodyDef.BodyType bodyType;
    private ArrayList<Boolean> tempFixtureSensorData;

    public SpawnAction(final int id, final Shape shape) {
		this.shape = shape;
		this.actionID = id;
		tempFixtureSensorData = new ArrayList<Boolean>(1);
		bodyType = shape.getBody().getType();
		shape.getBody().setType(BodyDef.BodyType.StaticBody);
		Array<Fixture> fixtures = shape.getBody().getFixtureList();
		for (Fixture fixture : fixtures){
			tempFixtureSensorData.add(fixture.isSensor());
			fixture.setSensor(true);
		}
	}

    public void act(){
		Array<Fixture> fixtures = shape.getBody().getFixtureList();
		for (int x = 0; x < fixtures.size; x++){
			if (tempFixtureSensorData.get(x) == false){
				fixtures.get(x).setSensor(false);
			}
		}

		shape.getBody().setType(bodyType);
		SideScrollerGameV2.getCurrentMap().addDrawObject(shape);
		SideScrollerGameV2.getCurrentMap().getActionManager().removeAction(this);
	}
}
