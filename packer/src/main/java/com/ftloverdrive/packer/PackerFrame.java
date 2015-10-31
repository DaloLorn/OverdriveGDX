package com.ftloverdrive.packer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by mateusz on 2015-10-31.
 */
public class PackerFrame extends JFrame {
    private JLabel dataDirectoryLabel;
    private JLabel outDirectoryLabel;
    private JLabel progressLabel;
    private JTextField dataDirectoryTextField;
    private JTextField outDirectoryTextField;
    private JButton selectDataButton;
    private JButton selectOutButton;
    private JButton packButton;
    private JProgressBar overallProgressBar;
    private JProgressBar specificProgressBar;
    
    public PackerFrame()
    {
        initUI();
    }

    private void initUI() {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
        }
        catch ( Exception e ) {
            e.printStackTrace();
        }

        // create and set compononents properties
        setupComponents();
        
        setupLayout();
        
        setTitle("Overdrive Packer");
        setSize(400, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void setupComponents() {
        dataDirectoryLabel = new JLabel("Choose data.dat in FLTs resource directory:");
        outDirectoryLabel = new JLabel("Choose output folder for packer:");
        progressLabel = new JLabel("Progress:");

        dataDirectoryTextField = new JTextField("");
        File dataFile = FTLUtilities.findDatsDir();
        if(dataFile != null && dataFile.exists())
        {
            dataDirectoryTextField.setText(dataFile.getAbsolutePath());
        }
        dataDirectoryTextField.setMaximumSize(new Dimension(600, 20));


        outDirectoryTextField = new JTextField("");
        File appDir = new File( "." );
        outDirectoryTextField.setText(appDir.getAbsolutePath().substring(0, appDir.getAbsolutePath().length() - 1));
        outDirectoryTextField.setMaximumSize(new Dimension(600, 20));

        selectDataButton = new JButton("Browse");
        selectDataButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File dataFile = FTLUtilities.promptForDatsDir(getContentPane());
                if(dataFile != null)
                {
                    dataDirectoryTextField.setText(dataFile.getPath());
                }
            }
        });

        selectOutButton = new JButton("Browse");
        selectOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                File outputDir = FTLUtilities.promptForOutputDir(getContentPane());
                if(outputDir != null)
                {
                    outDirectoryTextField.setText(outputDir.getPath());
                }
            }
        });

        packButton = new JButton("Package");
        packButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final File resourceDir = new File(dataDirectoryTextField.getText());
                final File outDir = new File(outDirectoryTextField.getText());

                if(!resourceDir.exists() || resourceDir.isFile()){
                    System.out.println(dataDirectoryTextField.getText() + " - " + resourceDir.getName() + " - " + resourceDir.getPath());
                    showErrorDialog("Resource directory is not valid.");
                    return;
                }

                if(!outDir.exists() || outDir.isFile()) {
                    showErrorDialog("Output directory is not valid.");
                    return;
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        unpack(resourceDir, outDir);
                    }
                }).start();
            }
        });

        overallProgressBar = new JProgressBar(0, 3);
        specificProgressBar = new JProgressBar(0, 100);
    }

    private void unpack(File resourceDir, File outDir) {
        ResourcePacker packer = new ResourcePacker(resourceDir, outDir);

        boolean tempBool = false;

        //setup
        try {
            tempBool = packer.setup(specificProgressBar);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } finally {
            if(!tempBool){
                return;
            }
        }

        //sort inner paths
        if(!packer.sortInnerPaths()){
            return;
        }
        overallProgressBar.setValue(1);

        //copy fonts
        try {
            if(!packer.copyFonts()){
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        overallProgressBar.setValue(2);

        try {
            if(!packer.copyTextures()){
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        overallProgressBar.setValue(3);
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog( getContentPane(),  message, "Invalid directory", JOptionPane.ERROR_MESSAGE );
    }

    private void setupLayout() {

        JPanel pane = (JPanel)getContentPane();

        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        pane.setToolTipText("Content pane");

        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(
                gl.createSequentialGroup()
                        .addGroup(
                                gl.createParallelGroup()
                                        .addComponent(dataDirectoryLabel)
                                        .addComponent(dataDirectoryTextField)
                                        .addComponent(outDirectoryLabel)
                                        .addComponent(outDirectoryTextField)
                                        .addComponent(packButton)
                                        .addComponent(progressLabel)
                                        .addComponent(overallProgressBar)
                                        .addComponent(specificProgressBar)
                        )
                        .addGroup(
                                gl.createParallelGroup()
                                        .addComponent(selectDataButton)
                                        .addComponent(selectOutButton)
                        )
        );

        gl.setVerticalGroup(
                gl.createSequentialGroup()
                        .addComponent(dataDirectoryLabel)
                        .addGap(10)
                        .addGroup(
                                gl.createParallelGroup()
                                        .addComponent(dataDirectoryTextField)
                                        .addComponent(selectDataButton)
                        )
                        .addComponent(outDirectoryLabel)
                        .addGap(10)
                        .addGroup(
                                gl.createParallelGroup()
                                        .addComponent(outDirectoryTextField)
                                        .addComponent(selectOutButton)
                        )
                        .addComponent(packButton)
                        .addGap(10)
                        .addComponent(progressLabel)
                        .addGap(10)
                        .addComponent(overallProgressBar)
                        .addGap(10)
                        .addComponent(specificProgressBar)
        );

        pane.add(new JLabel("test"));

        pack();
    }
}
