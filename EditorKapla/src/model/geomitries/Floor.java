package model.geomitries;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 * Classe Floor qui implemente l'interface IFloor.
 * @author Groupe C M1 GIL.
 */
@SuppressWarnings("deprecation")
public class Floor extends Geometry{

	/**
	 * Dimensions du sol.
	 */
	private static final float floorLength = 30f;
	private static final float floorWidth  = 0.5f;
	private static final float floorHeight = 30f;

	/**
	 * Geometrie et physique pour le sol.
	 */
	private static final Box box;
	private RigidBodyControl floorPhysic;
	private Material floorMaterial;

	/**
	 * Variable de gestion de la physique.
	 */
	private BulletAppState bulletAppState;

	/**
	 * AssetManager de l'application.
	 */
	private AssetManager assetManager;
	
	/**
	 * Instanciation du sol.
	 */
	static{    
		box = new Box(Vector3f.ZERO, floorLength, floorWidth, floorHeight);
		box.scaleTextureCoordinates(new Vector2f(3, 6));
	}

	/**
	 * Constructeur.
	 * @param bulletAppState
	 * @param assetManager
	 */
	public Floor(BulletAppState bulletAppState, AssetManager assetManager){
		super("floor");
		setMesh(box);
		this.bulletAppState = bulletAppState;
		this.assetManager = assetManager;
	}
	
	/**
	 * Initialise la texture du sol.
	 */
	private void initMaterials() {
		floorMaterial = new Material(assetManager, 
				"Common/MatDefs/Misc/Unshaded.j3md");
		TextureKey key3 = new TextureKey("Textures/floor.jpg");
		key3.setGenerateMips(true);
		Texture tex3 = assetManager.loadTexture(key3);
		tex3.setWrap(Texture.WrapMode.Repeat);
		floorMaterial.setTexture("ColorMap", tex3);
	}
	
	/**
	 * Cree le sol et le rend physique.
	 * @return un sol cree.
	 */
	public Floor makeFloor() {
		initMaterials();
		setMaterial(floorMaterial);
		setLocalTranslation(0, -5f, 0);
		// rend le sol physique avec un poids de 0
		floorPhysic = new RigidBodyControl(0.0f);
		addControl(floorPhysic);
		bulletAppState.getPhysicsSpace().add(floorPhysic);
		return this;
	}
}
