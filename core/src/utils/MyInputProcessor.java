package utils;

import java.util.HashMap;

import objects.Player;
import objects.PowerUp;
import screens.GameScreen;
import screens.MainMenuScreen;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.softnuke.biosleep.MyGame;

public class MyInputProcessor implements InputProcessor{
	
	private Vector3 tempTouchVec = new Vector3();
	public static enum CONTROL{
		LEFT,
		RIGHT,
		UP,
		FIRE
	}
	public HashMap<CONTROL, Boolean> PLAYER_KEYS;
	
	public MyInputProcessor(){
		
		//keys that can be active
		PLAYER_KEYS = new HashMap<CONTROL, Boolean>();
		PLAYER_KEYS.put(CONTROL.LEFT, false);
		PLAYER_KEYS.put(CONTROL.RIGHT, false);
		PLAYER_KEYS.put(CONTROL.UP, false);
		PLAYER_KEYS.put(CONTROL.FIRE, false);
		
	}
	
	public void resetKeys(){
		PLAYER_KEYS.put(CONTROL.LEFT, false);
		PLAYER_KEYS.put(CONTROL.RIGHT, false);
		PLAYER_KEYS.put(CONTROL.UP, false);
		PLAYER_KEYS.put(CONTROL.FIRE, false);
	}
	
	@Override
	public boolean keyDown(int keycode) {
		GameScreen gs = GameScreen.getInstance();
		LevelGenerate level = LevelGenerate.getInstance();
		
		Player pl = Player.getInstance();	
		
		if(keycode == Keys.UP || keycode == Keys.Z){
			pl.makeJump();
			PLAYER_KEYS.put(CONTROL.UP, true);
		}
		if(keycode == Keys.LEFT){
			makeActionLeft();
		}
		if(keycode == Keys.RIGHT){
			makeActionRight();
		}
		if(keycode == Keys.SPACE || keycode == Keys.X){
			makeActionFire();			
		}
		pl.updateMove(PLAYER_KEYS);		
		LevelGenerate.getInstance().updateMove(PLAYER_KEYS);

		
		if(GameScreen.SOFT_DEBUG)
		{	
			//camera controls
			if(keycode == Keys.MINUS)
				gs.getCamera().zoom *= 1.4f;
			if(keycode == Keys.PLUS  || keycode == Keys.VOLUME_UP)
				gs.getCamera().zoom *= 0.6f;
			if(keycode == Keys.SLASH)
				gs.getCamera().position.x-= 3;
			if(keycode == Keys.STAR)
				gs.getCamera().position.x+= 3;
			if(keycode == Keys.PAGE_UP)
				gs.getCamera().position.y+= 3;
			if(keycode == Keys.PAGE_DOWN)
				gs.getCamera().position.y-= 3;			
			
			if(keycode == Keys.D || keycode == Keys.CAMERA)
			{				
				level.test();					
			}
			
			
			
			gs.getCamera().update();
		
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		GameScreen gs = GameScreen.getInstance();
		
		Player pl = Player.getInstance();
		
		if(keycode == Keys.UP || keycode == Keys.Z){
			PLAYER_KEYS.put(CONTROL.UP, false);
		}
		if(keycode == Keys.LEFT){
			leaveActionLeft();

			//PLAYER_KEYS.put(CONTROL.LEFT, false);
		}
		if(keycode == Keys.RIGHT){
			leaveActionRight();
		}
		if(keycode == Keys.SPACE || keycode == Keys.X){
			//if(LevelGenerate.MACHINE_GUN)
				leaveActionFire();
		}
		pl.updateMove(PLAYER_KEYS);
		LevelGenerate.getInstance().updateMove(PLAYER_KEYS);

		
		if(keycode == Keys.R)
		{
			gs.reset(false);
			//gs.CURRENT_STATE = GameState.STOPPED;
			return true;
		}
		
		if(keycode == Keys.ENTER){
			if(LevelGenerate.CURRENT_LEVEL_CLEARED == true)
				gs.startloadingNextLevel();
		}
		
		if(keycode == Keys.BACK || keycode == Keys.ESCAPE){
			
			if(GameScreen.CURRENT_STATE == GameState.RUNNING && !GameScreen.getInstance().levelClearScreen.isVisible())
				GameScreen.CURRENT_STATE = GameState.PAUSED;
			else if(GameScreen.CURRENT_STATE == GameState.PAUSED)
				gs.resumeGame();
			else
				gs.returnToMainMenu();

			//			if(GameScreen.CURRENT_STATE != GameState.RUNNING)
//			{
//				
//				MyGame game = GameScreen.getInstance().getGame();
//				game.setScreen(new MainMenuScreen(game));				
//			}
//			else
//			{
				//gs.StageVisible  = true;
			//}
			return true;
		}
		
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		GameScreen gs = GameScreen.getInstance();
		tempTouchVec.set(screenX, screenY, 0);
		
		Vector3 point = gs.getCamera().unproject(tempTouchVec);
		
		if(point.x < MyGame.WIDTH && point.y < MyGame.HEIGHT){
			if(GameScreen.CURRENT_STATE == GameState.RUNNING){
				Player pl = Player.getInstance();
				
				return true;
			}
			
		}
		
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		//if(keycode == Keys.SPACE)		
			
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}
	
	//buttons for handling touch events from stage
	public void makeActionLeft(){
		PLAYER_KEYS.put(CONTROL.LEFT, true);
		PLAYER_KEYS.put(CONTROL.RIGHT, false);
		Player pl = Player.getInstance();	

		pl.updateMove(PLAYER_KEYS);		

	}
	
	public void makeActionRight(){
		PLAYER_KEYS.put(CONTROL.LEFT, false);
		PLAYER_KEYS.put(CONTROL.RIGHT, true);	
		
		Player pl = Player.getInstance();	

		pl.updateMove(PLAYER_KEYS);		

	}
	
	public void makeActionUp(){
		Player pl = Player.getInstance();	
		pl.makeJump();

		pl.updateMove(PLAYER_KEYS);		

	}

	public void leaveActionLeft() {
		Player pl = Player.getInstance();	
		PLAYER_KEYS.put(CONTROL.LEFT, false);

		pl.updateMove(PLAYER_KEYS);		

		//stop movement if not moving anywhere
		if(PLAYER_KEYS.get(CONTROL.RIGHT) == false)
			pl.stopMove();
	}
	
	public void leaveActionRight() {
		Player pl = Player.getInstance();	
		PLAYER_KEYS.put(CONTROL.RIGHT, false);

		pl.updateMove(PLAYER_KEYS);		
		//stop movement if not moving anywhere
		if(PLAYER_KEYS.get(CONTROL.LEFT) == false)
			pl.stopMove();

	}
	
	public void leaveActionUp() {
		Player pl = Player.getInstance();	
		PLAYER_KEYS.put(CONTROL.UP, false);

		pl.updateMove(PLAYER_KEYS);		

	}

	public void makeActionFire() {
		if(Player.getInstance().PLAYER_EVOLUTION == PowerUp.LEVEL_ZERO)
			return;
		if(!LevelGenerate.MACHINE_GUN)
		{
			LevelGenerate.getInstance().fireBullet();
			Player.getInstance().CAN_FIRE = false;
		}
		
		PLAYER_KEYS.put(CONTROL.FIRE, true);
		LevelGenerate.getInstance().updateMove(PLAYER_KEYS);
	}
	
	public void leaveActionFire() {	
		if(Player.getInstance().PLAYER_EVOLUTION == PowerUp.LEVEL_ZERO)
			return;
		
		//if(!LevelGenerate.MACHINE_GUN)
			Player.getInstance().CAN_FIRE = true;
		
		PLAYER_KEYS.put(CONTROL.FIRE, false);
		LevelGenerate.getInstance().updateMove(PLAYER_KEYS);

	}
}
