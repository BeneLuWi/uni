package de.unitrier.st.treemap;

import javax.swing.*;
import java.awt.*;
import java.io.File;

class TreeMap {
    private File rootDirectory;
    private DirectoryNode rootNode;

    private final JFrame frame;
    private final TreeMapPanel treeMapPanel;
    private JTextField directoryTextField;
    private JComboBox<FileNode.ScalingApproaches> scalingApproachComboBox;
    private JLabel filePathLabel;
    private JLabel fileNameLabel;
    private JLabel fileSizeLabel;

    private TreeMap() {
        frame = new JFrame();
        frame.setSize(800, 600);
        frame.setFont(new Font("Dialog", Font.PLAIN, 12));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("TreeMap");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());
        treeMapPanel = initializeTreeMapPanel();
        contentPane.add(treeMapPanel, BorderLayout.CENTER);
        contentPane.add(initializeStatusPanel(), BorderLayout.SOUTH);
        contentPane.add(initializeSettingsPanel(), BorderLayout.NORTH);
    }

    static void open() {
        SwingUtilities.invokeLater(() -> {
            TreeMap treeMap = new TreeMap();
            treeMap.frame.setVisible(true);
        });
    }

    private TreeMapPanel initializeTreeMapPanel() {
        return new TreeMapPanel();
    }

    private JPanel initializeSettingsPanel() {
        JPanel settingsPanel = new JPanel();
        settingsPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0,
                java.awt.Color.gray)
        );
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        flowLayout.setVgap(2);
        flowLayout.setHgap(5);
        settingsPanel.setLayout(flowLayout);

        JLabel directoryLabel = new JLabel();
        directoryLabel.setText("Directory:");

        directoryTextField = new JTextField();
        directoryTextField.setPreferredSize(new java.awt.Dimension(300, 20));
        directoryTextField.setText("");
        directoryTextField.setEditable(false);

        JButton directoryButton = new JButton();
        directoryButton.setText("Select");
        directoryButton.setPreferredSize(new java.awt.Dimension(70, 19));
        directoryButton.addActionListener(e -> selectDirectory());

        JLabel scalingApproachLabel = new JLabel();
        scalingApproachLabel.setText("Scaling:");

        scalingApproachComboBox = new JComboBox<>();
        scalingApproachComboBox.setPreferredSize(new java.awt.Dimension(100, 19));
        scalingApproachComboBox.setModel(new DefaultComboBoxModel<>(FileNode.ScalingApproaches.values()));
        scalingApproachComboBox.setSelectedIndex(0);
        scalingApproachComboBox.addItemListener(e -> {
            updateDirectoryTree();
            treeMapPanel.repaint();
        });

        settingsPanel.add(directoryLabel);
        settingsPanel.add(directoryTextField);
        settingsPanel.add(directoryButton);
        settingsPanel.add(scalingApproachLabel);
        settingsPanel.add(scalingApproachComboBox);

        return settingsPanel;
    }

    private JPanel initializeStatusPanel() {
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setPreferredSize(new java.awt.Dimension(130, 19));
        statusPanel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 0, 0, 0,
                java.awt.Color.gray)
        );

        JPanel fileInfoPanel = new JPanel();
        FlowLayout flowLayout = new FlowLayout();
        flowLayout.setAlignment(FlowLayout.LEFT);
        flowLayout.setVgap(2);
        flowLayout.setHgap(10);
        fileInfoPanel.setLayout(flowLayout);

        fileSizeLabel = new JLabel();
        fileSizeLabel.setText("");
        fileSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEADING);
        fileSizeLabel.setPreferredSize(new java.awt.Dimension(100, 15));

        fileNameLabel = new JLabel();
        fileNameLabel.setText("");

        filePathLabel = new JLabel();
        filePathLabel.setText("");
        filePathLabel.setFont(new Font("Dialog", Font.PLAIN, 12));

        statusPanel.add(fileSizeLabel, BorderLayout.EAST);
        fileInfoPanel.add(filePathLabel);
        fileInfoPanel.add(fileNameLabel);
        statusPanel.add(fileInfoPanel, BorderLayout.CENTER);

        return statusPanel;
    }

    private void selectDirectory() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fileChooser.showOpenDialog(frame);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            rootDirectory = fileChooser.getSelectedFile();
            directoryTextField.setText(rootDirectory.getAbsolutePath());
            updateDirectoryTree();
            treeMapPanel.repaint();
        }
    }

    private void updateDirectoryTree() {
        FileNode.selectedScalingApproach =
                FileNode.ScalingApproaches.values()[scalingApproachComboBox.getSelectedIndex()];
        rootNode = DirectoryNode.retrieveDirectoryTree(rootDirectory);
    }

    private class TreeMapPanel extends JPanel {
        private FileNode highlightedNode;

        TreeMapPanel() {
            highlightedNode = null;

            setSize(300, 200);
            setLayout(new GridBagLayout()); // https://docs.oracle.com/javase/tutorial/uiswing/layout/gridbag.html
            setBackground(Color.white);

            addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                public void mouseMoved(java.awt.event.MouseEvent e) {
                    if (rootNode == null) {
                        return;
                    }
                    FileNode selectedNode = rootNode.getSelectedNode((double)e.getX() / getWidth(),
                            (double)e.getY() / getHeight());
                    if (!selectedNode.equals(highlightedNode)) {
                        setHighlightedNode(selectedNode);
                    }
                }
            });
        }

        void setHighlightedNode(FileNode node) {
            // reset previously highlighted node
            if (highlightedNode != null) {
                highlightedNode.paint(getGraphics(), getWidth(), getHeight(), false);
            }
            // highlight new node
            if (node != null) {
                setToolTipText(node.getName());
                node.paint(getGraphics(), getWidth(), getHeight(), true);
                highlightedNode = node;
                filePathLabel.setText(highlightedNode.getPath());
                fileNameLabel.setText(highlightedNode.getName());
                fileSizeLabel.setText(highlightedNode.getSizeString());
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (rootNode != null) {
                rootNode.paint(g, getWidth(), getHeight());
            }
        }
    }
}
