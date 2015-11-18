package utils;

import objects.Player;
import screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.softnuke.biosleep.MyGame;

public class MyContactListener implements ContactListener{

	@Override
	public void beginContact(Contact contact) {
		
		Body A = contact.getFixtureA().getBody();
		Body B = contact.getFixtureB().getBody();
		
		Player pl = Player.getInstance();
		
		if((A.getUserData().equals("platform") == true && contact.getFixtureB() == pl.getSensorFixture()) || (contact.getFixtureA() == pl.getSensorFixture() && B.getUserData().equals("platform") == true))
		{//handle player and platform collision
			pl.CAN_JUMP = true;
			pl.startJumpEffect();
		}
		else if((A.getUserData().equals("enemy") == true && B.getUserData().equals("platform") == true) || (A.getUserData().equals("platform") == true && B.getUserData().equals("enemy") == true))
		{//handle enemy and platform collision
			if(A.getUserData().equals("platform") == true)
				LevelGenerate.getInstance().enemyWallBounce(contact.getFixtureB());
			else
				LevelGenerate.getInstance().enemyWallBounce(contact.getFixtureA());
			
			
		}
				
		if((A.getUserData().equals("enemy") == true && B.getUserData().equals("player") == true) || (A.getUserData().equals("player") == true && B.getUserData().equals("enemy") == true))
		{//handle badboy kills
			
			if(A.getUserData().equals("player") == true)
				LevelGenerate.getInstance().enemyPlayerCollide(contact.getFixtureB(), contact.getFixtureA());
			else
				LevelGenerate.getInstance().enemyPlayerCollide(contact.getFixtureA(), contact.getFixtureB());
			
		}
		
		if((A.getUserData().equals("boss1") == true && B.getUserData().equals("player") == true) || (A.getUserData().equals("player") == true && B.getUserData().equals("boss1") == true))
		{//handle boss kills
			
			if(A.getUserData().equals("player") == true)
				LevelGenerate.getInstance().bossPlayerCollide(contact.getFixtureB(), true);
			else
				LevelGenerate.getInstance().bossPlayerCollide(contact.getFixtureA(), true);
		}
		
		//handle bullet and boss
		if((A.getUserData().equals("boss1") == true && B.getUserData().equals("bullet") == true) || (A.getUserData().equals("bullet") == true && B.getUserData().equals("boss1") == true))
		{
			if(A.getUserData().equals("boss1") == true)
				LevelGenerate.getInstance().bossBulletCollide(contact.getFixtureA(), contact.getFixtureB());
			else
				LevelGenerate.getInstance().bossBulletCollide(contact.getFixtureB(), contact.getFixtureA());
			
		}
		
		if((A.getUserData().equals("spikes") == true && contact.getFixtureB().equals(pl.getBodyFixture()) == true) || (contact.getFixtureA().equals(pl.getBodyFixture()) == true && B.getUserData().equals("spikes") == true))
		{//handle spikes kills
			
			//start player anime
			pl.setDeath();
			
		}
		
		if((A.getUserData().equals("beamSpot") == true && contact.getFixtureB().equals(pl.getBodyFixture()) == true) || (contact.getFixtureA().equals(pl.getBodyFixture()) == true && B.getUserData().equals("beamSpot") == true))
		{//handle spikes kills
			pl.CAN_JUMP = true;

			if(A.getUserData().equals("player") == true)
				LevelGenerate.getInstance().beamPlayerCollide(contact.getFixtureB(), true);
			else
				LevelGenerate.getInstance().beamPlayerCollide(contact.getFixtureA(), true);
			
		}
		
		//handle bullet and enemy
		if((A.getUserData().equals("enemy") == true && B.getUserData().equals("bullet") == true) || (A.getUserData().equals("bullet") == true && B.getUserData().equals("enemy") == true))
		{
			if(A.getUserData().equals("enemy") == true)
				LevelGenerate.getInstance().enemyBulletCollide(contact.getFixtureA(), contact.getFixtureB());
			else
				LevelGenerate.getInstance().enemyBulletCollide(contact.getFixtureB(), contact.getFixtureA());
			
		}
		
		//handle bullet and (platfrom/portal)
		if((((A.getUserData().equals("platform") == true || A.getUserData().equals("portal") == true) && B.getUserData().equals("bullet") == true)) || (A.getUserData().equals("bullet") == true && (B.getUserData().equals("platform") || B.getUserData().equals("portal") == true) == true))
		{
			if(A.getUserData().equals("platform") == true || A.getUserData().equals("portal") == true)
				LevelGenerate.getInstance().bulletPlatformCollide(contact.getFixtureB());
			else
				LevelGenerate.getInstance().bulletPlatformCollide(contact.getFixtureA());
			
		}
		
		//handle powerups
		if((A.getUserData().equals("player") == true && B.getUserData().equals("powerUp") == true) || (A.getUserData().equals("powerUp") == true && B.getUserData().equals("player") == true))
		{
			if(A.getUserData().equals("player") == true)
				LevelGenerate.getInstance().powerUpPlayer(contact.getFixtureB());
			else
				LevelGenerate.getInstance().powerUpPlayer(contact.getFixtureA());
			
		}
		
		//handle portals
		if((A.getUserData().equals("player") == true && B.getUserData().equals("portal") == true) || (A.getUserData().equals("portal") == true && B.getUserData().equals("player") == true))
		{
			//user can jump on portals too
			pl.CAN_JUMP = true;

			if(A.getUserData().equals("player") == true)
				LevelGenerate.getInstance().levelClearPortal(contact.getFixtureB());
			else
				LevelGenerate.getInstance().levelClearPortal(contact.getFixtureA());
			
		}
		
		//handle moving platforms, a.k.a. movers
		if((A.getUserData().equals("player") == true && B.getUserData().equals("mover") == true) || (A.getUserData().equals("mover") == true && B.getUserData().equals("player") == true))
		{
			//user can jump on movers too
			pl.CAN_JUMP = true;
		}
		
		//handle coin collection
		if((A.getUserData().equals("player") == true && B.getUserData().equals("coin") == true) || (A.getUserData().equals("coin") == true && B.getUserData().equals("player") == true))
		{
			if(A.getUserData().equals("player") == true)
				LevelGenerate.getInstance().coinPlayerCollide(contact.getFixtureB());
			else
				LevelGenerate.getInstance().coinPlayerCollide(contact.getFixtureA());
		}
		
		//handle switch for enabling level exit portal
		if((contact.getFixtureA().equals(pl.getBodyFixture()) == true && B.getUserData().equals("switch") == true) || (A.getUserData().equals("switch") == true && contact.getFixtureB().equals(pl.getBodyFixture()) == true ))
		{
			if(contact.getFixtureA().equals(pl.getBodyFixture()) == true)
				LevelGenerate.getInstance().switchPlayerCollide(contact.getFixtureB());
			else
				LevelGenerate.getInstance().switchPlayerCollide(contact.getFixtureA());
		}
		
		//handle switch for enabling level exit portal
		if((contact.getFixtureA().equals(pl.getBodyFixture()) == true && B.getUserData().equals("gravityReverser") == true) || (A.getUserData().equals("gravityReverser") == true && contact.getFixtureB().equals(pl.getBodyFixture()) == true ))
		{
			if(contact.getFixtureA().equals(pl.getBodyFixture()) == true)
				LevelGenerate.getInstance().gravityPlayerCollide(contact.getFixtureB());
			else
				LevelGenerate.getInstance().gravityPlayerCollide(contact.getFixtureA());
		}
	}

	@Override
	public void endContact(Contact contact) {
		
		Body A = contact.getFixtureA().getBody();
		Body B = contact.getFixtureB().getBody();
		
		Player pl = Player.getInstance();
		
		if((A.getUserData().equals("platform") == true && contact.getFixtureB() == pl.getSensorFixture()) || (contact.getFixtureA() == pl.getSensorFixture() && B.getUserData().equals("platform") == true))
		{
			//pl.CAN_JUMP = false;
		}
		
		if((A.getUserData().equals("enemy") == true && B.getUserData().equals("platform") == true) || (A.getUserData().equals("platform") == true && B.getUserData().equals("enemy") == true))
		{//handle enemy and platform collision
			
			if(A.getUserData().equals("platform") == true)
				LevelGenerate.getInstance().enemyFlying(contact.getFixtureB());
			else
				LevelGenerate.getInstance().enemyFlying(contact.getFixtureA());
			
			
		}
		
		if((A.getUserData().equals("beamSpot") == true && contact.getFixtureB().equals(pl.getBodyFixture()) == true) || (contact.getFixtureA().equals(pl.getBodyFixture()) == true && B.getUserData().equals("beamSpot") == true))
		{//handle spikes kills
			
			if(A.getUserData().equals("player") == true)
				LevelGenerate.getInstance().beamPlayerCollide(contact.getFixtureB(), false);
			else
				LevelGenerate.getInstance().beamPlayerCollide(contact.getFixtureA(), false);
		}
		
		if((A.getUserData().equals("boss1") == true && contact.getFixtureB().equals(pl.getBodyFixture()) == true) || (contact.getFixtureA().equals(pl.getBodyFixture()) == true && B.getUserData().equals("boss1") == true))
		{//handle spikes kills
			
			if(A.getUserData().equals("player") == true)
				LevelGenerate.getInstance().bossPlayerCollide(contact.getFixtureB(), false);
			else
				LevelGenerate.getInstance().bossPlayerCollide(contact.getFixtureA(), false);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Body A = contact.getFixtureA().getBody();
		Body B = contact.getFixtureB().getBody();
		
		Player pl = Player.getInstance();
		

		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		// TODO Auto-generated method stub
		
	}

}
