package model.notice;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;


/**
 * Classe CommentDefinition qui represente la fenetre de commentaire.
 * @author Groupe C M1GIL 2013.
 *
 */
@SuppressWarnings("serial")
public class CommentDefinition extends JDialog{
	/**
	 * Zone de commentaire.
	 */
	private JTextArea comment = new JTextArea("Tapez votre commentaire ici");
	
	/**
	 * Constructeur.
	 */
	public CommentDefinition(JFrame owner, final ImageAction imageAction){
		
		super(owner, true);
		setLayout(new BorderLayout());
		setSize(500, 500);
		getContentPane().setBackground(Color.DARK_GRAY);
		// label qui contient l'image en plus grand
		JLabel label = null;
		
		// design zone de commentaire
		comment.setRows(5);
		comment.setBackground(Color.GRAY);
		comment.setForeground(Color.WHITE);
		
		
		final NoticeImage noticeImage = imageAction.getNoticeImage();
		
		// image redimensionne
		try {
			BufferedImage originalImage = ImageIO.read(new File(
					noticeImage.getPath()));
			
			BufferedImage resizeImage = resizeImage(
					originalImage, BufferedImage.TYPE_INT_ARGB, 350, 
					350);
			ImageIcon newImageIcon = new ImageIcon(resizeImage);

			label = new JLabel(newImageIcon);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// centrer
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (int) ((dimension.getWidth() - 
	    		this.getWidth()) / 2);
	    int y = (int) ((dimension.getHeight() - 
	    		this.getHeight()) / 2);
	    setLocation(x, y);
		
		
		JPanel buttonPanel = new JPanel(new BorderLayout());
		buttonPanel.setBackground(Color.DARK_GRAY);
		// bouton annuler
		JButton annuler = new JButton("Annuler");
		annuler.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
		// bouton de validation du commentaire
		JButton valider = new JButton("Valider");
		valider.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				noticeImage.setComment(comment.getText());
				dispose();
			}
		});
		
		// ajout des boutons au panel
		buttonPanel.add(annuler, BorderLayout.WEST);
		buttonPanel.add(new JLabel());
		buttonPanel.add(valider, BorderLayout.EAST);
		
		JPanel tempPanel = new JPanel(new BorderLayout());
		tempPanel.add(comment);
		tempPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		// ajout des composants.
		add(label);
		add(tempPanel, BorderLayout.SOUTH);
	}
	
	/**
	 * Redimensionne une image. 
	 * @param originalImage l'image d'origine
	 * @param type type des variables width et height
	 * @param width largeur de l'image
	 * @param height hauteur de l'image
	 * @return une image redimensionnee.
	 */
	private static BufferedImage resizeImage(BufferedImage originalImage, 
			int type, int width, int height){
		
		BufferedImage resizedImage = new BufferedImage(width, height, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();

		return resizedImage;	
	}
}
