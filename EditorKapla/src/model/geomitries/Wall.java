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
 * Classe Wall qui represente un mur.
 * @author Groupe C M1 GIL.
 */
@SuppressWarnings("deprecation")
public class Wall extends Geometry{

	/**
	 * Geometrie et physique pour le mur.
	 */
	private static Box box;
	private RigidBodyControl wallPhysic;
	private Material wallMaterial;

	/**
	 * Variable de gestion de la physique.
	 */
	private BulletAppState bulletAppState;

	/**
	 * AssetManager de l'application.
	 */
	private AssetManager assetManager;

	/**
	 * Constructeur.
	 * @param bulletAppState
	 * @param assetManager
	 */
	public Wall(BulletAppState bulletAppState, AssetManager assetManager,
			float length,float width,float height){
		super("wall");
		this.bulletAppState = bulletAppState;
		this.assetManager = assetManager; 
		// instanciation du mur
		box = new Box(Vector3f.ZERO, length, width, height);
		box.scaleTextureCoordinates(new Vector2f(3, 6));
		setMesh(box);
	}

	/**
	 * Initialise la texture du mur.
	 */
	private void initMaterials() {
		wallMaterial = new Material(assetManager, 
		"Common/MatDefs/Misc/Unshaded.j3md");
		TextureKey key3 = new TextureKey("Textures/floor.jpg");
		key3.setGenerateMips(true);
		Texture tex3 = assetManager.loadTexture(key3);
		tex3.setWrap(Texture.WrapMode.Repeat);
		wallMaterial.setTexture("ColorMap", tex3);
	}

	/**
	 * Cree le mur et le rend physique.
	 * @return un mur cree.
	 */
	public Wall makeWall(Vector3f vec) {
		initMaterials();
		setMaterial(wallMaterial);
		setLocalTranslation(vec);
		// rend le mur physique avec un poids de 0
		wallPhysic = new RigidBodyControl(0.0f);
		addControl(wallPhysic);
		bulletAppState.getPhysicsSpace().add(wallPhysic);
		return this;
	}
}
