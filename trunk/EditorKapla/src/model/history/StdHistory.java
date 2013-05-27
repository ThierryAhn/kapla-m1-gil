package model.history;

import java.util.ArrayList;
import java.util.List;

public class StdHistory implements History {

	// ATTRIBUTS
	private int currentPosition;
	private int endPosition;
	//private ArrayList<String> historyList;
	private ArrayList<AbstractAction> historyList;

	// CONSTRUCTEURS
	public StdHistory() {
		this.currentPosition = 0;
		this.endPosition = 0;
		//this.historyList = new ArrayList<String>();
		this.historyList = new ArrayList<AbstractAction>();
	}

	// REQUETES
	@Override
	public int getCurrentPosition() {
		return currentPosition;
	}

	@Override
	public AbstractAction getCurrentElement() {
		if (getCurrentPosition() < 0) {
			throw new IllegalArgumentException();
		}
		return historyList.get(currentPosition);
	}

	@Override
	public int getEndPosition() {
		return endPosition;
	}

	public int getSize() {
		return historyList.size();
	}

	public List getAllElement() {
		return historyList;

	}

	//COMMANDES
	@Override
	public void add(AbstractAction action) {
		if (action == null) {
			throw new IllegalArgumentException("L'action ne peut �tre nulle");
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

	@Override
	public void goForward() {
		if (getCurrentPosition() > getEndPosition()) {
			throw new IllegalStateException();
		}
		currentPosition = currentPosition + 1;
	}

	@Override
	public void goBackward() {
		if (getCurrentPosition() < 0) {
			
		}
		currentPosition = currentPosition - 1;
		System.out.println("currentPosition = " +currentPosition); 
	}
}
