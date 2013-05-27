package model.history;

import java.util.List;

/**
 * Une interface sp�cifiant les fonctionnalit�s d'un outil de gestion d'historique.
 * On peut avancer et reculer le curseur rep�rant l'�l�ment courant �
 * loisir dans l'historique, mais si le curseur n'est pas sur l'�l�ment le 
 * plus r�cent, ajouter un �l�ment dans l'historique � cet instant fait 
 * dispara�tre les �l�ments post�rieurs au curseur.
 * 
 *  
 * @author CHARLEROY Andr�
 *
 */

public interface History{
	// REQUETES
	
	/**
	 * La position courante dans l'historique.
	 */
	int getCurrentPosition();
	/**
	 * L'�l�ment d�sign� par la position courante.
	 * @pre <pre>
	 *     getCurrentPosition() > 0 </pre>
	 */
	AbstractAction getCurrentElement();
	/**
	 * La derni�re position de l'historique.
	 */
	int getEndPosition();
	
	int getSize();
	
	List getAllElement();

	// COMMANDES

	/**
	 * Ajoute l'�l�ment <code>e</code> � la suite de l'�l�ment courant
	 *  et supprime les �l�ments post�rieurs � cet �l�ment courant.
	 * S'il n'y a pas d'�l�ment courant, ajoute simplement <code>e</code>
	 *  comme premier �l�ment.
	 * L'�l�ment <code>e</code> devient le nouvel �l�ment courant.
	 * @pre <pre>
	 *     e != null </pre>
	 * @post <pre>
	 *     getCurrentElement() == e
	 *     getCurrentPosition() == 
	 *         min(old getCurrentPosition() + 1, getMaxHeight())
	 *     getEndPosition() == getCurrentPosition()
	 *     si l'historique �tait plein, le plus ancien �l�ment a disparu </pre>
	 */
	void add(AbstractAction action);
	/**
	 * Avance le curseur dans la direction du plus r�cent �l�ment.
	 * @pre <pre>
	 *     getCurrentPosition() < getEndPosition() </pre>
	 * @post <pre>
	 *     getCurrentPosition() == old getCurrentPosition() + 1 </pre>
	 */
	void goForward();
	/**
	 * Recule le curseur dans la direction du plus ancien �l�ment.
	 * @pre <pre>
	 *     getCurrentPosition() > 0 </pre>
	 * @post <pre>
	 *     getCurrentPosition() == old getCurrentPosition() - 1 </pre>
	 */
	void goBackward();
	
}
