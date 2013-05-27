package model.history;

import view.Editor;

public abstract class AbstractAction {

	// ATTRIBUTS
	private Editor editor;
	private State state;

	// CONSTRUCTEUR
	AbstractAction(Editor editor) {
		if (editor == null) {
			throw new IllegalArgumentException("Le'�diteur donn� est null.");
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
		state = state;
	}

	// COMMANDES

	public void act() {
		if (!canDo() && !canUndo()) {
			throw new IllegalStateException();
		}
		if (state == State.DO) {
			doIt();
			state = State.UNDO;
		} else { // n�cessairement state == State.UNDO
			undoIt();
			state = State.DO;
		}
	}
	/**
	 * Cette m�thode doit �tre red�finie dans les sous-classes, de sorte
	 *  qu'elle implante l'action � r�aliser pour ex�cuter la commande.
	 * Elle est appel�e par act() et ne doit pas �tre appel�e directement.
	 * @pre
	 *     canDo()
	 * @post
	 *     La commande a �t� ex�cut�e
	 */
	public abstract void doIt();
	/**
	 * Cette m�thode doit �tre red�finie dans les sous-classes, de sorte
	 *  qu'elle implante l'action � r�aliser pour annuler la commande.
	 * Si l'�tat du texte correspond � celui dans lequel il �tait apr�s doIt,
	 *  alors undoIt r�tablit le texte dans l'�tat o� il �tait avant
	 *  l'ex�cution de doIt.
	 * Elle est appel�e par act() et ne doit pas �tre appel�e directement.
	 * @pre
	 *     canUndo()
	 * @post
	 *     La commande a �t� annul�e
	 */
	public abstract void undoIt();

}
