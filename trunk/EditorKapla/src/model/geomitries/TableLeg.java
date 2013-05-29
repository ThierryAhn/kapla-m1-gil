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
 * Classe TableLeg qui implemente l'interface ITableLeg.
 * @author Groupe C M1 GIL.
 */

public class TableLeg extends Geometry{
	/**
	 * Dimensions du pied de table.
	 */
	private static final float tableLegLength = 0.15f;
	private static final float tableLegWidth  = 2.5f;
	private static final float tableLegHeight = 0.15f;

	/**
	 * Geometrie et physique pour du pied de table.
	 */
	private static final Box box;
	private RigidBodyControl tableLegPhysic;
	private Material tableLegMaterial;

	/**
	 * Variable de gestion de la physique.
	 */
	private BulletAppState bulletAppState;
	/**
     * AssetManager de l'application.
     */
	private AssetManager assetManager;
	
	/**
	 * Instanciation du pied de la table.
	 */
	static{    
		box = new Box(tableLegLength,tableLegWidth
				,tableLegHeight);
		box.scaleTextureCoordinates(new Vector2f(3, 6));
	}
	
	/**
	 * Constructor.
	 * @param bulletAppState
	 * @param assetManager
	 */
	public TableLeg(BulletAppState bulletAppState, AssetManager assetManager){
		super("tableLeg");
		setMesh(box);
		this.bulletAppState = bulletAppState;
		this.assetManager = assetManager;
	}
	
	/**
	 * Initialisation de la texture du pied de la table.
	 */
	private void initMaterials() {
		tableLegMaterial = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		TextureKey key3 = new TextureKey("Textures/table.jpg");
		key3.setGenerateMips(true);
		Texture tex3 = assetManager.loadTexture(key3);
		tex3.setWrap(Texture.WrapMode.MirroredRepeat);
		tableLegMaterial.setTexture("ColorMap", tex3);
	}

	/**
	 * Cree un pied de table physique a une position donnee.
	 * @param v position du pied de la table.
	 * @return un pied de table cree.
	 */
	public TableLeg makeTableLeg(Vector3f v) {
		initMaterials();
		setMaterial(tableLegMaterial);
		setLocalTranslation(v);
		// rend le pied de la table physique avec un poids de 0
		tableLegPhysic = new RigidBodyControl(0.0f);
		addControl(tableLegPhysic);
		bulletAppState.getPhysicsSpace().add(tableLegPhysic);
		
		return this;
	}
}
