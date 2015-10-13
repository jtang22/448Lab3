
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;

public class GeneContentDialog extends JPanel implements ActionListener{

    GeneContentModel model;
    JButton openGTFButton, openFASTAButton, runButton, clearButton; 
    JFileChooser gtfFC, fastaFC;
    JTextField gtfFileTextField, fastaFileTextField;
    File gtfFile, fastaFile;

    public GeneContentDialog() {
        super(new BorderLayout());
        model = new GeneContentModel();
        createGUI();
    }

    public static void main(String[] args) {
        runGUI();
    }

    private static void runGUI() {
        JFrame frame = new JFrame("Gene Content Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new GeneContentDialog());

        frame.pack();
        frame.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        //Handle open button action.
        if (e.getSource() == openGTFButton) {
            int returnVal = gtfFC.showOpenDialog(GeneContentDialog.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                gtfFile = gtfFC.getSelectedFile();
                gtfFileTextField.setText(gtfFile.getName());
            } 
        } else if (e.getSource() == openFASTAButton) {
            int returnVal = fastaFC.showOpenDialog(GeneContentDialog.this);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fastaFile = fastaFC.getSelectedFile();
                fastaFileTextField.setText(fastaFile.getName());
            } 
        } else if (e.getSource() == runButton) {
            String[] fastaName, gtfName;

            if(gtfFile == null || fastaFile == null) {
                JOptionPane.showMessageDialog(null, "Please insert file(s).");
                return;
            }

            fastaName = fastaFile.getName().split("\\.");
            gtfName = gtfFile.getName().split("\\.");
            if ( (fastaName[1].equals("txt") || fastaName[1].equals("fasta")) && gtfName[1].equals("txt") ) {
                int check = model.caclulateGeneContent(gtfFile, fastaFile);
                if(check == 1) {
                    JOptionPane.showMessageDialog(null, "Unable to open files.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "File is not .txt or .fasta");
            }

        } else if ( e.getSource() == clearButton) {
            model.reset();
            gtfFileTextField.setText("");
            fastaFileTextField.setText("");
            gtfFile = null;
            fastaFile = null;
        }
    }

    private void createGUI() {
        //file chooser for gtf File
        JPanel gtfPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel gtfLabel = new JLabel();
        gtfLabel.setText("Insert GTF File: ");
        gtfPanel.add(gtfLabel);

        gtfFC = new JFileChooser();
        gtfFC.setCurrentDirectory(new File("."));
        openGTFButton = new JButton("Open GTF File");
        openGTFButton.addActionListener(this);

        gtfFileTextField = new JTextField();
        gtfFileTextField.setEditable(false);
        gtfFileTextField.setPreferredSize(new Dimension(300, 24));

        JPanel gtfFilePanel = new JPanel();
        gtfFilePanel.add(gtfFileTextField);
        gtfFilePanel.add(openGTFButton);

        //file chooser for fasta File
        JPanel fastaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel fastaLabel = new JLabel();
        fastaLabel.setText("Insert FASTA File: ");
        fastaPanel.add(fastaLabel);

        fastaFC = new JFileChooser();
        fastaFC.setCurrentDirectory(new File("."));
        openFASTAButton = new JButton("Open FASTA File");
        openFASTAButton.addActionListener(this);

        fastaFileTextField = new JTextField();
        fastaFileTextField.setEditable(false);
        fastaFileTextField.setPreferredSize(new Dimension(300, 24));

        JPanel fastaFilePanel = new JPanel();
        fastaFilePanel.add(fastaFileTextField);
        fastaFilePanel.add(openFASTAButton);

        //action buttons
        JPanel actionButtons = new JPanel();
        runButton = new JButton("Run");
        runButton.addActionListener(this);
        clearButton = new JButton("Clear");
        clearButton.addActionListener(this);
        actionButtons.add(runButton);
        actionButtons.add(clearButton);

        setLayout(new GridLayout(5, 1));
        add(gtfPanel);
        add(gtfFilePanel);
        add(fastaPanel);
        add(fastaFilePanel);
        add(actionButtons);
    }
 }