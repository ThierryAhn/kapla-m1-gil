package model.geomitries;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;

/**
 * Classe modelisant la brique.
 * @author Groupe C M1 GIL
 */

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
	private float rotateBrickH=0;
	private float rotateBrickV=0;

	/**
	 * Dimensions de la brique.
	 */
	private static float brickLength = 0.48f;
	private static  float brickWidth  = 0.24f;
	private static  float brickHeight = 0.12f;
	
	private float brickLengthCalcul = 0.48f;
	private float brickWidthCalcul  = 0.24f;
	private float brickHeightCalcul = 0.12f;

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
	private boolean isPhysic;

	
	/**
	 * la Texture
	 */
	TextureKey myText = new TextureKey();
	
	
	/**
	 * le result de la collision
	 */
	CollisionResults results = new CollisionResults();
	
	
	
	/**
	 * Instanciation de la brique.
	 */
	static{
		box = new Box(brickLength, brickHeight, brickWidth);
		box.scaleTextureCoordinates(new Vector2f(1f, .5f));
	}

	/**
	 * Constructeur.
	 * @param brickProperties
	 * @param bulletAppState
	 * @param assetManager
	 */
	public Brick(BrickProperties brickProperties, BulletAppState bulletAppState, AssetManager assetManager){
		super("brick",box);
		this.brickProperties = brickProperties;
		this.bulletAppState = bulletAppState;
		this.isPhysic= false;
		brickMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		
		myText = new TextureKey("Textures/BrickPomme.jpg");
		//brickProperties.setMyText("BrickPomme");
		myText.setGenerateMips(true);
		Texture tex3 = assetManager.loadTexture(myText);
		tex3.setWrap(Texture.WrapMode.MirroredRepeat);
		brickMaterial.setTexture("ColorMap", tex3);
		
	}
	
	/**
	 * Permet de changer la texture de la brique.
	 * @param assetManager et le nom de la texture souhaitée.
	 */
	public void changeTexture(String s,AssetManager assetManager){
		myText = new TextureKey("Textures/"+s+".jpg");
		myText.setGenerateMips(true);
		Texture tex3 = assetManager.loadTexture(myText);
		tex3.setWrap(Texture.WrapMode.MirroredRepeat);
		brickMaterial.setTexture("ColorMap", tex3);
		brickProperties.setMyText(s);
	}
	
	/**
	 * Change la couleur des briques selectionnées en violet.
	 */
	public void setSelectedTexture(AssetManager assetManager){
		TextureKey key3 = new TextureKey("Textures/BrickViolet.jpg");
		key3.setGenerateMips(true);
		Texture tex3 = assetManager.loadTexture(key3);
		tex3.setWrap(Texture.WrapMode.MirroredRepeat);
		brickMaterial.setTexture("ColorMap", tex3);
	}
	
	public void getAncienneTexture(AssetManager assetManager){
		myText.setGenerateMips(true);
		Texture tex3 = assetManager.loadTexture(myText);
		tex3.setWrap(Texture.WrapMode.MirroredRepeat);
		brickMaterial.setTexture("ColorMap", tex3);
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
	 * Cree une brique .
	 * @return une brique creee.
	 */
	public Brick makeBrick() {
		//brickMaterial.setColor("Color", ColorRGBA.Orange);
		//getBrickProperties().setColor(ColorRGBA.Orange);
		// cree la brique
		setMaterial(brickMaterial);
		// position de la brique
		setLocalTranslation(brickProperties.getPosition());
		// poids de la brique
		brickPhysic = new RigidBodyControl(0.1f);
		// ajout de la brique dans l'espace physique
		addControl(brickPhysic);
		if (isPhysic){
			
			bulletAppState.getPhysicsSpace().add(brickPhysic);
		}
		return this;
	}
	
	
	
	/**
	 * Cree une brique et le rend physique.
	 * @return une brique creee.
	 */
	public Brick makeBrick2() {
		//brickMaterial.setColor("Color", ColorRGBA.Orange);
		//getBrickProperties().setColor(ColorRGBA.Orange);
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
		if (rotateBrickH >= 3 || rotateBrickH <= -3){
			brickProperties.setRotateBrickH(rotateBrickH= 0);
			return rotateBrickH = 0;
		}
		else{
			brickProperties.setRotateBrickH(rotateBrickH+= 0.005);
			return rotateBrickH+= 0.005;
		}
		
	}
	
	/**
	 * Rotation  a droite de la brique.
	 * @return la valeur de la rotation de la brique.
	 */
	public float rotateRight() {
		if (rotateBrickH >= 3 || rotateBrickH <= -3){
			brickProperties.setRotateBrickH(rotateBrickH= 0);
			return rotateBrickH = 0;
		}
		else{
			brickProperties.setRotateBrickH(rotateBrickH-= 0.005);
			return rotateBrickH-= 0.005;
		}
		
		
	}
	
	/**
	 * Rotation  en haut de la brique.
	 * @return la valeur de la rotation de la brique.
	 */
	public float rotateUp() {
		if (rotateBrickV >= 3 || rotateBrickV <= -3){
			brickProperties.setRotateBrickV(rotateBrickV = 0);
			return rotateBrickV = 0;
			
		}
		else{
			brickProperties.setRotateBrickV(rotateBrickV+= 0.005);
			return rotateBrickV+= 0.005;
		}
	}
	
	/**
	 * Rotation  en bas de la brique.
	 * @return la valeur de la rotation de la brique.
	 */
	public float rotateDown() {
		if (rotateBrickV >= 3 || rotateBrickV <= -3){
			brickProperties.setRotateBrickV(rotateBrickV = 0);
			return rotateBrickV = 0;
		}
		else{
			brickProperties.setRotateBrickV(rotateBrickV-= 0.005);
			return rotateBrickV-= 0.005;
		}
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
	
	/**
	 * 
	 * accesseur à la taille
	 */
	
	public static float getBricklength() {
		return brickLength;
	}

	public static float getBrickwidth() {
		return brickWidth;
	}

	public static float getBrickheight() {
		return brickHeight;
	}
	
	public Brick isModelPhysic(){
		
		setMaterial(brickMaterial);
		
		setLocalTranslation(brickProperties.getPosition());
		
		brickPhysic = new RigidBodyControl(0.1f);
		
		addControl(brickPhysic);
		
			
			bulletAppState.getPhysicsSpace().add(brickPhysic);
		
		return this;
	}

	public float getRotateBrickH() {
		return rotateBrickH;
	}

	public void setRotateBrickH(float rotateBrickH) {
		this.rotateBrickH = rotateBrickH;
	}

	public float getRotateBrickV() {
		return rotateBrickV;
	}

	public void setRotateBrickV(float rotateBrickV) {
		this.rotateBrickV = rotateBrickV;
	}

	public  float getBrickLength() {
		return brickLength;
	}

	public  void setBrickLength(float brickLength) {
		Brick.brickLength = brickLength;
	}

	public  float getBrickWidth() {
		return brickWidth;
	}

	public  void setBrickWidth(float brickWidth) {
		Brick.brickWidth = brickWidth;
	}

	public float getBrickHeight() {
		return brickHeight;
	}

	public  void setBrickHeight(float brickHeight) {
		Brick.brickHeight = brickHeight;
	}

	public void setBrickLengthCalcul(float brickLengthCalcul) {
		this.brickLengthCalcul = brickLengthCalcul;
	}

	public float getBrickLengthCalcul() {
		return brickLengthCalcul;
	}

	public void setBrickWidthCalcul(float brickWidthCalcul) {
		this.brickWidthCalcul = brickWidthCalcul;
	}

	public float getBrickWidthCalcul() {
		return brickWidthCalcul;
	}

	public void setBrickHeightCalcul(float brickHeightCalcul) {
		this.brickHeightCalcul = brickHeightCalcul;
	}

	public float getBrickHeightCalcul() {
		return brickHeightCalcul;
	}
	
	
	
}
