package model.history;

import java.util.ArrayList;
import java.util.List;

public class StdHistory implements History {

	// ATTRIBUTS
	private int currentPosition;
	private int endPosition;
	private ArrayList<AbstractAction> historyList;

	// CONSTRUCTEURS
	/**
	 * Constructeur de la classe StdHistory
	 */
	public StdHistory() {
		this.currentPosition = 0;
		this.endPosition = 0;
		this.historyList = new ArrayList<AbstractAction>();
	}

	// REQUETES
	/**
	 * La position courante dans l'historique
	 * return currentPosition
	 */
	@Override
	public int getCurrentPosition() {
		return currentPosition;
	}

	/**
	 * L'element courant dans l'historique
	 * return historyList.get(currentPosition)
	 */
	@Override
	public AbstractAction getCurrentElement() {
		if (getCurrentPosition() < 0) {
			throw new IllegalArgumentException();
		}
		return historyList.get(currentPosition);
	}

	/**
	 * La position de fin de l'historique
	 * return endPosition
	 */
	@Override
	public int getEndPosition() {
		return endPosition;
	}

	/**
	 * La taille de l'historique
	 * return historyList.size()
	 */
	public int getSize() {
		return historyList.size();
	}

	/**
	 * Tous les elements de l'historique
	 */
	public List<AbstractAction> getAllElement() {
		return historyList;

	}

	//COMMANDES
	/**
	 * Ajouter un element dans l'historique
	 */
	public void add(AbstractAction action) {
		if (action == null) {
			throw new IllegalArgumentException("L'action ne peut etre nulle");
		}
		if (getCurrentPosition() <= -1) {
			for (int i = 0; i< historyList.size(); i++) {
				historyList.removeAll(historyList);
			}
			this.currentPosition = 0;
			this.endPosition = 0;
			historyList.add(currentPosition, action);
			currentPosition = currentPosition + 1;
			
		} else {
			if ((getCurrentPosition()) < (historyList.size())) {
				for (int i = (getCurrentPosition());
						i < historyList.size(); i++) {
					historyList.remove(i);
				}
			}
				historyList.add(currentPosition, action);
				currentPosition = currentPosition + 1;
				endPosition = getCurrentPosition();
		}
	}

	/**
	 * Avancer le curseur
	 */
	@Override
	public void goForward() {
		if (getCurrentPosition() > getEndPosition()) {
			throw new IllegalStateException();
		}
		currentPosition = currentPosition + 1;
	}

	/**
	 * Reculer le curseur
	 */
	@Override
	public void goBackward() {
		if (getCurrentPosition() < 0) {
			
		}
		currentPosition = currentPosition - 1;
		System.out.println("currentPosition = " +currentPosition); 
	}
}
