package model.history;

import view.Editor;

/**
 * Classe abstraite pour les evenements (actions)
 * @author Groupe C M1GIL 2013
 *
 */
public abstract class AbstractAction {

	// ATTRIBUTS
	private Editor editor;
	private State state;

	// CONSTRUCTEUR
	AbstractAction(Editor editor) {
		if (editor == null) {
			throw new IllegalArgumentException("L'editeur donne est null");
		}
		this.editor = editor;
		state = State.DO;
	}

	// REQUETES
	public boolean canDo() {
		return state == State.DO;
	}
	
	public boolean canUndo() {
		return state == State.UNDO;
	}
	
	public Editor getEditor() {
		return editor;
	}
	
	public State getState() {
		return state;
	}
	
	public void setState(State state) {
		this.state = state;
	}

	// COMMANDES
	public void act() {
		if (!canDo() && !canUndo()) {
			throw new IllegalStateException();
		}
		if (state == State.DO) {
			doIt();
			state = State.UNDO;
		} else { // necessairement state == State.UNDO
			undoIt();
			state = State.DO;
		}
	}
	/**
	 * Cette methode doit etre redefinie dans les sous-classes, de sorte
	 *  qu'elle implante l'action a realiser pour executer la commande.
	 * Elle est appelee par act() et ne doit pas etre appelee directement.
	 * @pre
	 *     canDo()
	 * @post
	 *     La commande a ete executee
	 */
	public abstract void doIt();
	/**
	 * Cette methode doit etre redefinie dans les sous-classes, de sorte
	 *  qu'elle implante l'action a realiser pour annuler la commande.
	 * Elle est appelee par act() et ne doit pas etre appelee directement.
	 * @pre
	 *     canUndo()
	 * @post
	 *     La commande a ete annulee
	 */
	public abstract void undoIt();

}
