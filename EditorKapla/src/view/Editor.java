package view;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.geomitries.Brick;
import model.geomitries.BrickProperties;
import model.geomitries.Room;
import model.geomitries.Table;
import model.geomitries.TableLeg;
import model.history.ActionCreate;
import model.history.ActionDelete;
import model.history.History;
import model.history.StdHistory;
import model.nifty.CommonBuilders;
import model.nifty.ControlStyles;

import com.itextpdf.text.log.SysoLogger;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyMethodInvoker;
import de.lessvoid.nifty.builder.LayerBuilder;
import de.lessvoid.nifty.builder.PanelBuilder;
import de.lessvoid.nifty.builder.ScreenBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.examples.defaultcontrols.common.MenuButtonControlDefinition;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;

/**
 * Classe modelisant l'editeur de kapla
 * @author Groupe C M1GIL 2013
 */

@SuppressWarnings("deprecation")
public class Editor extends SimpleApplication 
implements ActionListener,ScreenController {

	private static CommonBuilders builders = new CommonBuilders();
	private ControlStyles styles;  
	private NiftyJmeDisplay niftyDisplay;
	private Nifty nifty;
	private ArrayList<Brick> brickList;
	private History history;

//	private ActionMove am;
	private int nbOfPossibleUndo;
	private int nbOfPossibleRedo;
	private ActionCreate ac;
	private ActionDelete ad;

	/**
	 * Mise en place du physique.
	 */
	private BulletAppState bulletAppState;

	/**
	 * Brique courante.
	 */
	private Brick brick;

	/**
	 * Compteur de brique.
	 */
	private int brickComptor=0;
	/**
	 * Camera.
	 */
	private CharacterControl camera;
	/**
	 * Position de la camera.
	 */
	private Vector3f cameraPosition;

	/**
	 * Activation de la rotation de la camera.
	 */
	private boolean left = false, right = false, up = false, down = false;
	/**
	 * Activation du zoom de la camera.
	 */
	private boolean zoom = false, dezoom = false;
	/**
	 * Activation de la rotation de la piece.
	 */
	//verification de la rotation
	private boolean verif=false;
	/**
	 * Activation de la rotation de la piece.
	 */
	private boolean rotateLeft = false, rotateRight = false;
	private boolean rotateUp = false, rotateDown = false;
	
	/**
	 *  Auteur de la caméra
	 */
	private int camHauteur=0;
	
	/**
	 *  vérifier si le jeu est en mode physic 
	 */

	private boolean GamePhysic= false;
	 
	/**
	 * le result de la collision
	 */
	CollisionResults results = new CollisionResults();
	
	/**
	 * Initialisation des variables.
	 */
	
	@Override
	public void simpleInitApp() {
		cameraPosition = new Vector3f();
		brickList = new ArrayList<Brick>();
		// mise en place du physique
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		// creation de la salle et de la table.
		createRoom();
		createTable();
		initListeners();
		initNifty();

		mouseInput.setCursorVisible(true);
		flyCam.setEnabled(false); 

		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(
				1.5f, 6f, 1);
		camera = new CharacterControl(capsuleShape, 0.05f);
		camera.setGravity(0);
		camera.setPhysicsLocation(new Vector3f(1,25, 10));
		// ajout de la camera dans l'espace physique
		bulletAppState.getPhysicsSpace().add(camera);
		// creation d'un historique
		history = new StdHistory();
		// Initialisation des entiers
		this.nbOfPossibleRedo = 0;
		this.nbOfPossibleUndo = 0;
	}

	@Override
	public void simpleUpdate(float tpf) {
		Vector3f camDir = cam.getDirection().clone().multLocal(0.2f);
		Vector3f camLeft = cam.getLeft().clone().multLocal(0.2f);
		Vector3f camUp = cam.getUp().clone().mult(0.2f);
		cameraPosition.set(0, 0, 0);
		cam.lookAt(new Vector3f(1,1,1),new Vector3f(cameraPosition.x,
				cameraPosition.y, cameraPosition.z));

		if(left)       { cameraPosition.addLocal(camLeft); }
		if(right)      { cameraPosition.addLocal(camLeft.negate()); }
		if(up)         { cameraPosition.addLocal(camUp); camHauteur++;}
		if(down)       { cameraPosition.addLocal(camUp.negate());camHauteur--; }
		if(zoom)       { cameraPosition.addLocal(camDir); }
		if(dezoom)     { cameraPosition.addLocal(camDir.negate()); }
		
		// si rotation a gauche
		if ((results.size()>0)&&
				(results.getCollision(0).getGeometry().getName().equals("brick")) 
				&& (results.getCollision(0).getGeometry()== brick)){
			if(rotateLeft){ 
				brick.setLocalRotation(new Quaternion().fromAngles(0, 
						brick.rotateLeft(), 0));
				results.getCollision(0).getGeometry().setLocalRotation(new Quaternion().fromAngles(0, 
						brick.rotateLeft(), 0));
				brick = (Brick) results.getCollision(0).getGeometry();
				brick.getBrickPhysic().setPhysicsRotation(new Quaternion().fromAngles(0, 
						brick.rotateLeft(), 0));
				if (brick.getRotateBrickH() >= 1  && brick.getRotateBrickH() <= 2 || brick.getRotateBrickH() <= -1  && brick.getRotateBrickH() >= -2){
					float test = brick.getBrickLength();
					brick.setBrickLengthCalcul(brick.getBrickWidth());
					brick.setBrickWidthCalcul(test);
					System.out.println("ici l'araignée");
				}
				else{
					brick.setBrickLengthCalcul(brick.getBrickLength());
					brick.setBrickWidthCalcul(brick.getBrickWidth());
				}
				//mettre à jour dans la liste
				for (Brick brickTemp : brickList){
					if (brickTemp.getBrickProperties().getId() 
								== brick.getBrickProperties().getId()){
							brickTemp.getBrickProperties().setPosition(
									brick.getLocalTranslation());
					}
				}	
			}
			// si rotation a droite
			if(rotateRight){
				brick.setLocalRotation(new Quaternion().fromAngles(0,
						brick.rotateRight(), 0));
				results.getCollision(0).getGeometry().setLocalRotation(new Quaternion().fromAngles(0, 
						brick.rotateRight(), 0));
				brick = (Brick) results.getCollision(0).getGeometry();
				brick.getBrickPhysic().setPhysicsRotation(new Quaternion().fromAngles(0, 
						brick.rotateRight(), 0));
				if (brick.getRotateBrickH() >= 1  && brick.getRotateBrickH() <= 2 || brick.getRotateBrickH() <= -1  && brick.getRotateBrickH() >= -2){
					float test = brick.getBrickLength();
					brick.setBrickLengthCalcul(brick.getBrickWidth());
					brick.setBrickWidthCalcul(test);
					System.out.println("ici l'araignée");
				}
				else{
					brick.setBrickLengthCalcul(brick.getBrickLength());
					brick.setBrickWidthCalcul(brick.getBrickWidth());
				}
				
				//mettre à jour dans la liste
				for (Brick brickTemp : brickList){
					if (brickTemp.getBrickProperties().getId() 
								== brick.getBrickProperties().getId()){
							brickTemp.getBrickProperties().setPosition(
									brick.getLocalTranslation());
					}
				}
			}
			// si rotation en haut
			if(rotateUp){ 
				brick.setLocalRotation(new Quaternion().fromAngles(0, 
						0, brick.rotateUp()));
				results.getCollision(0).getGeometry().setLocalRotation(new Quaternion().fromAngles(0, 
						0, brick.rotateUp()));
				brick = (Brick) results.getCollision(0).getGeometry();
				brick.getBrickPhysic().setPhysicsRotation(new Quaternion().fromAngles(0, 
						0, brick.rotateUp()));
				System.out.println(brick.getRotateBrickV());
				if (brick.getRotateBrickV() >= 1  && brick.getRotateBrickV() <= 2 || brick.getRotateBrickV() <= -1  && brick.getRotateBrickV() >= -2){
					float test = brick.getBrickLength();
					brick.setBrickLengthCalcul(brick.getBrickHeight());
					brick.setBrickHeightCalcul(test);
					System.out.println("ici l'araignée");
					if (verif ==false){
						brick.setLocalTranslation(brick.getBrickProperties().getPosition().x,
								brick.getBrickLength(),
								brick.getBrickProperties().getPosition().z);
						brick.getBrickPhysic().setPhysicsLocation(new Vector3f(brick.getBrickProperties().getPosition().x,
								brick.getBrickLength(),
								brick.getBrickProperties().getPosition().z));
						verif =true;
					}	
				}
				else{
					brick.setBrickLengthCalcul(brick.getBrickLength());
					brick.setBrickHeightCalcul(brick.getBrickHeight());
					if (verif ==true){
						brick.setLocalTranslation(brick.getBrickProperties().getPosition().x,
								brick.getBrickHeight(),
								brick.getBrickProperties().getPosition().z);
						brick.getBrickPhysic().setPhysicsLocation(new Vector3f(brick.getBrickProperties().getPosition().x,
								brick.getBrickHeight(),
								brick.getBrickProperties().getPosition().z));
						verif =false;
					}
				}
			
				//mettre à jour dans la liste
				for (Brick brickTemp : brickList){
					if (brickTemp.getBrickProperties().getId() 
								== brick.getBrickProperties().getId()){
							brickTemp.getBrickProperties().setPosition(
									brick.getLocalTranslation());
					}
				}
			}
			// si rotation en bas 
			if(rotateDown){
				brick.setLocalRotation(new Quaternion().fromAngles(0,
						0, brick.rotateDown()));
				results.getCollision(0).getGeometry().setLocalRotation(new Quaternion().fromAngles(0,
						0, brick.rotateDown()));
				brick = (Brick) results.getCollision(0).getGeometry();
				brick.getBrickPhysic().setPhysicsRotation(new Quaternion().fromAngles(0,
						0, brick.rotateDown()));
				//System.out.println(brick.getRotateBrickV());
				if (brick.getRotateBrickV() >= 1  && brick.getRotateBrickV() <= 2 || brick.getRotateBrickV() <= -1  && brick.getRotateBrickV() >= -2){
					float test = brick.getBrickLength();
					brick.setBrickLengthCalcul(brick.getBrickHeight());
					brick.setBrickHeightCalcul(test);
					System.out.println("ici l'araignée");
					if (verif ==false){
						brick.setLocalTranslation(brick.getBrickProperties().getPosition().x,
								brick.getBrickLength(),
								brick.getBrickProperties().getPosition().z);
						brick.getBrickPhysic().setPhysicsLocation(new Vector3f(brick.getBrickProperties().getPosition().x,
								brick.getBrickLength(),
								brick.getBrickProperties().getPosition().z));
						verif =true;
					}		
				}
				else{
					brick.setBrickLengthCalcul(brick.getBrickLength());
					brick.setBrickHeightCalcul(brick.getBrickHeight());
					if (verif ==true){
						brick.setLocalTranslation(brick.getBrickProperties().getPosition().x,
								brick.getBrickHeight(),
								brick.getBrickProperties().getPosition().z);
						brick.getBrickPhysic().setPhysicsLocation(new Vector3f(brick.getBrickProperties().getPosition().x,
								brick.getBrickHeight(),
								brick.getBrickProperties().getPosition().z));
						verif =false;
					}		
				}
				
				//mettre à jour dans la liste
				for (Brick brickTemp : brickList){
					if (brickTemp.getBrickProperties().getId() 
								== brick.getBrickProperties().getId()){
							brickTemp.getBrickProperties().setPosition(
									brick.getLocalTranslation());
					}
				}
			}
		}
		camera.setWalkDirection(cameraPosition);
		cam.setLocation(camera.getPhysicsLocation());
	}


	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		Editor app = new Editor();

		// parametres de la fenetre
		AppSettings gameSettings = new AppSettings(false);
		gameSettings.setResolution(640, 480);
		gameSettings.setFullscreen(false);
		gameSettings.setVSync(false);
		gameSettings.setTitle("Kapla Editor");
		gameSettings.setUseInput(true);
		gameSettings.setFrameRate(500);
		gameSettings.setSamples(0);
		gameSettings.setRenderer("LWJGL-OpenGL2");
		app.settings = gameSettings;
		app.setShowSettings(false);
		app.setDisplayStatView(false);
		app.setDisplayFps(false);
		// Pour enlever les lignes ecrites a chaque fois dans le terminal par nifty
		Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE); 
		Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE);
		app.start();
	}

	/**
	 * Creation et ajout de la table.
	 */
	public void createTable(){
		Table table =  new Table(bulletAppState, assetManager).makeTable();
		for(int i = 0; i < 4; i++){
			TableLeg tableLeg = table.getTableLeg(i);
			rootNode.attachChild(tableLeg);
		}
		rootNode.attachChild(table);
	}

	/**
	 * Cree la salle autour de la table
	 */
	public void createRoom(){
		Room room = new Room(bulletAppState, assetManager);
		for(int i=0;i<6;i++){
			rootNode.attachChild(room.getWalls(i));
		}
	}

	/**
	 * Initialisation des ecouteurs.
	 */
	private void initListeners() {
		inputManager.addMapping("create",
				new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addListener(actionListener, "create");
		inputManager.addMapping("select",
				new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addListener(actionListener, "select");
		inputManager.addMapping("drag",
				new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addListener(analogListener, "drag");

		inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
		inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
		inputManager.addMapping("Zoom", new KeyTrigger(KeyInput.KEY_Z));
		inputManager.addMapping("Dezoom", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_UP));
		inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_DOWN));
		inputManager.addMapping("rotateRight", new KeyTrigger(KeyInput.KEY_T));
		inputManager.addMapping("rotateLeft", new KeyTrigger(KeyInput.KEY_R));
		inputManager.addMapping("rotateUp", new KeyTrigger(KeyInput.KEY_F));
		inputManager.addMapping("rotateDown", new KeyTrigger(KeyInput.KEY_V));
		inputManager.addListener(this, "Left");
		inputManager.addListener(this, "Right");
		inputManager.addListener(this, "Zoom");
		inputManager.addListener(this, "Dezoom");
		inputManager.addListener(this, "Up");
		inputManager.addListener(this, "Down");
		inputManager.addListener(this, "rotateRight");
		inputManager.addListener(this, "rotateLeft");
		inputManager.addListener(this, "rotateUp");
		inputManager.addListener(this, "rotateDown");
		
		//les couleurs
		inputManager.addMapping("couleur1", new KeyTrigger(KeyInput.KEY_1));
		inputManager.addListener(this, "couleur1");
		inputManager.addMapping("couleur2", new KeyTrigger(KeyInput.KEY_2));
		inputManager.addListener(this, "couleur2");
		inputManager.addMapping("couleur3", new KeyTrigger(KeyInput.KEY_3));
		inputManager.addListener(this, "couleur3");
		inputManager.addMapping("couleur4", new KeyTrigger(KeyInput.KEY_4));
		inputManager.addListener(this, "couleur4");
		inputManager.addMapping("couleur5", new KeyTrigger(KeyInput.KEY_5));
		inputManager.addListener(this, "couleur5");
		inputManager.addMapping("couleur6", new KeyTrigger(KeyInput.KEY_6));
		inputManager.addListener(this, "couleur6");
		inputManager.addMapping("couleur7", new KeyTrigger(KeyInput.KEY_7));
		inputManager.addListener(this, "couleur7");
		inputManager.addMapping("couleur8", new KeyTrigger(KeyInput.KEY_8));
		inputManager.addListener(this, "couleur8");
		inputManager.addMapping("couleur9", new KeyTrigger(KeyInput.KEY_9));
		inputManager.addListener(this, "couleur9");
	}   

	/**
	 * Controleur clic souris(creation et selection brique).
	 */
	private ActionListener actionListener = new ActionListener() {
		public void onAction(String name, boolean keyPressed, float tpf) {
			// si creation d'une nouvelle brique
			if(name.equals("create") && !keyPressed ) {
				results = new CollisionResults();
				// convertit le clic sur l'ecran en coordonnees 3d
				Vector2f click2d = inputManager.getCursorPosition();
				Vector3f click3d = viewPort.getCamera().getWorldCoordinates(
						new Vector2f(click2d.x, click2d.y), 0f).clone();
				Vector3f dir = viewPort.getCamera().getWorldCoordinates(
						new Vector2f(click2d.x, click2d.y), 1f);
				Ray ray = new Ray(click3d, dir);
				/* 	collecte des intersections entre ray et tous les noeuds de 
					la liste results */
				rootNode.collideWith(ray, results);
				Vector3f tempRes = new Vector3f(results.getClosestCollision()
						.getContactPoint().x,Brick.getBrickheight(),results.getClosestCollision()
						.getContactPoint().z);
				if (GamePhysic == false){
					brick = new Brick(new BrickProperties(brickComptor++,
							new Vector3f(tempRes), null), bulletAppState,
									assetManager).makeBrick();
				}
				else{
					brick = new Brick(new BrickProperties(brickComptor++,
							new Vector3f(tempRes), null), bulletAppState,
									assetManager).makeBrick2();
					
				}
				if (results.size() > 0){
					brick.getBrickProperties().setPosition(new Vector3f(tempRes));
				}else{
					brick.getBrickProperties().setPosition(new Vector3f(
							new Vector3f(dir.x/100+cam.getLocation().x,
							dir.y/100+cam.getLocation().y,0)));
				}
				// ajout de la brique dans la liste
				brickList.add(brick);
				// ajout de la brique au rootNode
				ac = new ActionCreate(getEditor(), brick.getBrickProperties().getId(), brick);
				ac.act();
				ac.canUndo();
				nbOfPossibleUndo += 1;
				history.add(ac);
			}
			
			// si une brique selectionnee
			if (name.equals("select") && !keyPressed) {
				CollisionResults results = new CollisionResults();
				// convertit le clic sur l'ecran en coordonnees 3d
				Vector2f click2d = inputManager.getCursorPosition();
				Vector3f click3d = cam.getWorldCoordinates(new Vector2f(
						click2d.x, click2d.y), 0f).clone();
				Vector3f dir = cam.getWorldCoordinates(new Vector2f(
						click2d.x, click2d.y), 1f).subtractLocal(click3d);				
				Ray ray = new Ray(click3d, dir);				
				/* 	collecte des intersections entre ray et tous les noeuds de 
					la liste results */
				rootNode.collideWith(ray, results);
				if (results.size() > 0) {
					// cible la plus proche
					Geometry target = results.getCollision(0).getGeometry();
					if (target.getName().equals("brick") ) {
						brick = (Brick) results.getCollision(0).getGeometry();
						
						Iterator<Brick> iter = brickList.iterator();
						// parcours des briques selectionnees
						while(iter.hasNext()){
							Brick brickTemp = (Brick)iter.next();
							if(brickTemp==brick){
								brickTemp.setSelectedTexture(assetManager);
							}
							else{
								brickTemp.getAncienneTexture(assetManager);
							}							
						}
					}
				}
			}
		}
	};    

	/**
	 * Controleur AnalogListener(clic souris)
	 */
	private AnalogListener analogListener = new AnalogListener() {    
		public void onAnalog(String name, float value, float tpf) { 	
			 results = new CollisionResults();
			// convertit le clic sur l'ecran en coordonnees 3d
			Vector2f click2d = inputManager.getCursorPosition();
			Vector3f click3d = cam.getWorldCoordinates(new Vector2f(
					click2d.x, click2d.y), 0f).clone();
			Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x,
					click2d.y), 1f).subtractLocal(click3d);			
			Ray ray = new Ray(click3d, dir);
			/* 	collecte des intersections entre ray et tous les noeuds de 
				la liste results */
			rootNode.collideWith(ray, results);
			if ((results.size()>0)&&
				(results.getCollision(0).getGeometry().getName().equals("brick")) 
				&& (results.getCollision(0).getGeometry()== brick)){
				// si deplacement de la brique
				if (name.equals("drag")) {
					//moteur physique
					Vector3f v = results.getCollision(0).getContactPoint();										
					System.out.println(camHauteur);
					//condition pour la caméra, si l'on souhaite en rajouter par la suite
						
						if(cam.getLeft().clone().multLocal(0.2f).x <=0){
							brick.setLocalTranslation(v.x ,  brick.getBrickProperties().getPosition().y, v.z);
							results.getCollision(0).getGeometry().setLocalTranslation(
									v.x ,  brick.getBrickProperties().getPosition().y, v.z);
								}
						else{
							brick.setLocalTranslation(v.x ,  brick.getBrickProperties().getPosition().y, v.z);
							results.getCollision(0).getGeometry().setLocalTranslation(
									v.x ,  brick.getBrickProperties().getPosition().y, v.z);
						}
						// Position de la brique en cours de mouvement
						float brickX = brick.getBrickProperties().getPosition().getX();
						float brickY = brick.getBrickProperties().getPosition().getY();
						float brickZ = brick.getBrickProperties().getPosition().getZ();
						int nbBrickSurLaPile = 0;
						for (Brick brickTemp : brickList)
						{
							// Position de la brique qu'on est en train de comparer
							float brickTempX = brickTemp.getBrickProperties().getPosition().getX();
							float brickTempZ = brickTemp.getBrickProperties().getPosition().getZ();
							brickTemp.getBrickProperties().getPosition().getX();
							brickTemp.getBrickProperties().getPosition().getX();
							if (brickTemp!=brick)
							{
								// On vérifie si on est sur la même position qu'une autre brique
								if(Math.abs(brickX-brickTempX)<brickTemp.getBrickLengthCalcul()+ brick.getBrickLengthCalcul())
								{
									if(Math.abs(brickZ-brickTempZ)<brickTemp.getBrickWidthCalcul()+brick.getBrickWidthCalcul())
									{	
										nbBrickSurLaPile++;
										System.out.println("5");
										System.out.println("------ ma brique  " +brick.getRotateBrickV()+" " + brick.getBrickLengthCalcul()+"------ "+ brick.getBrickHeightCalcul());
										System.out.println("------ ma temp  " + brickTemp.getRotateBrickV()+" " + brickTemp.getBrickLengthCalcul()+"------ "+ brickTemp.getBrickHeightCalcul());				
									}
								}
							}
						}						
						// On gère le cas ou ya deja au moins une brique à notre position
						if (nbBrickSurLaPile != 0)
						{
							// On recherche la brique la plus haute de cette pile
							Brick brickLaplusHaute = chercherBriqueLaPlusHaute(brickX,brickY,brickZ);
							
							// On vérifie si on est en haut de la pile ou pas
							if (brick.getBrickProperties().getId() == brickLaplusHaute.getBrickProperties().getId()
								|| brickY > brickLaplusHaute.getBrickProperties().getPosition().getY()
							)
							{
								// On est tout en haut de la pile ==> on peut rester dessus
								brick.setLocalTranslation(brickX,brickY,brickZ);
							}
							else
							{
								System.out.println("7");
								System.out.println("brick.getBrickHeightCalcul()  "+ brick.getBrickHeightCalcul());
								System.out.println("brickLaplusHaute.getBrickHeightCalcul()  "+ brickLaplusHaute.getBrickHeightCalcul());
								if (brick.getBrickHeightCalcul()==brickLaplusHaute.getBrickHeightCalcul()){
									System.out.println("boucle 1");
									brick.setLocalTranslation(brickX,brickY+ brick.getBrickHeightCalcul()+brickLaplusHaute.getBrickHeightCalcul(),brickZ);
								}
								else if (brick.getBrickHeightCalcul()<brickLaplusHaute.getBrickHeightCalcul()){
									System.out.println("boucle 2");
									brick.setLocalTranslation(brickX,brickY+brick.getBrickHeight()*2+ brick.getBrickHeightCalcul()*2+brickLaplusHaute.getBrickHeightCalcul(),brickZ);
									
								}
								else if (brick.getBrickHeightCalcul()>brickLaplusHaute.getBrickHeightCalcul()){
									System.out.println("boucle 3");
									brick.setLocalTranslation(brickX,brickY+brick.getBrickHeight()+ brick.getBrickHeightCalcul()*2+brickLaplusHaute.getBrickHeightCalcul(),brickZ);	
								}
							}
						}
						else if(brickY != brick.getBrickHeightCalcul())
						{
							float brickXSauv =brickX;
							float brickYSauv =brickY;
							float brickZSauv =brickZ;
							// On revérifie tout avec le Y en moins
							nbBrickSurLaPile = 0;
							for (Brick brickTemp : brickList)
							{
								// Position de la brique qu'on est en train de comparer
								float brickTempX = brickTemp.getBrickProperties().getPosition().getX();
								float brickTempZ = brickTemp.getBrickProperties().getPosition().getZ();
	
								if (brickTemp!=brick)
								{
									// On vérifie si on est sur la même position qu'une autre brique
									if(Math.abs(brickX-brickTempX)<brickTemp.getBrickLengthCalcul()+ brick.getBrickLengthCalcul())
									{
										if(Math.abs(brickZ-brickTempZ)<brickTemp.getBrickWidthCalcul()+brick.getBrickWidthCalcul())
										{
											nbBrickSurLaPile++;
											System.out.println("5 bis");
										}
									}
								}
							}
							// On gère le cas ou ya deja au moins une brique à notre position
							if (nbBrickSurLaPile != 0)
							{
								// On recherche la brique la plus haute de cette pile
								Brick brickLaplusHaute = chercherBriqueLaPlusHaute(brickX,brickY,brickZ);
								// On vérifie si on est en haut de la pile ou pas
								if (brick.getBrickProperties().getId() == brickLaplusHaute.getBrickProperties().getId()
										|| brickY > brickLaplusHaute.getBrickProperties().getPosition().getY()
									)
								{
									// On est tout en haut de la pile ==> on peut rester dessus
									brick.setLocalTranslation(brickXSauv,brickYSauv,brickZSauv);
								}
								else
								{
									//On est pas tout en haut de la pile ==> on veut monter dessus
									if (brick.getBrickHeightCalcul()==brickLaplusHaute.getBrickHeightCalcul()){
										System.out.println("boucle 1");
										brick.setLocalTranslation(brickX,brickY+ brick.getBrickHeightCalcul()+brickLaplusHaute.getBrickHeightCalcul(),brickZSauv);
									}
									else if (brick.getBrickHeightCalcul()<brickLaplusHaute.getBrickHeightCalcul()){
										System.out.println("boucle 2");
										brick.setLocalTranslation(brickX,brickY+brick.getBrickHeight()*2+ brick.getBrickHeightCalcul()*2+brickLaplusHaute.getBrickHeightCalcul(),brickZSauv);
										
									}
									else if (brick.getBrickHeightCalcul()>brickLaplusHaute.getBrickHeightCalcul()){
										System.out.println("boucle 3");
										brick.setLocalTranslation(brickX,brickY+brick.getBrickHeight()+ brick.getBrickHeightCalcul()+brickLaplusHaute.getBrickHeightCalcul(),brickZSauv);
									}
								}
							}
							else
							{
								System.out.println("8 bis");
								brick.setLocalTranslation(brickX,brick.getBrickHeightCalcul(),brickZSauv);
							}
						}
						else
						{
							System.out.println("9");
							// Cas classique, on bouge selon la souris
							brick.setLocalTranslation(brickX,brickY,brickZ);
						}
						
					}
						for (Brick brickTemp : brickList){
						if (brickTemp.getBrickProperties().getId() 
								== brick.getBrickProperties().getId()){
							brickTemp.getBrickProperties().setPosition(
									brick.getLocalTranslation());
						}
					
				}
				brick = (Brick) results.getCollision(0).getGeometry();
				brick.getBrickPhysic().setPhysicsLocation(new Vector3f( brick.getBrickProperties().getPosition().x ,  brick.getBrickProperties().getPosition().y,  brick.getBrickProperties().getPosition().z));
			}			
		}
	};
	
	public Brick chercherBriqueLaPlusHaute(float brickX, float brickY, float brickZ)
	{
		Brick brickLaPlusHaute = null;
		float yLePlusHaut = -50;
		for (Brick brickTemp : brickList)
		{
			// Position de la brique qu'on est en train de comparer
			float brickTempX = brickTemp.getBrickProperties().getPosition().getX();
			float brickTempY = brickTemp.getBrickProperties().getPosition().getY();
			float brickTempZ = brickTemp.getBrickProperties().getPosition().getZ();				
			if (brickTemp!=brick)
			{
				// On vérifie si la brique est sur la position recherchée
				if(Math.abs(brickX-brickTempX)<brickTemp.getBrickLengthCalcul()+brick.getBrickLengthCalcul())
				{
					if(Math.abs(brickZ-brickTempZ)<brickTemp.getBrickWidthCalcul()+brick.getBrickWidthCalcul())
					{
						if(brickTempY >= yLePlusHaut)
						{
							yLePlusHaut = brickTempY;
							brickLaPlusHaute = brickTemp;
						}
					}
				}
			}
		}
		return brickLaPlusHaute;
	}

	public void onAction(String name, boolean isPressed, float tpf) {
		if (name.equals("Left")) {
			left = isPressed;
		} else if (name.equals("Right")) {
			right = isPressed;
		} else if (name.equals("Zoom")) {
			zoom = isPressed;
		} else if (name.equals("Dezoom")) {
			dezoom = isPressed;
		} else if (name.equals("Up")) {
			up = isPressed;
		} else if (name.equals("Down")) {
			down = isPressed;
		}else if (name.equals("rotateLeft")) {
			rotateLeft = isPressed;
		}else if (name.equals("rotateRight")) {
			rotateRight = isPressed;
		}else if (name.equals("rotateUp")) {
			rotateUp = isPressed;
		}else if (name.equals("rotateDown")) {
			rotateDown = isPressed;
		}
		else if (name.equals("couleur1")) {
			brick.changeTexture("BrickJaune",assetManager);
		}else if (name.equals("couleur2")) {
			brick.changeTexture("BrickNoire",assetManager);
		}else if (name.equals("couleur3")) {
			brick.changeTexture("BrickCiel",assetManager);
		}else if (name.equals("couleur4")) {
			brick.changeTexture("BrickPomme",assetManager);
		}else if (name.equals("couleur5")) {
			brick.changeTexture("BrickGris",assetManager);
		}else if (name.equals("couleur6")) {
			brick.changeTexture("BrickVerte",assetManager);
		}else if (name.equals("couleur7")) {
			brick.changeTexture("BrickViolet",assetManager);
		}else if (name.equals("couleur8")) {
			brick.changeTexture("BrickRouge",assetManager);
		}else if (name.equals("couleur9")) {
			brick.changeTexture("BrickRose",assetManager);
		}
	}

	/**
	 * Initialise nifty avec les boutons.
	 */
	private void initNifty(){
		niftyDisplay = new NiftyJmeDisplay(
				assetManager, inputManager, audioRenderer, guiViewPort);
		nifty = niftyDisplay.getNifty();
		guiViewPort.addProcessor(niftyDisplay);

		nifty.loadStyleFile("nifty-default-styles.xml");
		nifty.loadControlFile("nifty-default-controls.xml");

		// register
		styles = new ControlStyles(nifty);
		styles.registerMenuButtonHintStyle();
		styles.registerStyles();
		styles.registerConsolePopup();

		// ajout de controles
		MenuButtonControlDefinition.register(nifty);
		nifty.addScreen("Screen", new ScreenBuilder("NavigateScreen"){{
			// proprietes de l'ecran
			controller(new DefaultScreenController());      
			// on definit le layer
			layer(new LayerBuilder("Layer") {{
				childLayoutVertical();

				// panel horizontal
				panel(new PanelBuilder("navigation") {{
					width("100%");
					height("40px");
					backgroundColor("#5588");
					childLayoutHorizontal();
					padding("20px");
					// bouton nouvelle partie
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_new", "Nouveau"
							,"Permet de creer une nouvelle partie.", "90px"));
					panel(builders.hspacer("10px"));
					// bouton charger
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_load", "Charger"
							,"Permet de charger une partie enregistree.", "90px"));
					panel(builders.hspacer("10px"));
					//bouton sauvegarder
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_save", "Sauvegarder"
							,"Permet de sauvgarder la partie.","90px"));
					panel(builders.hspacer("10px"));
					// Bouton Undo
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_undo", "Defaire"
							,"Permet de defaire une action.","90px"));
					panel(builders.hspacer("10px"));
					// Bouton Redo
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_redo", "Refaire"
							,"Permet de refaire une action.","90px"));
					panel(builders.hspacer("10px"));
					//bouton mettre le model physics
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_physic", "Model physic"
							,"Permer de mettre le model physic","90px"));
					panel(builders.hspacer("10px"));
					
				}});

				// panel vertical
				panel(new PanelBuilder("edition") {{
					width("60px");
					height("100%");
					alignRight();
					backgroundColor("#5588");
					childLayoutVertical();
					padding("40px");
					// bouton supprimer
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_delete", "Supp"
							,"Permet de supprimer un kapla.","40px"));
					panel(builders.vspacer("10px"));
					// bouton transparent
				//	control(MenuButtonControlDefinition.getControlBuilder(
					//		"Button_transparent", "Trans"
						//	,"Permet de rendre un kapla transparent.","40px"));
					//panel(builders.vspacer("10px"));
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_transparent", "Aide"
							,"Appuyez sur le clic droit pour  créer un kapla.\n" +
							 "Appuyez sur le clic gauche pour  sélectionner le kapla ciblé.\n" +
							 "Restez appuyé avec le clic gauche sur un kapla, et avec la souris déplacer le. \n" +
							 "Dirigez la caméra, en utilisant les touches directionnelles \n" +
							 "Zoomez et dézoomez sur la construction, en utilisant Z et D\n " +
							 "Effectuez une rotation horizontale, en utilisant R et T\n" +
							 "Effectuez une rotation verticale, en utilisant F et V\n" +
							 "Changez la couleur d'un kapla en le sélectionnant et en appuyant sur un chiffre(1 à 9)\n" +
							 "Les boutons Nouveau, Sauvegarder, Charger sont explicites\n" +
							 "Les boutons Défaire et Refaire permettent de supprimer et de faire réapparaître un kapla\n" +
							 "Le bouton model physic permet d'appliquer un modèle physique à l'ensemble des kaplas, ainsi qu'aux futurs créés" +
							 "  "	,"40px"));
					panel(builders.vspacer("10px"));
				}});
			}});
		}}.build(nifty));
		// </screen>
		nifty.gotoScreen("Screen"); // demarre l'ecran
		// Controleur pour le bouton Nouveau
		Element buttonNew = nifty.getCurrentScreen()
				.findElementByName("Button_new");
		buttonNew.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "removeAllKaplas()", this));
		// Controleur pour le bouton save
		Element buttonSave = nifty.getCurrentScreen()
				.findElementByName("Button_save");
		buttonSave.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "save()", this));
		// Controleur pour le bouton load
		Element buttonLoad = nifty.getCurrentScreen()
				.findElementByName("Button_load");
		buttonLoad.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "load()", this));
		// Controleur pour le bouton undo
		Element buttonUndo = nifty.getCurrentScreen()
				.findElementByName("Button_undo");
		buttonUndo.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "undo()", this));
		// Controleur pour le bouton redo
		Element buttonRedo = nifty.getCurrentScreen()
				.findElementByName("Button_redo");
		buttonRedo.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "redo()", this));
		
		//controller pour mettre le model physics
		Element buttonPhysic = nifty.getCurrentScreen()
		.findElementByName("Button_physic");
		buttonPhysic.getElementInteraction().getPrimary().setOnClickMethod(
		new NiftyMethodInvoker(nifty, "physic()", this));

		// Controleur pour le button supprimer
		Element buttonDelete = nifty.getCurrentScreen()
				.findElementByName("Button_delete");
		buttonDelete.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "delete()", this));
		// Controleur pour le button transarent
	}
	
	/**
	* model physic. 
	*/
	public void physic() {
		//liste temporaire de Brick
		ArrayList<Brick> listtemp = new ArrayList<Brick>();
		Iterator<Brick> iter = brickList.iterator();
		// parcours des briques selectionnees
		while(iter.hasNext()){
			Brick brickTemp = (Brick)iter.next();
			Brick b = new Brick(new BrickProperties(brickTemp.getBrickProperties().getId(),
					new Vector3f(brickTemp.getBrickProperties().getPosition()), null), bulletAppState,
							assetManager).makeBrick2();
			listtemp.add(b);
			rootNode.detachChild(brickTemp);
			iter.remove();
		}
		brickList= listtemp;
		for (Brick brickTemp : brickList){	
			rootNode.attachChild(brickTemp);
		}
		GamePhysic = true;
	}
	
	/**
	 * Sauvegarde de la construction. 
	 */
	public void save() {
		try {
			FileOutputStream fichier = new FileOutputStream(
					System.getProperty("user.home")+"/ListBrick.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			ArrayList<BrickProperties> listBP = new ArrayList<BrickProperties>();
			for(Brick bri : brickList){
				listBP.add(bri.getBrickProperties());
			}
			oos.writeObject(listBP);
			oos.flush();
			oos.close();
			System.out.println("sauvegarder!");
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Charge une construction.
	 */
	@SuppressWarnings("unchecked")
	public void load (){
		try{
			FileInputStream fichier = new FileInputStream(
					System.getProperty("user.home")+"/ListBrick.ser");
			ObjectInputStream ois = new ObjectInputStream(fichier);
			List<BrickProperties> Liste = (ArrayList<BrickProperties>) ois.readObject();
			removeAllKaplas();
			for (BrickProperties brickTemp : Liste){
				brick = new Brick(brickTemp, bulletAppState, assetManager).makeBrick();
				rootNode.attachChild(brick);
				brickList.add(brick);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Supprime tous les kaplas de la liste et du rootNode.
	 */
	public void removeAllKaplas(){
		for(Brick brick : brickList){
			rootNode.detachChild(brick);
		}
		brickList = new ArrayList<Brick>();
		history = new StdHistory();
	}

	/**
	 * Permet de supprimer la pièce actuellement secltionnée.
	 */
	public void delete(){
		Iterator<Brick> iter = brickList.iterator();
		// parcours des briques selectionnees
		while(iter.hasNext()){
			Brick brickTemp = (Brick)iter.next();
			if(brickTemp==brick){
				//rootNode.detachChild(brickTemp);
				//iter.remove();
				ad = new ActionDelete(getEditor(), brick.getBrickProperties().getId(), brick);
				ad.act();
				ad.canUndo();
			}			
		}
		nbOfPossibleUndo += 1;
		history.add(ad);
	}
	
	/**
	 * Permet de dï¿½faire une action
	 */
	public void undo(){
		try{
			nbOfPossibleUndo = nbOfPossibleUndo - 1;
			nbOfPossibleRedo = nbOfPossibleRedo + 1;
			history.goBackward();
			System.out.println(history.getCurrentPosition());
			System.out.println(history.getCurrentElement());
			System.out.println(history.getCurrentElement().getState());
			history.getCurrentElement().act();
		}catch(IllegalArgumentException e){}
	}

	/**
	 * Permet de refaire une action
	 */
	public void redo() {
		try{
			if (history.getCurrentPosition() == -1) {
				history.goForward();
			}
			
	
			nbOfPossibleUndo = nbOfPossibleUndo + 1;
			nbOfPossibleRedo = nbOfPossibleRedo - 1;
			history.getCurrentElement().act();
			history.goForward();
		}catch(IndexOutOfBoundsException e){}
		catch(IllegalArgumentException e){}
	}

	public int nbOfPossibleUndo() {
		return nbOfPossibleUndo;
	}

	public int nbOfPossibleRedo() {
		return nbOfPossibleRedo;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public Editor getEditor() {
		return this;
	}

	public History getHistroy() {
		return history;
	}

	@Override
	public void bind(Nifty arg0, Screen arg1) {
	}

	@Override
	public void onEndScreen() {
	}

	@Override
	public void onStartScreen() {
	}

	/*	public void onButton(int button, boolean pressed, int x, int y) {
		MouseInputListener inputEvent = new MouseInputEvent(newX, Display.getDisplayMode().getHeight() - newY, bLastLeftMouseWasDown);
		eventsMouse.add(inputEvent);
	}*/
}
