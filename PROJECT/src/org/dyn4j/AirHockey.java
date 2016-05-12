package org.dyn4j;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;

import org.dyn4j.ExampleGraphics2D.GameObject;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

public final class AirHockey extends SimulationFrame 
{	
	public AirHockey(String PlayerName, String ConnectToIP, double TimeInMin) 
	{
		super("Air Hockey 2K16", 1.0);
		this.PlayerName = PlayerName;
		this.ConnectToIP = ConnectToIP;
		this.TimeInMin = TimeInMin;
	}
	
	protected void initializeWorld()
	{

		this.world.setGravity(World.ZERO_GRAVITY);
				
		SimulationBody up = new SimulationBody(Color.darkGray);
		up.addFixture(Geometry.createRectangle(1125, 22.5));
		up.translate(652.5, 80);
		up.setMass(MassType.INFINITE);
		world.addBody(up);
		
		SimulationBody bottom = new SimulationBody(Color.darkGray);
		bottom.addFixture(Geometry.createRectangle(1125, 22.5));
		bottom.translate(652.5, 620);
		bottom.setMass(MassType.INFINITE);
		world.addBody(bottom);
		
		SimulationBody left = new SimulationBody(Color.darkGray);
		left.addFixture(Geometry.createRectangle(22.5, 517.5));
		left.translate(101.25, 350);
		left.setMass(MassType.INFINITE);
		world.addBody(left);
		
		SimulationBody right = new SimulationBody(Color.darkGray);
		right.addFixture(Geometry.createRectangle(22.5, 517.5));
		right.translate(1203.75, 350);
		right.setMass(MassType.INFINITE);
		world.addBody(right);
	
		
		SimulationBody mallet1 = new SimulationBody(Color.gray);
		mallet1.addFixture(Geometry.createCircle(38.25), 200, 1, 1);
		mallet1.translate(905, 350);
		mallet1.setLinearVelocity(30000, 40000);
		mallet1.setMass(MassType.NORMAL);
		mallet1.setLinearDamping(0.0);
		mallet1.setAngularDamping(0.0);
		mallet1.setAutoSleepingEnabled(false);
		this.world.addBody(mallet1);
		
		SimulationBody mallet2 = new SimulationBody(Color.gray);
		mallet2.addFixture(Geometry.createCircle(38.25), 200, 1, 1);
		mallet2.translate(240, 350);
		mallet2.setLinearVelocity(0, 0);
		mallet2.setMass(MassType.NORMAL);
		mallet2.setLinearDamping(0.0);
		mallet2.setAngularDamping(0.0);
		mallet2.setAutoSleepingEnabled(false);
		this.world.addBody(mallet2);
		
		SimulationBody puck1 = new SimulationBody(Color.red);
		puck1.addFixture(Geometry.createCircle(20), 1, 1, 1);
		puck1.translate(855, 350);
		puck1.setLinearVelocity(40, 50); 		  
		puck1.setMass(MassType.NORMAL);
		puck1.setLinearDamping(0.0);
		puck1.setAngularDamping(0.0);
		puck1.setAutoSleepingEnabled(false);
		this.world.addBody(puck1);
		
		SimulationBody puck2 = new SimulationBody(Color.red);
		puck2.addFixture(Geometry.createCircle(20), 1, 1, 1);
		puck2.translate(300, 350);
		puck2.setLinearVelocity(-40, -50); 		  
		puck2.setMass(MassType.NORMAL);
		puck2.setLinearDamping(0.0);
		puck2.setAngularDamping(0.0);
		puck2.setAutoSleepingEnabled(false);
		this.world.addBody(puck2);
	}

	protected void render(Graphics2D g, double elapsedTime)
	{
		g.translate(0, 0);
		super.render(g, elapsedTime);
	}
}
