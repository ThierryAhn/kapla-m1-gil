package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.itextpdf.text.DocumentException;
import model.notice.GeneratePdf;
import model.notice.ImageAction;
import model.notice.NoticeImage;
import model.notice.WrapLayout;

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
	private JPanel centerPanel;
	
	/**
	 * Liste contenant les images.
	 */
	private ArrayList<NoticeImage> arrayImages = new ArrayList<NoticeImage>();

	/**
	 * Padding entre les images.
	 */
	private static final int PADDING = 40;
	
	/**
	 * Barre de progression pour le chargement des images.
	 */
	private JProgressBar progressBar;
	
	
	/**
	 * Champ de texte qui contient le chemin du repertoire des images.
	 */
	private JTextField folderPath = new JTextField();
	
	/**
	 * ScrollPane pour les images.
	 */
	private JScrollPane scrollPane;
	
	/**
	 * Classe interne pour la gestion de la barre de progression.
	 *
	 */
	class MySwingWorker extends SwingWorker<Integer, String> {
		 
        public MySwingWorker() {
            /* On ajoute un écouteur de barre de progression. */
            addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    if("progress".equals(evt.getPropertyName())) {
                        progressBar.setValue((Integer) evt.getNewValue());
                    }
                }
            });
        }
 
        @Override
        public Integer doInBackground() {
            return loadImage(0, 100);
        }
        
        /**
         * Charge les images d'un dossier.
         * @param progressStart
         * @param progressEnd
         * @return
         */
        public int loadImage(double progressStart, double progressEnd){
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
    		
    		
    		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    		// mise a jour du panel des images
    		centerPanel.removeAll();
    		centerPanel.repaint();
    		
    		if(!arrayImages.isEmpty()){
    			
    			double step = (progressEnd - progressStart) / 
    					arrayImages.size();
    			
	    		for(NoticeImage noticeImage : arrayImages){
	    			progressStart += step;
	    			 
                    /* Transmet la nouvelle progression. */
                    setProgress((int) progressStart);
 
                    /* Ajout d'un temps d'attente pour observer les changements 
                       à l'échelle "humaine". */
                    try {
                        Thread.sleep(50);
                    } catch(InterruptedException e) {
                        e.printStackTrace();
                    }
	    			
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
    		}
    		
    		add(scrollPane);
    		
    		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    		return arrayImages.size();
    	}
 
        @Override
        protected void process(List<String> strings) {
        }
 
        @Override
        protected void done() {
            try {
                /* Le traitement est terminé. */
                setProgress(100);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
	
	/**
	 * Constructeur
	 */
	public NoticeInterface(){
		super("New Notice");
		setLayout(new BorderLayout());
		setSize(600, 600);
		
		centerPanel = new JPanel(new WrapLayout(WrapLayout.LEADING, PADDING, 
				PADDING));
		
		centerPanel.setBackground(Color.DARK_GRAY);
		
		// barre de progression
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		
		// centrer la fenetre
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
		int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);
		setLocation(x, y);

		// panel de choix du dossier des images
		JPanel northPanel = new JPanel(new BorderLayout());

		// champ de texte
		folderPath.setEditable(false);

		// bouton de choix du dossier des images
		JButton chooseFolder = new JButton("Choose Folder");

		// design bouton
		chooseFolder.setBackground(new Color(128, 15, 1));
		chooseFolder.setForeground(Color.BLACK);
		
		chooseFolder.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
		            	MySwingWorker swingWorker = new MySwingWorker();
		                swingWorker.execute();
		            }
		        });
		           
				
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
				dispose();
			}
			
		});
		southPanel.add(progressBar);
		southPanel.add(generatePdf, BorderLayout.EAST);

		// ajout des composants du northPanel
		northPanel.add(folderPath);
		northPanel.add(chooseFolder, BorderLayout.EAST);
		
		scrollPane = new JScrollPane(centerPanel);
		
		// ajout des panels
		add(northPanel, BorderLayout.NORTH);
		add(scrollPane);
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




