package org;

import java.util.ArrayList;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Fixture;
//import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
//import org.newdawn.slick.BasicGame;
//import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
//import org.newdawn.slick.Sound;


import utils.AsteroidSpawner;
import utils.CollideTest;
import utils.ShipBuild;
import utils.ShipCPU;

public class ShipTest extends ShipGame{
	
	private Input input;
	private AppGameContainer app;
	
	public static int x;
	public static int y;
	
	private World world;
	private float timeStep = 1.0f/60.0f;
	private int velocityIterations = 8;
	private int positionIterations = 3;
	
	//public static jbox2slick tr;
	
	private ShipBuild player1;
	private ShipBuild player2;
	private ShipBuild player3;
	private ShipBuild player4;
	private AsteroidSpawner asteroids;
	
	private CollideTest cDetect;
	//private Sound hit;
	
	//public int[][] player1Ship;
	//public int[][] player2Ship;
	
	//private boolean firstDeath=true;
	//public byte winner=0;
	public int mode=0;
	//public int level=0;

	public ShipTest(int m) {
		super("SPACEBLOCKS");
		mode=m;
	}

	/*public static void main(String[] args) {
		try {
			AppGameContainer container = new AppGameContainer(new ShipTest(0));
			x = container.getScreenWidth();
			y = container.getScreenHeight();
			tr = new jbox2slick(x,y,x/10,y/10);
			container.setDisplayMode(x,y,true);
			container.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}

	}*/
	
	
    public boolean closeRequested()
    {
      try {
		destroyOpenAL();
      } catch (SlickException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
      }
      app.exit();
      return false;
    }

    
	public void render(GameContainer arg0, Graphics arg1) throws SlickException {
		
		arg1.setBackground(Color.black);
		
		asteroids.drawAsteroids(arg1);
		
		if(!player1.dead){
			player1.drawShip(arg1);
		}
		if(!player2.dead){
			player2.drawShip(arg1);
		}
		if(mode>=2){
			if(!player3.dead){
				player3.drawShip(arg1);
			}
			if(mode>=3){
				if(!player4.dead){
					player4.drawShip(arg1);
				} else{
				}
			}
		}
		
		
		//nothing, dont need
			
			//I had to comment out some stuff b/c the text wasn't rendering
			//right. I will try again with images.
			
			//arg1.setColor(Color.black);
			
			/*if(player1.dead && player2.dead){
				arg1.setColor(Color.white);
				arg1.fillRect(0, 0, tr.width, tr.height);
				arg1.setColor(Color.black);
				arg1.drawString("IT'S A TIE", tr.width/2-80, tr.height/2-10);
			}*/
		
	}

	
	public void init(GameContainer container) throws SlickException {
		
		//firstDeath = true;
		
		//hit = new Sound("Hit.ogg");
		
		over = false;
		
		if (container instanceof AppGameContainer) {
			app = (AppGameContainer) container;
		}
		
		input = container.getInput();
		
		Vec2 gravity = new Vec2(0.0f,0.0f);
		world = new World(gravity, true);
		
		player1 = new ShipBuild(player1Ship, world, tr);
		if(mode>=1){
			player2 = new ShipCPU(player2Ship, world, tr, level); 	//thats the time in ms for shooting
		} else{
			player2 = new ShipBuild(player2Ship, world, tr);
		}
		if(mode>=2){
			player3 = new ShipCPU(player2Ship, world, tr, level);
		}
		if(mode>=3){
			player4 = new ShipCPU(player2Ship, world, tr, level);
		}
		
		float jX = tr.width/tr.xscale;
		float jY = tr.height/tr.yscale;
		
		float max = jY/2-15;
		float min = -jY/2+15;
		
		asteroids = new AsteroidSpawner(world, tr);
		asteroids.genAsteroids(jX/2-40, max-25);
		
		float p1Y = (float) (Math.random()*(max-min) + min);
		float p2Y = (float) (Math.random()*(max-min) + min);
			
		if(mode==2){
			player2.player.setTransform(new Vec2(jX/2-15, max), (float)Math.PI/2);
			player3.player.setTransform(new Vec2(jX/2-15,min), (float)Math.PI/2);
		} else if(mode==3){
			player2.player.setTransform(new Vec2(jX/2-15, max), (float)Math.PI/2);
			player3.player.setTransform(new Vec2(jX/2-15,min), (float)Math.PI/2);
			player4.player.setTransform(new Vec2(jX/2-15, 0), (float)Math.PI/2);
		} else{
			player2.player.setTransform(new Vec2(jX/2-15, p2Y), (float)Math.PI/2);
		}

		
		player1.player.setTransform(new Vec2(-jX/2+15, p1Y), 3*(float)Math.PI/2);
		
		player1.inputForward = Input.KEY_W;//E;
		player1.inputLeft = Input.KEY_A;//W;
		player1.inputRight = Input.KEY_D;//R;
		player1.inputShoot = Input.KEY_SPACE;//A;
		player1.inputBackward = Input.KEY_S;
		
		cDetect = new CollideTest(player1, player2, world);
		
		world.setContactListener(cDetect);
		
	}

	
	public void update(GameContainer arg0, int arg1) throws SlickException {
		world.step(timeStep, velocityIterations, positionIterations);
		
		Fixture fa = cDetect.fa;
		Fixture fb = cDetect.fb;
		
		if(fa != null && fb != null){
			
				player1.checkDeleteFixture(fa,fb);
				player2.checkDeleteFixture(fa,fb);
				
				if(mode>=2){
					player3.checkDeleteFixture(fa,fb);
					if(mode>=3){
						player4.checkDeleteFixture(fa,fb);
					}
				}
				
				asteroids.damageAsteroid(fa);
				asteroids.damageAsteroid(fb);
		
		}
		
		float player4Dist = 1000;
		float player3Dist = 1000;
		float player2Dist = 1000;
		
		if(mode==3){
			Vec2[] temp = {player2.player.getPosition(), player3.player.getPosition()};
			ArrayList<Vec2> temp2 = new ArrayList<Vec2>();
			for(Vec2 p: temp){
				temp2.add(p);
			}
			player4.calcForceCPU(player1.position, asteroids.asteroids, temp2, arg1);
			
			Vec2[] temp3 = {player2.player.getPosition(), player4.player.getPosition()};
			ArrayList<Vec2> temp4 = new ArrayList<Vec2>();
			for(Vec2 p: temp3){
				temp4.add(p);
			}
			player3.calcForceCPU(player1.position, asteroids.asteroids, temp4, arg1);
			
			Vec2[] temp5 = {player3.player.getPosition(), player4.player.getPosition()};
			ArrayList<Vec2> temp6 = new ArrayList<Vec2>();
			for(Vec2 p: temp5){
				temp6.add(p);
			}
			player2.calcForceCPU(player1.position, asteroids.asteroids, temp6, arg1);
			
			float x24 = player4.player.getPosition().x-player1.player.getPosition().x;
			float y24 = player4.player.getPosition().y-player1.player.getPosition().y;
			
			float x23 = player3.player.getPosition().x-player1.player.getPosition().x;
			float y23 = player3.player.getPosition().y-player1.player.getPosition().y;
			
			float x22 = player2.player.getPosition().x-player1.player.getPosition().x;
			float y22 = player2.player.getPosition().y-player1.player.getPosition().y;
			if(!player4.dead){
				player4Dist = (float) Math.sqrt(Math.pow(x24,2)+Math.pow(y24,2));
			}
			if(!player3.dead){
				player3Dist = (float) Math.sqrt(Math.pow(x23,2)+Math.pow(y23,2));
			}
			if(!player2.dead){
				player2Dist = (float) Math.sqrt(Math.pow(x22,2)+Math.pow(y22,2));
			}
			
		}else if (mode==2){
			
			ArrayList<Vec2> temp1 = new ArrayList<Vec2>();
			temp1.add(player2.player.getPosition());
			player3.calcForceCPU(player1.position, asteroids.asteroids, temp1, arg1);
			
			ArrayList<Vec2> temp2 = new ArrayList<Vec2>();
			temp2.add(player3.player.getPosition());
			player2.calcForceCPU(player1.position, asteroids.asteroids, temp2, arg1);
			
			float x23 = player3.player.getPosition().x-player1.player.getPosition().x;
			float y23 = player3.player.getPosition().y-player1.player.getPosition().y;
			
			float x22 = player2.player.getPosition().x-player1.player.getPosition().x;
			float y22 = player2.player.getPosition().y-player1.player.getPosition().y;
			
			if(!player3.dead){
				player3Dist = (float) Math.sqrt(Math.pow(x23,2)+Math.pow(y23,2));
			}
			if(!player2.dead){
				player2Dist = (float) Math.sqrt(Math.pow(x22,2)+Math.pow(y22,2));
			}
			
		}else if(mode==1){
			player2.calcForceCPU(player1.position, asteroids.asteroids, new ArrayList<Vec2>(), arg1);
			
			float x22 = player2.player.getPosition().x-player1.player.getPosition().x;
			float y22 = player2.player.getPosition().y-player1.player.getPosition().y;
			
			player2Dist = (float) Math.sqrt(Math.pow(x22,2)+Math.pow(y22,2));
		} else{
			player2.calcForce(input, arg1, player1.player.getPosition());
			
			float x22 = player2.player.getPosition().x-player1.player.getPosition().x;
			float y22 = player2.player.getPosition().y-player1.player.getPosition().y;
			
			player2Dist = (float) Math.sqrt(Math.pow(x22,2)+Math.pow(y22,2));
		}
		
		if(player2Dist<player3Dist && player2Dist<player4Dist){
			player1.calcForce(input, arg1, player2.player.getPosition());
		}
		if(player4Dist<player3Dist && player4Dist<player3Dist){
			player1.calcForce(input, arg1, player4.player.getPosition());
		}
		if(player3Dist<player2Dist && player3Dist<player4Dist){
			player1.calcForce(input, arg1, player3.player.getPosition());
		}
		
		if(player1.dead){ 
			//arg1.drawString("PLAYER 2 WINS", tr.width/2-80, tr.height/2-10); 
			winner=2;
			arg0.sleep(500);
			over = true;
		}
		if(mode==2){
			if(player2.dead && player3.dead){
				winner=1;
				over = true;
			}
			if(player2.dead){
				cDestroy(player2);
			}
			if(player3.dead){
				cDestroy(player3);
			}
			if(player1.batteryLeft<=0 && player2.batteryLeft<=0 && player3.batteryLeft<=0 ){
				over = true;
			}
		}else if(mode==3){
			if(player2.dead && player3.dead && player4.dead){
				winner=1;
				over = true;
			}
			if(player2.dead){
				cDestroy(player2);
			}
			if(player3.dead){
				cDestroy(player3);
			}
			if(player4.dead){
				cDestroy(player4);
			}
			if(player1.batteryLeft<=0 && player2.batteryLeft<=0 && player3.batteryLeft<=0 && player4.batteryLeft <= 0){
				over = true;
			}
		}else{
			if(player1.dead && player2.dead){
				winner=0;
				over = true;
			}else if(player2.dead){ 
				//arg1.drawString("PLAYER 1 WINS", tr.width/2-80, tr.height/2-10); 
				winner=1;
				over = true;
			}
			
			if(player1.batteryLeft<=0 && player2.batteryLeft<=0){
				over = true;
			}
		}
		
		if(input.isKeyDown(Input.KEY_ESCAPE)){
			over = true;
		}
		
	}
	
	private void cDestroy(ShipBuild b){
		world.destroyBody(b.player);
		for(Fixture f: b.blastLasers){
			world.destroyBody(f.getBody());
		}
		for(Fixture f: b.homBlastLasers){
			world.destroyBody(f.getBody());
		}
	}

}
