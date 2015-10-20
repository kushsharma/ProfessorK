package utils;

import com.badlogic.gdx.utils.Array;

public class TaskQueue {

	public static final int GRAVITY_FLI = 0;
	public static final int GRAVITY_FIX = 0;
	
	Array<Integer> tasks;
	
	public TaskQueue(){
		tasks= new Array<Integer>();
		
	}
	
	public void execute(){
		for(Integer i : tasks){
			switch(i){
				
			}
		}
	}
	
	public void clear(){
		tasks.clear();
	}
}
