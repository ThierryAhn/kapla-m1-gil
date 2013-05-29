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
	 * Methode permettant de supprimer une piece
	 */
	@Override
	public void doIt() {
		getEditor().getRootNode().detachChild(brick);
	}

	/**
	 * Methode permettant de creer une piece
	 */
	@Override
	public void undoIt() {
		getEditor().getRootNode().attachChild(brick);
	}
}
