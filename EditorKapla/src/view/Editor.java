package view;

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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.geomitries.*;
import model.nifty.CommonBuilders;
import model.nifty.ControlStyles;

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

		inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
		inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
		inputManager.addMapping("Zoom", new KeyTrigger(KeyInput.KEY_Z));
		inputManager.addMapping("Dezoom", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_UP));
		inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_DOWN));
		inputManager.addMapping("s", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("r", new KeyTrigger(KeyInput.KEY_R));
		inputManager.addMapping("t", new KeyTrigger(KeyInput.KEY_T));
		inputManager.addMapping("u", new KeyTrigger(KeyInput.KEY_U));
		inputManager.addListener(this, "Left");
		inputManager.addListener(this, "Right");
		inputManager.addListener(this, "Zoom");
		inputManager.addListener(this, "Dezoom");
		inputManager.addListener(this, "Up");
		inputManager.addListener(this, "Down");
		inputManager.addListener(this, "r");
		inputManager.addListener(this, "t");
		inputManager.addListener(this, "u");

	}   
	
	/**
	 * Controleur clic souris(creation et selection brique).
	 */
	private ActionListener actionListener = new ActionListener() {
		public void onAction(String name, boolean keyPressed, float tpf) {
			// si creation d'une nouvelle brique
			if(name.equals("create") && !keyPressed ) {
				brickComptor ++;
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
				// ajout de la brique au rootNode
				rootNode.attachChild(brick); 
				// ajout de la brique dans la liste
				brickList.add(brick);
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
		}else if (name.equals("r")) {
			rotateLeft = isPressed;
		}else if (name.equals("t")) {
			rotateRight = isPressed;
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
				}});
			}});
		}}.build(nifty));
		// </screen>
		nifty.gotoScreen("Screen"); // demarre l'ecran
		
		// Controleur pour le bouton de capture d'ecran
		Element buttonSave = nifty.getCurrentScreen()
				.findElementByName("Button_save");
		buttonSave.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "save()", this));
		// Controleur pour le bouton de capture d'ecran
		Element buttonLoad = nifty.getCurrentScreen()
				.findElementByName("Button_load");
		buttonLoad.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "load()", this));
		// Controleur pour le button supprimer
		Element buttonDelete = nifty.getCurrentScreen()
				.findElementByName("Button_delete");
		buttonDelete.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "delete()", this));

	}

	/**
	 * Sauvegarde de la construction. 
	 */
	public void save() {
		try {
			FileOutputStream fichier = new FileOutputStream(
					System.getProperty("user.home")+"/ListBrick.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			oos.writeObject(brickList);
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
	 * Permet de supprimer toutes les kaplas selectionnés.
	 */
	public void delete(){
		Iterator<Brick> iter = brickList.iterator();
		// parcours des briques selectionnees
		while(iter.hasNext()){
			Brick brickTemp = (Brick)iter.next();
			if(brickTemp.isSelected()){
				rootNode.detachChild(brickTemp);
				iter.remove();
			}
		}
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
}
