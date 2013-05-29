package model.history;

import view.Editor;
import model.geomitries.Brick;

/**
 * Classe ActionCreate qui permet de creer un kapla
 * @author Groupe C M1 GIL.
 */
public class ActionCreate extends AbstractAction {

	// ATTRIBUTS
	private int idBrick;
	private Brick brick;

	// CONSTRUCTEUR
	/**
	 * Constructeur de la classe ActionCreate
	 * @param editor
	 * @param idBrick
	 * @param brick
	 */
	public ActionCreate(Editor editor, int idBrick, Brick brick) {
		super(editor);
		this.idBrick = idBrick;
		this.brick = brick;
	}
	
	// REQUETES
	
	/**
	 * L'id de la piece 
	 */
	public int getIdBrick() {
		return idBrick;
	}
	/**
	 * La piece
	 */
	public Brick getBrick() {
		return brick;
	}

	// COMMANDES
	
	/**
	 * Methode permettant de creer une piece
	 */
	@Override
	public void doIt() {
		getEditor().getRootNode().attachChild(getBrick());
	}

	/**
	 * Methode permettant de supprimer une piece
	 */
	@Override
	public void undoIt() {
		getEditor().getRootNode().detachChild(getBrick());
	}
}
