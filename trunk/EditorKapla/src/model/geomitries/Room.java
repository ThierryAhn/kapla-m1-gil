package model.geomitries;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.math.Vector3f;

/**
 * Classe modelisant la salle de jeu qui entour la table.
 * 
 * @author Groupe C
 *
 */
public class Room {
	
	/**
	 * Tableau de murs.
	 */
	private Wall walls[] = new Wall[6];
	
	/**
	 * Constructeur.
	 * @param bulletAppState
	 * @param assetManager
	 */
	public Room(BulletAppState bulletAppState,AssetManager assetManager) {
		walls[0] =  new Wall(bulletAppState, assetManager,30f,0.5f,30f).makeWall(
				new Vector3f(0, -5f, 0),"moquette");
		walls[1] =  new Wall(bulletAppState, assetManager,30f,20f,0.5f).makeWall(
				new Vector3f(0, 14.5f, -30.5f),"parquet");
		walls[2] =  new Wall(bulletAppState, assetManager,30f,20f,0.5f).makeWall(
				new Vector3f(0, 14.5f, 30.5f),"parquet");
		walls[3] =  new Wall(bulletAppState, assetManager,0.5f,20f,30f).makeWall(
				new Vector3f(-30.5f, 14.5f, 0),"parquet");
		walls[4] =  new Wall(bulletAppState, assetManager,0.5f,20f,30f).makeWall(
				new Vector3f(30.5f, 14.5f, 0),"parquet");
		walls[5] =  new Wall(bulletAppState, assetManager,30f,0.5f,30f).makeWall(
				new Vector3f(0, 34.5f, 0),"parquet");
	}

	/**
	 * Retourne un mur depuis le tableau de murs.
	 * @param index de la case du mur.
	 * @return un mur depuis le tableau de murs.
	 */
	public Wall getWalls(int index){
		return walls[index];
	}
}
