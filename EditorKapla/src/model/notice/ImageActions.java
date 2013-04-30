package model.notice;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;


/**
 * Classe ImageActions qui permet d'effectuer differentes actions sur les images.
 * @author Groupe C M1GIL 2013.
 *
 */
@SuppressWarnings("serial")
public class ImageActions{
	
	/**
	 * Taille des images.
	 */
	private static int WIDTH = 350;
	private static int HEIGHT = 350;
	
	private JWindow window = new JWindow();
	
	/**
	 * Boutons comment et zoom
	 */
	private JButton comment;
	private JButton zoom;
	
	/**
	 * Checkbox qui permet d'inclure l'image dans la notice.
	 */
	private JCheckBox check;
	
	/**
	 * Constructeur.
	 * @param noticeImage image sur lequel il faut appliquer les actions.
	 * @param owner frame parent.
	 */
	public ImageActions(final JFrame owner, final NoticeImage noticeImage){
		
		// bouton pour commenter
		comment = new JButton("Comment");
		comment.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new CommentDefinition(owner, noticeImage);
			}
			
		});
		
		
		
		// bouton pour zoomer
		zoom = new JButton("Agrandir");
		zoom.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				try {
					// image redimensionne
					BufferedImage originalImage = ImageIO.read(new File(
							noticeImage.getPath()));
					
					BufferedImage resizeImage = resizeImage(
							originalImage, BufferedImage.TYPE_INT_ARGB);
					ImageIcon newImageIcon = new ImageIcon(resizeImage);

					JLabel label = new JLabel(newImageIcon);
					label.addMouseListener(new MouseListener(){
						@Override
						public void mouseClicked(MouseEvent arg0) {
							window.dispose();
						}

						@Override
						public void mouseEntered(MouseEvent arg0) {}

						@Override
						public void mouseExited(MouseEvent arg0) {
							window.dispose();
						}

						@Override
						public void mousePressed(MouseEvent arg0) {}

						@Override
						public void mouseReleased(MouseEvent arg0) {}
						
					});
					
					// window qui contient l'image
					window.setContentPane(label);
					window.pack();
					
					// centrer window
					Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
				    int x = (int) ((dimension.getWidth() - 
				    		window.getWidth()) / 2);
				    int y = (int) ((dimension.getHeight() - 
				    		window.getHeight()) / 2);
				    window.setLocation(x, y);
					
					
					window.setVisible(true);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		
		// checkbox check
		check = new JCheckBox();
		check.setSelected(true);
		check.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				noticeImage.setSelected(check.isSelected());
			}
		});
	}
	
	/**
	 * Retourne le bouton comment.
	 * @return le bouton comment.
	 */
	public JButton getComment() {
		return comment;
	}

	/**
	 * Retourne le bouton zoom.
	 * @return le bouton zoom.
	 */
	public JButton getZoom() {
		return zoom;
	}
	
	/**
	 * Retourne le checkbox check.
	 * @return le checkbox check.
	 */
	public JCheckBox getCheck(){
		return check;
	}
	
	/**
	 * Redimensionne une image. 
	 * @param originalImage l'image d'origine
	 * @param type type des variables WIDTH et HEIGHT
	 * @return une image redimensionnee.
	 */
	private static BufferedImage resizeImage(BufferedImage originalImage, 
			int type){
		
		BufferedImage resizedImage = new BufferedImage(WIDTH, HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, WIDTH, HEIGHT, null);
		g.dispose();

		return resizedImage;	
	}
}