package model.history;

public interface Action<E> {
    /**
     * Indique que la commande et son environnement sont dans un état
     *  permettant de faire la commande.
     */
    boolean canDo();
    /**
     * Indique que la commande et son environnement sont dans un état
     *  permettant de défaire la commande.
     */
    boolean canUndo();

    // COMMANDES
    
    /**
     * Définit l'action qu'effectue la commande sur l'éditeur associé.
     * @pre <pre>
     *     canDo() || canUndo() </pre>
     * @post <pre>
     *     getState() != old getState()
     *     old canDo()
     *         ==> la commande a fait son action sur l'éditeur
     *     old canUndo()
     *         ==> la commande a défait son action sur l'éditeur </pre>
     */
    void act();
}
