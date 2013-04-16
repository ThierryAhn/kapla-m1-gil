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
 * Classe Table qui implemente l'interface ITable.
 * @author Groupe C M1 GIL.
 */
@SuppressWarnings("deprecation")
public class Table extends Geometry{

	/**
	 * Dimensions de la table.
	 */
	private static final float tableLength = 8f;
	private static final float tableWidth  = 0.1f;
	private static final float tableHeight = 5f;

	/**
	 * Geometrie et physique pour la table.
	 */
	private static final Box box;
	private RigidBodyControl tablePhysic;
	private Material tableMaterial;

	/**
	 * Variable de gestion de la physique.
	 */
	private BulletAppState bulletAppState;
	/**
     * AssetManager de l'application.
     */
	private AssetManager assetManager;
	
	/**
	 * Liste de pieds de la table.
	 */
	private TableLeg tableLeg[] = new TableLeg[4];
	
	static{    
		box = new Box(Vector3f.ZERO, tableLength, tableWidth, tableHeight);
		box.scaleTextureCoordinates(new Vector2f(3, 6));
	}
	
	/**
	 * Constructor.
	 * @param bulletAppState
	 * @param assetManager
	 */
	public Table(BulletAppState bulletAppState, AssetManager assetManager){
		super("table");
		setMesh(box);
		this.bulletAppState = bulletAppState;
		this.assetManager = assetManager;
		
		tableLeg[0] = new TableLeg(bulletAppState, assetManager).makeTableLeg(
                new Vector3f(7.7f, -2.5f, -4.7f));
		tableLeg[1] = new TableLeg(bulletAppState, assetManager).makeTableLeg(
                new Vector3f(-7.7f, -2.5f, 4.7f));
		tableLeg[2] = new TableLeg(bulletAppState, assetManager).makeTableLeg(
                new Vector3f(-7.7f, -2.5f, -4.7f));
		tableLeg[3] = new TableLeg(bulletAppState, assetManager).makeTableLeg(
                new Vector3f(7.7f, -2.5f, 4.7f));
		
	}
	
	/**
	 * Initialise la texture de la table.
	 */
	private void initMaterials() {
		tableMaterial = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		TextureKey key3 = new TextureKey("Textures/table.jpg");
		key3.setGenerateMips(true);
		Texture tex3 = assetManager.loadTexture(key3);
		tex3.setWrap(Texture.WrapMode.MirroredRepeat);
		tableMaterial.setTexture("ColorMap", tex3);
	}

	/**
	 * Cree une table physique.
	 * @return une table creee.
	 */
	public Table makeTable() {
		initMaterials();
		setMaterial(tableMaterial);
		setLocalTranslation(0, -0.1f, 0);
		// Rend la table physique avec un poids de 0
		tablePhysic = new RigidBodyControl(0.0f);
		addControl(tablePhysic);
		bulletAppState.getPhysicsSpace().add(tablePhysic);
		return this;
	}
	
	/**
	 * Retourne le pied la table a une position donnee.
	 * @param i index du pied de la table.
	 * @return une instance de TableLeg.
	 */
	public TableLeg getTableLeg(int i){
		return tableLeg[i];
	}
}
