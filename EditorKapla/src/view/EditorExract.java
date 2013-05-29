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
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.controls.textfield.builder.TextFieldBuilder;
import de.lessvoid.nifty.controls.textfield.filter.input.FilterAcceptDigits;
import de.lessvoid.nifty.controls.textfield.filter.input.FilterAcceptRegex;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.examples.defaultcontrols.common.MenuButtonControlDefinition;
import de.lessvoid.nifty.screen.DefaultScreenController;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;
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
	/**
	 * Login de l'utilisateur
	 */
	private static String user;


	private static CommonBuilders builders = new CommonBuilders();
	private ControlStyles styles;
	private TextField mainTextField;
	private Label mainLabel;
	/**
	 * Liste des proprietes des briques.
	 */
	private ArrayList<BrickProperties> brickListProperties;
	private ArrayList<Brick> brickList;


	private ScreenshotAppState screenShotState;
	private NiftyJmeDisplay niftyDisplay;
	private Nifty nifty;

	private int etape=0;
	/**
	 * Mise en place du physique.
	 */
	private BulletAppState bulletAppState;

	/**
	 * Camera.
	 */
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


	public ArrayList<Brick> getBrickList() {
		return brickList;
	}

	public void setBrickList(ArrayList<Brick> brickList) {
		this.brickList = brickList;
	}

	/**
	 * Initialisation des variables.
	 */
	@Override
	public void simpleInitApp() {
		screenShotState = new ScreenshotAppState();
		this.stateManager.attach(screenShotState);
		brickList = new ArrayList<Brick>();
		// mise en place du physique
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);

		// creation du sol et de la table.
		createRoom();
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

		this.mainTextField = nifty.getScreen("Screen").findNiftyControl("mainTextField", TextField.class);
		this.mainLabel =  nifty.getScreen("Screen").findNiftyControl("mainLabel", Label.class);
		
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
		
		// recuperation du login de user
		user = args[0];
		
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
		Table table =  new Table(bulletAppState,assetManager).makeTable();
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
					panel(builders.hspacer("100px"));

					// button debut
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_first", "<|"
							,"Passer à la première étape.","20px"));
					panel(builders.hspacer("5px"));

					// button precedent
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_previous", "<<"
							,"Passer à l'étape précédente.","20px"));
					panel(builders.hspacer("5px"));


					// textfield numero etape
					control(new TextFieldBuilder("mainTextField") {{
						width("100px");
						alignLeft();
						valignCenter();
						textHAlignLeft();
					}});
					panel(builders.hspacer("5px"));

					// label numero etape
					control(builders.createLabel("mainLabel",etape + "/"+brickList.size(),"50px"));
					panel(builders.hspacer("5px"));

					// button precedent
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_go", "go"
							,"Passer à l'étape choisie","20px"));
					panel(builders.hspacer("5px"));


					// button precedent
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_next", ">>"
							,"Passer à l'étape suivante.","20px"));
					panel(builders.hspacer("5px"));

					// button debut
					control(MenuButtonControlDefinition.getControlBuilder(
							"Button_end", "|>"
							,"Passer à la dernière étape.","20px"));
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
		
		// Controleur pour le bouton debut
		Element buttonFirst = nifty.getCurrentScreen()
				.findElementByName("Button_first");
		buttonFirst.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "first()", this));
		// Controleur pour le bouton precedent
		Element buttonPrevious = nifty.getCurrentScreen()
				.findElementByName("Button_previous");
		buttonPrevious.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "previous()", this));
		// Controleur pour le bouton suivant
		Element buttonNext = nifty.getCurrentScreen()
				.findElementByName("Button_next");
		buttonNext.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "next()", this));
		// Controleur pour le bouton fin
		Element buttonEnd = nifty.getCurrentScreen()
				.findElementByName("Button_end");
		buttonEnd.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "end()", this));
		// controleur pour le textfield
		Element go = nifty.getCurrentScreen()
				.findElementByName("Button_go");
		go.getElementInteraction().getPrimary().setOnClickMethod(
				new NiftyMethodInvoker(nifty, "getEtape()", this));
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

	public void bind(Nifty nifty, Screen screen) { }

	public void onStartScreen() { }

	public void onEndScreen() { }

	/**
	 * Prendre une capture d'ecran.
	 */
	public void takeShot() {
		//screenShotState.setFilePath("C:/wamp/www/Kapla_site/users/"+user +"/images/");
		screenShotState.takeScreenshot();
	}

	/**
	 * Charge une construction.
	 */
	@SuppressWarnings("unchecked")
	public void load (){
		try{
			detachBricks();
			brickList.clear();
			FileInputStream fichier = new FileInputStream(
					//System.getProperty("user.home")+"/ListBrick.ser");
					"C:/wamp/www/Kapla_site/users/"+user +"/constructions/ListBrick.ser");
			ObjectInputStream ois = new ObjectInputStream(fichier);
			brickListProperties = (ArrayList<BrickProperties>) ois.readObject();
			Brick brick;
			for (BrickProperties brickP : brickListProperties){
				brick = new Brick(brickP, bulletAppState, assetManager).makeBrick();
				brickList.add(brick);
			}
			for (Brick brickP : brickList){
				rootNode.attachChild(brickP);
			}
			etape = brickList.size();
			mainLabel.setText(etape+"/"+brickList.size());
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void first(){
		if(brickList.size()!= 0){
			detachBricks();
			rootNode.attachChild(brickList.get(0));
			etape = 1;
			mainLabel.setText(etape+"/"+brickList.size());
		}
	}

	public void previous(){
		if (etape > 1){
			rootNode.detachChild(brickList.get(etape-- -1));
			mainLabel.setText(etape+"/"+brickList.size());
		}
	}

	public void next(){
		if (etape < brickList.size()){
			rootNode.attachChild(brickList.get(etape++));
			mainLabel.setText(etape+"/"+brickList.size());
		}
	}

	public void end(){
		detachBricks();
		for (Brick brickP : brickList){
			rootNode.attachChild(brickP);
		}
		etape = brickList.size();
		mainLabel.setText(etape+"/"+brickList.size());

	}

	public void detachBricks(){
		Iterator<Brick> iter = brickList.iterator();
		while(iter.hasNext()){
			Brick brickTemp = (Brick)iter.next();	
			rootNode.detachChild(brickTemp);
		}
	}

	public void getEtape(){
		detachBricks();
		if (isNumeric(mainTextField.getText())){
			int etap = Integer.parseInt(mainTextField.getText());
			if (etap>0 && etap<=brickList.size()){
				for (int i=0;i<etap;i++){
					rootNode.attachChild(brickList.get(i));
				}
				etape = etap;
				mainLabel.setText(etape+"/"+brickList.size());
			}
		}
	}

	public static boolean isNumeric(String str) {  
		try  
		{  
			@SuppressWarnings("unused")
			Integer d = Integer.parseInt(str);  
		}  
		catch(NumberFormatException nfe)  
		{  
			return false;  
		}  
		return true;  
	}
}