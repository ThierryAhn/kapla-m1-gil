package model.geomitries;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;

/**
 * Classe modelisant la brique.
 * @author Groupe C M1 GIL
 */

@SuppressWarnings({ "deprecation" })
public class Brick extends Geometry{

	/**
	 * Proprietes de la brique.
	 */
	private BrickProperties brickProperties;
	
	/**
	 * Definit l'etat de selection de la brique
	 */
	private boolean selected;
	
	/**
	 * Rotation de la brique
	 */
	private float rotateBrick;

	/**
	 * Dimensions de la brique.
	 */
	private static final float brickLength = 0.48f;
	private static final float brickWidth  = 0.24f;
	private static final float brickHeight = 0.12f;

	/**
	 * Geometrie et physique de la brique.
	 */
	private static final Box box;
	private RigidBodyControl brickPhysic;
	private Material brickMaterial;
	
	/**
	 *  Prepare le modele physique (jBullet).
	 */
	private BulletAppState bulletAppState;


	/**
	 * Instanciation de la brique.
	 */
	static{
		box = new Box(Vector3f.ZERO, brickLength, brickHeight, brickWidth);
		box.scaleTextureCoordinates(new Vector2f(1f, .5f));
	}

	/**
	 * Constructeur.
	 * @param brickProperties
	 * @param bulletAppState
	 * @param assetManager
	 */
	public Brick(BrickProperties brickProperties, BulletAppState bulletAppState, AssetManager assetManager){
		super("brick");
		this.brickProperties = brickProperties;
		// initialise la geometrie de la brique
		setMesh(box);
		this.bulletAppState = bulletAppState;
		brickMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		this.rotateBrick=0;
	}

	/**
	 * Permet de changer la couleur de la brique.
	 * @param color la couleur souhaitee.
	 */
	public void changeColor(ColorRGBA color){
		// tant que la couleur n'est pas la couleur de selection
		if(!color.equals(ColorRGBA.Magenta)){
			brickMaterial.setColor("Color", color);
			getBrickProperties().setColor(color);
			setMaterial(brickMaterial);
		}
	}

	/**
	 * Change la couleur des briques selectionnées en violet.
	 */
	public void setSelectedColor(){
		brickMaterial.setColor("Color", ColorRGBA.Magenta);
		setMaterial(brickMaterial);
		setSelected(true);
	}
	
	/**
	 * Retourne a la couleur initiale de la brique.
	 */
	public void backTextureColor(){
		brickMaterial.setColor("Color", getBrickProperties().getColor());
		setMaterial(brickMaterial);
		setSelected(false);
	}

	/**
	 * Cree une brique et le rend physique.
	 * @return une brique creee.
	 */
	public Brick makeBrick() {
		brickMaterial.setColor("Color", ColorRGBA.Orange);
		getBrickProperties().setColor(ColorRGBA.Orange);
		// cree la brique
		setMaterial(brickMaterial);
		// position de la brique
		setLocalTranslation(brickProperties.getPosition());
		// poids de la brique
		brickPhysic = new RigidBodyControl(0.1f);
		// ajout de la brique dans l'espace physique
		addControl(brickPhysic);
		bulletAppState.getPhysicsSpace().add(brickPhysic);
		
		return this;
	}
	
	/**
	 * Retourne le modele physique de la brique.
	 * @return le modele physique de la brique
	 */
	public RigidBodyControl getBrickPhysic() {
		return brickPhysic;
	}
	
	/**
	 * Modifie le modele physique de la brique.
	 * @param brickPhysic nouveau modele physique de la brique.
	 */
	public void setBrickPhysic(RigidBodyControl brickPhysic) {
		this.brickPhysic = brickPhysic;
	}
	
	/**
	 * Rotation a gauche de la brique.
	 * @return la valeur de la rotation de la brique.
	 */
	public float rotateLeft() {
		return rotateBrick+= 0.005;
	}
	
	/**
	 * Rotation  a droite de la brique.
	 * @return la valeur de la rotation de la brique.
	 */
	public float rotateRight() {
		return rotateBrick-= 0.005;
	}
	
	/**
	 * Retourne les proprietes de la brique.
	 * @return les proprietes de la brique.
	 */
	public BrickProperties getBrickProperties(){
		return brickProperties;
	}
	
	/**
	 * Retourne l'etat de la selection de la brique.
	 * @return true si la brique est selectionnee, false sinon.
	 */
	public boolean isSelected() {
		return selected;
	}
	
	/**
	 * Modifie l'etat de selection de la brique.
	 * @param selected nouvel etat de selection de la brique.
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
}
