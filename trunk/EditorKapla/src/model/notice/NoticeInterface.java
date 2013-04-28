package model.notice;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Classe NoticeInterface qui represente l'interface de la notice.
 * @author Groupe C M1GIL 2013.
 *
 */
public class NoticeInterface extends JFrame{
	
	/**
	 * Chemin du dossier des images.
	 */
	private String imagesPath;
	/**
	 * Panel des images.
	 */
	private JPanel centerPanel;
	/**
	 * Nombre d'images.
	 */
	private int countImages = 0;
	/**
	 * Liste contenant les images.
	 */
	private ArrayList<NoticeImage> arrayImages = new ArrayList<NoticeImage>();
	
	/**
	 * Padding entre les images.
	 */
	private static final int PADDING = 3;
	/**
	 * Taille des images.
	 */
	private int SIZE = 5;
	
	public NoticeInterface(){
		super("New Notice");
		setLayout(new BorderLayout());
		
		
		// panel de choix du dossier des images
		JPanel northPanel = new JPanel(new BorderLayout());
		
		// champ de texte qui contient le chemin du repertoire des images
		final JTextField folderPath = new JTextField();
		folderPath.setEditable(false);
		
		// bouton de choix du dossier des images
		JButton chooseFolder = new JButton("Choose Folder");
		chooseFolder.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File("."));
				//chooser.setDialogTitle(choosertitle);
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
					folderPath.setText(chooser.getSelectedFile().toString());
					imagesPath = chooser.getSelectedFile().toString();
					
					// recuperation des images du dossier
					getFiles(imagesPath);
				}
				System.out.println((int) (countImages/5 + 1.0f));
				
				
				// mise a jour du panel des images
				//centerPanel = new JPanel(new GridLayout(7, 10));
				centerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 
						PADDING, PADDING));
				
				for(NoticeImage noticeImage : arrayImages){
					ImageIcon imageIcon = new ImageIcon(noticeImage.getPath());

					Image img = imageIcon.getImage();  
					BufferedImage bi = new BufferedImage(img.getWidth(null), 
							img.getHeight(null), BufferedImage.TYPE_INT_ARGB);  
					
					Graphics g = bi.createGraphics();  
					g.drawImage(img, 140, 199, SIZE, SIZE, null, null);  
					ImageIcon newImageIcon = new ImageIcon(bi);  
					
					
					
					JLabel tempLabel = new JLabel(newImageIcon);
					//tempLabel.setMaximumSize(maximumSize);
					//tempLabel.setSize(5, 5);
					
					centerPanel.add(tempLabel);
				}
				for(int i = countImages; i < 70; i++){
					JLabel tempLabel = new JLabel();
					//tempLabel.setMaximumSize(maximumSize);
					tempLabel.setSize(5, 5);
					centerPanel.add(tempLabel);
				}
				add(centerPanel);
			}
			
		});
				
		// ajout des composants du northPanel
		northPanel.add(folderPath);
		northPanel.add(chooseFolder, BorderLayout.EAST);
		
		
		
		// ajout des panels
		add(northPanel, BorderLayout.NORTH);
		
		pack();
		setVisible(true);
	}
	
	/**
	 * Liste les fichiers d'un dossier.
	 * @param folder dossier dont il faut lister les fichiers.
	 */
	private void getFiles(String folder) {
	    File file = new File(folder);
	    File[] files = file.listFiles();
	    
	    arrayImages.clear();
	    if (files != null) {
	        for (int i = 0; i < files.length; i++) {
	            if(files[i].isDirectory() == false) {
	            	NoticeImage noticeImage = new NoticeImage(imagesPath +"\\"
	            			+files[i].getName());
	            	
	            	arrayImages.add(noticeImage);
	            	countImages++;
	            }
	        }
	    }
	}	
}




