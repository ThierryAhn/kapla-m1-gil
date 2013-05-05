package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import com.itextpdf.text.DocumentException;
import model.notice.GeneratePdf;
import model.notice.ImageAction;
import model.notice.NoticeImage;

/**
 * Classe NoticeInterface qui represente l'interface de la notice.
 * @author Groupe C M1GIL 2013.
 *
 */
@SuppressWarnings("serial")
public class NoticeInterface extends JFrame{

	/**
	 * Chemin du dossier des images.
	 */
	private String imagesPath;
	/**
	 * Panel des images.
	 */
	private JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 
			PADDING, PADDING));

	//private JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));

	/**
	 * Liste contenant les images.
	 */
	private ArrayList<NoticeImage> arrayImages = new ArrayList<NoticeImage>();

	/**
	 * Padding entre les images.
	 */
	private static final int PADDING = 20;
	
	public NoticeInterface(){
		super("New Notice");
		setLayout(new BorderLayout());
		setSize(600, 600);
		
		centerPanel.setBackground(Color.DARK_GRAY);
		
		// centrer la fenetre
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
		setLocation(x, y);

		// panel de choix du dossier des images
		JPanel northPanel = new JPanel(new BorderLayout());

		// champ de texte qui contient le chemin du repertoire des images
		final JTextField folderPath = new JTextField();
		folderPath.setEditable(false);

		// bouton de choix du dossier des images
		JButton chooseFolder = new JButton("Choose Folder");

		// design bouton
		chooseFolder.setBackground(new Color(128, 15, 1));
		chooseFolder.setForeground(Color.BLACK);
		//chooseFolder.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

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

				// mise a jour du panel des images
				centerPanel.removeAll();
				for(NoticeImage noticeImage : arrayImages){
					// ajout de l'image
					ImageAction imageAction = new ImageAction(
							NoticeInterface.this, noticeImage);
					
					
					// action checkbox
					JPanel checkPanel = new JPanel(new BorderLayout());
					checkPanel.setBackground(Color.DARK_GRAY);
					checkPanel.add(imageAction.getCheck(), 
							BorderLayout.NORTH);
					checkPanel.add(new JLabel());
					
					JPanel tempPanel = new JPanel(new BorderLayout());
					//tempPanel.setBackground(Color.DARK_GRAY);
					tempPanel.add(imageAction);
					tempPanel.add(checkPanel, BorderLayout.EAST);
					centerPanel.add(tempPanel);
				}
				centerPanel.repaint();
				centerPanel.validate();
				add(centerPanel);
			}

		});

		JPanel southPanel = new JPanel(new BorderLayout());
		JButton generatePdf = new JButton("Generer Pdf");
		// design bouton
		generatePdf.setBackground(new Color(128, 15, 1));
		generatePdf.setForeground(Color.BLACK);
		// action bouton
		generatePdf.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				GeneratePdf pdf = new GeneratePdf(arrayImages);
				try {
					pdf.generate();
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (DocumentException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		southPanel.add(new JLabel());
		southPanel.add(generatePdf, BorderLayout.EAST);

		// ajout des composants du northPanel
		northPanel.add(folderPath);
		northPanel.add(chooseFolder, BorderLayout.EAST);

		// ajout des panels
		add(northPanel, BorderLayout.NORTH);
		add(centerPanel);
		add(southPanel, BorderLayout.SOUTH);

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
				}
			}
		}
	}	

}




