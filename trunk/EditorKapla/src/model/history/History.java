package model.history;

import java.util.List;

/**
 * Une interface specifiant les fonctionnalites d'un outil de gestion d'historique.
 * On peut avancer et reculer le curseur reperant l'element courant a
 * loisir dans l'historique, mais si le curseur n'est pas sur l'element le 
 * plus recent, ajouter un element dans l'historique a cet instant fait 
 * disparaitre les elements posterieurs au curseur.
 * 
 *  
 * @author Groupe C M1GIL 2013
 *
 */

public interface History{
	// REQUETES
	
	/**
	 * La position courante dans l'historique.
	 */
	int getCurrentPosition();
	/**
	 * L'element designe par la position courante.
	 * @pre <pre>
	 *     getCurrentPosition() > 0 </pre>
	 */
	AbstractAction getCurrentElement();
	/**
	 * La derniere position de l'historique.
	 */
	int getEndPosition();
	/**
	 * La taille de l'historique
	 * @return size
	 */
	int getSize();
	
	/**
	 * Liste des elements de l'historique
	 * @return tous les elements
	 */
	List getAllElement();

	// COMMANDES

	/**
	 * Ajoute l'element <code>e</code> a la suite de l'element courant
	 *  et supprime les elements posterieurs a cet element courant.
	 * S'il n'y a pas d'element courant, ajoute simplement <code>e</code>
	 *  comme premier element.
	 * L'element <code>e</code> devient le nouvel element courant.
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
	 * Avance le curseur dans la direction du plus recent element.
	 * @pre <pre>
	 *     getCurrentPosition() < getEndPosition() </pre>
	 * @post <pre>
	 *     getCurrentPosition() == old getCurrentPosition() + 1 </pre>
	 */
	void goForward();
	/**
	 * Recule le curseur dans la direction du plus ancien element.
	 * @pre <pre>
	 *     getCurrentPosition() > 0 </pre>
	 * @post <pre>
	 *     getCurrentPosition() == old getCurrentPosition() - 1 </pre>
	 */
	void goBackward();
}
