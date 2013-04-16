package view;

import com.jme3.app.SimpleApplication;
import com.jme3.app.state.ScreenshotAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
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
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.geomitries.*;
import model.nifty.CommonBuilders;
import model.nifty.ControlStyles;

/**
 * Editeur en mode exctraction de données.
 * @author Groupe C M1GIL 2013
 */
@SuppressWarnings("deprecation")
public class EditorExract extends SimpleApplication 
implements ActionListener, ScreenController {

	private static CommonBuilders builders = new CommonBuilders();
	private ControlStyles styles;  
	/**
	 * Liste des proprietes des briques.
	 */
	private ArrayList<BrickProperties> brickListProperties;

	private ScreenshotAppState screenShotState;
	private NiftyJmeDisplay niftyDisplay;
	private Nifty nifty;

	/**
	 * Mise en place du physique.
	 */
	private BulletAppState bulletAppState;
	/**
	 * Table de la construction.
	 */
	private Table table;
	/**
	 * Le sol sur lequel est posee la table.
	 */
	private Floor floor;
	
	private CharacterControl camera;
	private Vector3f cameraPosition = new Vector3f();
	/**
	 * Activation de la rotation de la camera.
	 */
	private boolean left = false, right = false, up = false, down = false;
	/**
	 * Activation du zoom de la camera.
	 */
	private boolean zoom = false, dezoom = false;
	
	/**
	 * Initialisation des variables.
	 */
	@Override
	public void simpleInitApp() {
		screenShotState = new ScreenshotAppState();
		this.stateManager.attach(screenShotState);
		// mise en place du physique
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		
		// creation du sol et de la table.
		createFloor();
		createTable();
		
		initListeners();
		initNifty();
		
		mouseInput.setCursorVisible(true);
		flyCam.setEnabled(false); 
		
		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(
				1f, 6f, 1);
		camera = new CharacterControl(capsuleShape, 0.00f);
		camera.setGravity(0);
		camera.setPhysicsLocation(new Vector3f(1,10, 20));
		bulletAppState.getPhysicsSpace().add(camera);
	}

	@Override
	public void simpleUpdate(float tpf) {
		Vector3f camDir = cam.getDirection().clone().multLocal(0.4f);
		Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
		Vector3f camUp = cam.getUp().clone().mult(0.4f);
		cameraPosition.set(0, 0, 0);
		cam.lookAt( new Vector3f(1,1,1),new Vector3f(cameraPosition.x
				,cameraPosition.y, cameraPosition.z));
		if (left)  { cameraPosition.addLocal(camLeft); }
		if (right) { cameraPosition.addLocal(camLeft.negate()); }
		if (up)  { cameraPosition.addLocal(camUp); }
		if (down) { cameraPosition.addLocal(camUp.negate()); }
		if (zoom)    { cameraPosition.addLocal(camDir); }
		if (dezoom)  { cameraPosition.addLocal(camDir.negate()); }
		camera.setWalkDirection(cameraPosition);
		cam.setLocation(camera.getPhysicsLocation());
	}

	@Override
	public void simpleRender(RenderManager rm) {
		//TODO: add render code
	}
	
	/**
	 * Main
	 * @param args
	 */
	public static void main(String[] args) {
		EditorExract app = new EditorExract();
		
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
	 * Creation et ajout du sol.
	 */
	public void createFloor(){
		floor =  new Floor(bulletAppState,assetManager).makeFloor();
		rootNode.attachChild(floor);
	}
	
	/**
	 * Creation et ajout de la table.
	 */
	public void createTable(){
		table =  new Table(bulletAppState,assetManager).makeTable();
		for(int i = 0; i < 4; i++){
			TableLeg tableLeg = table.getTableLeg(i);
			rootNode.attachChild(tableLeg);
		}
		rootNode.attachChild(table);
	}
	
	/**
	 * Initialisation des ecouteurs.
	 */
	private void initListeners() {
		inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_LEFT));
		inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_RIGHT));
		inputManager.addMapping("Zoom", new KeyTrigger(KeyInput.KEY_Z));
		inputManager.addMapping("Dezoom", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_UP));
		inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_DOWN));
		inputManager.addListener(this, "Left");
		inputManager.addListener(this, "Right");
		inputManager.addListener(this, "Zoom");
		inputManager.addListener(this, "Dezoom");
		inputManager.addListener(this, "Up");
		inputManager.addListener(this, "Down");
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
		//register
		styles = new ControlStyles(nifty);
		styles.registerMenuButtonHintStyle();
		styles.registerStyles();
		styles.registerConsolePopup();
		// register some helper controls
		MenuButtonControlDefinition.register(nifty);
		// on definit le Screen
		nifty.addScreen("Screen", new ScreenBuilder("NavigateScreen"){{
			// proprietes de l'ecran
			controller(new DefaultScreenController());      
			// on definit le layer
			layer(new LayerBuilder("Layer") {{
				childLayoutVertical();
				// panel des boutons
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
					
					//bouton de capture d'ecran
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_shot", "Capture d'ecran"
							,"Permet de prendre une capture d'ecran."));
					panel(builders.hspacer("10px"));
					
					//bouton creer notice
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_notice", "Creer Notice"
							,"Permet de creer une notice sous format pdf."));
					panel(builders.hspacer("10px"));
				}});
			}});


		}}.build(nifty));
		// demarer l'ecran
		nifty.gotoScreen("Screen");
		
		// Controleur pour le bouton de capture d'ecran
		Element buttonShot = nifty.getCurrentScreen()
		.findElementByName("Button_shot");
		buttonShot.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "takeShot()", this));
		
		// Controleur pour le bouton de chargement
		Element buttonLoad = nifty.getCurrentScreen()
		.findElementByName("Button_load");
		buttonLoad.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "load()", this));
		
		// Controleur pour le bouton creer notice
		Element buttonNotice = nifty.getCurrentScreen()
		.findElementByName("Button_notice");
		buttonNotice.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "createNotice()", this));
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
		}
	}

	public void bind(Nifty nifty, Screen screen) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void onStartScreen() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	public void onEndScreen() {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	/**
	 * Prendre une capture d'ecran.
	 */
	public void takeShot() {
		screenShotState.takeScreenshot();
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
			brickListProperties = (ArrayList<BrickProperties>) ois.readObject();
			Brick brick;
			for (BrickProperties brickP : brickListProperties){
				brick = new Brick(brickP, bulletAppState, assetManager).makeBrick();
				rootNode.attachChild(brick);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Cree une notice sous format pdf
	 */
	public void createNotice(){
		
	}
}