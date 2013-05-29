package model.history;

/**
 * Interface qui definit les méthodes pour un evenement (une action)
 * @author Groupe C M1GIL 2013
 *
 * @param <E>
 */

public interface Action<E> {
    /**
     * Indique que la commande et son environnement sont dans un etat
     *  permettant de faire la commande.
     */
    boolean canDo();
    /**
     * Indique que la commande et son environnement sont dans un etat
     *  permettant de defaire la commande.
     */
    boolean canUndo();

    // COMMANDES
    
    /**
     * Definit l'action qu'effectue la commande sur l'editeur associe.
     * @pre <pre>
     *     canDo() || canUndo() </pre>
     * @post <pre>
     *     getState() != old getState()
     *     old canDo()
     *         ==> la commande a fait son action sur l'editeur
     *     old canUndo()
     *         ==> la commande a defait son action sur l'editeur </pre>
     */
    void act();
}
