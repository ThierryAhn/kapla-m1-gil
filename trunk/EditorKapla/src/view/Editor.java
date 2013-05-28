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
import com.jme3.renderer.RenderManager;
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
	//private boolean transparent;
	private int nbOfPossibleUndo;
	private int nbOfPossibleRedo;
	private ActionCreate ac;
	private ActionDelete ad;
//	private ActionMove am;

	private Vector3f oldPositionBrick;
	private Vector3f newPositionBrick;

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
	private boolean rotateLeft = false, rotateRight = false;
	private boolean rotateUp = false, rotateDown = false;

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
		camera.setPhysicsLocation(new Vector3f(0,5, 20));
		// ajout de la camera dans l'espace physique
		bulletAppState.getPhysicsSpace().add(camera);
		// Cr�ation d'un historique
		history = new StdHistory();
		// Initialisation des entiers
		this.nbOfPossibleRedo = 0;
		this.nbOfPossibleUndo = 0;

		this.oldPositionBrick = null;
		this.newPositionBrick = null;



		/* Inititialisation du bool�en pour la transparence
		this.transparent = false;*/


		// initialisation des boolean
		/*undo = false;
		redo = false;*/

		//this.currentPosition = 0;

		// Initialisation de la liste d'action
		//action = new ArrayList<String>();
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
		if(up)         { cameraPosition.addLocal(camUp); }
		if(down)       { cameraPosition.addLocal(camUp.negate()); }
		if(zoom)       { cameraPosition.addLocal(camDir); }
		if(dezoom)     { cameraPosition.addLocal(camDir.negate()); }

		// si rotation a gauche
		if(rotateLeft){ 
			brick.setLocalRotation(new Quaternion().fromAngles(0, 
					brick.rotateLeft(), 0));
		}
		// si rotation a droite
		if(rotateRight){
			brick.setLocalRotation(new Quaternion().fromAngles(0,
					brick.rotateRight(), 0));
		}
		// si rotation en haut
	/*	if(rotateUp){ 
			brick.setLocalRotation(new Quaternion().fromAngles(0, 
					0, brick.rotateUp()));
		}
		// si rotation en bas 
		if(rotateDown){
			brick.setLocalRotation(new Quaternion().fromAngles(0,
					0, brick.rotateDown()));
		}*/
		camera.setWalkDirection(cameraPosition);
		cam.setLocation(camera.getPhysicsLocation());
	}

	@Override
	public void simpleRender(RenderManager rm) { }

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

		inputManager.addMapping("drop", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addListener(actionListener, "drop");

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
	}   

	/**
	 * Controleur clic souris(creation et selection brique).
	 */
	private ActionListener actionListener = new ActionListener() {
		public void onAction(String name, boolean keyPressed, float tpf) {
			// si creation d'une nouvelle brique
			if(name.equals("create") && !keyPressed ) {
				//brickComptor ++;
				CollisionResults results = new CollisionResults();
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

				brick = new Brick(new BrickProperties(brickComptor++,
						new Vector3f(results.getClosestCollision()
								.getContactPoint()), null), bulletAppState,
								assetManager).makeBrick();

				if (results.size() > 0){
					brick.getBrickProperties().setPosition(new Vector3f(
							results.getClosestCollision().getContactPoint()));
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

			//	System.out.println("Position du curseur de la liste de position avant ajout :" +brick.getCurrentPositionInListPosition());
				// On ajoute la position de la brique cr�er dans sa liste de positions
				//brick.getPositionsList().add(brick.getBrickProperties().getPosition());
				//brick.incrementCurrentPositionInListPosition();

				//brick.incrementCurrentPositionInListPosition();
				//System.out.println("Position du curseur de la liste de position  apres ajout :" +brick.getCurrentPositionInListPosition());
				// On incr�mente la position courante du curseur de la liste de position
				//	brick.incrementCurrentPositionInListPosition();

				//ac.setState(State.UNDO);
				//System.out.println(history.getCurrentPosition());

				System.out.println(history.getAllElement());
				//rootNode.attachChild(brick); 

				// Ajout de l'action dans l'historique
				//System.out.println("Position courante de l'historique avant  :" + history.getCurrentPosition());

				//history.add("Cr�ation");
				//System.out.println("Position courante de l'historique apr�s  :" + history.getCurrentPosition());

				//	brick.getPositionsList().add(brick.getBrickProperties().getPosition());
				//System.out.println("nbOfPossibleUndo = " + nbOfPossibleUndo);
				//System.out.println("Essai" + history.getCurrentPosition());
				/*currentPosition += 1;
				action.add("Cr�ation");*/
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
						if(!brick.isSelected()){
							brick.setSelectedColor();
						}else{
							brick.backTextureColor();
						}
					}
				}
			}

			// Si une brique est relach�e
		/*	if (name.equals("drop") && !keyPressed) {
				for (Brick brickTemp : brickList) {
					if (brickTemp.isSelected()) {
						System.out.println("Nouveau AcitonMove");
						newPositionBrick = brick.getBrickProperties().getPosition();
						am = new ActionMove(getEditor(), brick.getBrickProperties().getId(), brick, oldPositionBrick, newPositionBrick);
						am.act();
						am.canUndo();
						nbOfPossibleUndo += 1;
						history.add(am);
						System.out.println("list = " + am.getList());
					}
				}
			}*/
		}
	};   

	/**
	 * Controleur AnalogListener(clic souris)
	 */
	private AnalogListener analogListener = new AnalogListener() {    
		public void onAnalog(String name, float value, float tpf) { 

			CollisionResults results = new CollisionResults();
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
				/*	if (name.equals("drag")) {

					results.getCollision(0).getGeometry().getControl(RigidBodyControl.class).setMass(0f);
					Vector3f v = results.getCollision(0).getContactPoint();
					Vector3f camLeft = cam.getLeft().clone().multLocal(0.2f);
					System.out.println(camLeft);
					if(cam.getLeft().clone().multLocal(0.2f).x <=0){
						brick.setLocalTranslation(v.x ,  brick.getBrickProperties().getPosition().y, v.z-v.y);
						//brick.getBrick_phy().setPhysicsLocation(new Vector3f(v.x ,  brick.getBrickProperties().getPosition().y, v.z-v.y));
					}
					else{
						brick.setLocalTranslation(v.x ,  brick.getBrickProperties().getPosition().y, v.z+v.y);
						//brick.getBrick_phy().setPhysicsLocation(new Vector3f(v.x ,  brick.getBrickProperties().getPosition().y, v.z+v.y));
					}
					for (Brick bri : brickList){
						if (bri.getBrickProperties().getId() 
								== brick.getBrickProperties().getId()){
							bri.getBrickProperties().setPosition(
									brick.getLocalTranslation());
						}
					}

				}*/
				if (name.equals("drag")) {
					Vector3f v = results.getCollision(0).getContactPoint();
					results.getCollision(0).getGeometry().setLocalTranslation(
							v.x , v.y, brick.getLocalTranslation().z
							);

					brick = (Brick) results.getCollision(0).getGeometry();
					brick.getBrickPhysic().setPhysicsLocation(new Vector3f(v.x , v.y,
							brick.getLocalTranslation().z));

					// mise a jour du deplacement de la brick dans la liste
					for (Brick brickTemp : brickList){
						if (brickTemp.getBrickProperties().getId() 
								== brick.getBrickProperties().getId()){
							// 
							brickTemp.getBrickProperties().setPosition(
									brick.getLocalTranslation());
						}
					}
				}
			}
		}
	};

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
					// bouton charger
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_load", "Charger"
							,"Permet de charger une partie enregistree."));
					panel(builders.hspacer("10px"));
					//bouton sauvegarder
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_save", "Sauvegarder"
							,"Permet de sauvgarder la partie."));
					panel(builders.hspacer("10px"));
					// Bouton Undo
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_undo", "Defaire"
							,"Permet de defaire une action."));
					panel(builders.hspacer("10px"));
					// Bouton Redo
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_redo", "Refaire"
							,"Permet de refaire une action."));
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
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_transparent", "Trans"
							,"Permet de rendre un kapla transparent.","40px"));
					panel(builders.vspacer("10px"));
				}});
			}});
		}}.build(nifty));
		// </screen>
		nifty.gotoScreen("Screen"); // demarre l'ecran

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
		// Controleur pour le button supprimer
		Element buttonDelete = nifty.getCurrentScreen()
				.findElementByName("Button_delete");
		buttonDelete.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "delete()", this));
		// Controleur pour le button transarent
		Element ButtonTransparent = nifty.getCurrentScreen()
				.findElementByName("Button_transparent");
		ButtonTransparent.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "transparency()", this));

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
	}

	/**
	 * Permet de supprimer toutes les kaplas selectionn�s.
	 */
	public void delete(){
		Iterator<Brick> iter = brickList.iterator();
		// parcours des briques selectionnees
		while(iter.hasNext()){
			Brick brickTemp = (Brick)iter.next();
			if(brickTemp.isSelected()){
				ad = new ActionDelete(getEditor(), brick.getBrickProperties().getId(), brick);
				ad.act();
				ad.canUndo();
				//rootNode.detachChild(brickTemp);
				//iter.remove();
			}
		}
		//Ajout de l'action dans l'historique
		//history.add("Suppression");
		// ajout de la brique au rootNode

		nbOfPossibleUndo += 1;
		history.add(ad);
		System.out.println(history.getAllElement());

		//action.add("Supression");
	}

	/**
	 * Permet de rendre transparent tous les kapla
	 * 
	 */
	public void transparency() {
		System.out.println("Transparence");
		//transparent = true;
		for(Brick brick : brickList){
			brick.setTranparency();
		}
	
		
		
	}

	/**
	 * Permet de d�faire une action
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
