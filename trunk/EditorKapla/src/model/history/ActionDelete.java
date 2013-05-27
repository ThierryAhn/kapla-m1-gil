package model.history;

import model.geomitries.Brick;
import view.Editor;

/**
 * Classe ActionDelete qui permet de supprimer un kapla
 * @author Groupe C M1 GIL.
 */
public class ActionDelete extends AbstractAction {

	// ATTRIBUTS
	
	private int idBrick;
	private Brick brick;

	// CONSTRUCTEUR
	
	/**
	 * Constructeur de la classe ActionDelete
	 * @param editor
	 * @param idBrick
	 * @param brick
	 */
	public ActionDelete(Editor editor, int idBrick, Brick brick) {
		super(editor);
		this.idBrick = idBrick;
		this.brick = brick;
	}

	// REQU�TES	
	/**
	 * L'id de la pi�ce
	 */
	public int getIdBrick() {
		return idBrick;
	}
	/**
	 * La kapla
	 */
	public Brick getBrick() {
		return brick;
	}

	// COMMANDES
	
	/**
	 * M�thode permettant de supprimer un kapla
	 */
	@Override
	public void doIt() {
		getEditor().getRootNode().detachChild(brick);
	}

	/**
	 * M�thode permettant de cr�er un kapla
	 */
	@Override
	public void undoIt() {
		getEditor().getRootNode().attachChild(brick);
	}
}
