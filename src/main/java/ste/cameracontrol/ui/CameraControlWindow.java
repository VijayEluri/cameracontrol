/*
 * cameracontrol
 * Copyright (C) 2010 Stefano Fornari
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY Stefano Fornari, Stefano Fornari
 * DISCLAIMS THE WARRANTY OF NON INFRINGEMENT OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 */

package ste.cameracontrol.ui;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.SwingWorker;
import org.apache.commons.lang.StringUtils;

import ste.cameracontrol.CameraController;
import ste.cameracontrol.Photo;

/**
 *
 * @author ste
 */
public class CameraControlWindow extends BaseFrame {

    public static final String ICON_CAMERA_CONNECT    = "images/camera-connect-24x24.png";
    public static final String ICON_CAMERA_DISCONNECT = "images/camera-disconnect-24x24.png";
    /**
     * The camera controller
     */
    private CameraController controller;

    /** Creates new form CameraControlWindow */
    public CameraControlWindow() {
        initComponents();
        setLocationRelativeTo(null);
        setStatus("");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        statusPanel = new javax.swing.JPanel();
        connectionLabel = new javax.swing.JLabel();
        statusLabel = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        menuFile = new javax.swing.JMenu();
        menuEdit = new javax.swing.JMenu();
        menuCamera = new javax.swing.JMenu();
        shootMenuItem = new javax.swing.JMenuItem();
        menuHelp = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Camera connection status");
        setBackground(javax.swing.UIManager.getDefaults().getColor("window"));
        setIconImage(getImage(ICON_CAMERACONTROL));
        setMinimumSize(new java.awt.Dimension(500, 400));
        setName("connectionframe"); // NOI18N

        statusPanel.setLayout(new java.awt.BorderLayout());

        connectionLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/camera-connect-24x24.png"))); // NOI18N
        connectionLabel.setText("connection status");
        connectionLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        connectionLabel.setPreferredSize(null);
        statusPanel.add(connectionLabel, java.awt.BorderLayout.LINE_START);

        statusLabel.setText("status");
        statusLabel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        statusPanel.add(statusLabel, java.awt.BorderLayout.CENTER);

        getContentPane().add(statusPanel, java.awt.BorderLayout.PAGE_END);

        menuFile.setText("File");
        menuBar.add(menuFile);

        menuEdit.setText("Edit");
        menuBar.add(menuEdit);

        menuCamera.setText("Camera");

        shootMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.ALT_MASK));
        shootMenuItem.setText("Shoot");
        shootMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shootMenuItemActionPerformed(evt);
            }
        });
        menuCamera.add(shootMenuItem);

        menuBar.add(menuCamera);

        menuHelp.setText("Help");

        aboutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.ALT_MASK));
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        menuHelp.add(aboutMenuItem);

        jMenuItem1.setText("jMenuItem1");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        menuHelp.add(jMenuItem1);

        menuBar.add(menuHelp);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void shootMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shootMenuItemActionPerformed
        setStatus("Taking picture");
        try {
            new SwingWorker<Void, Object>() {
                @Override
                public Void doInBackground() throws Exception {
                    setStatus("Wait...");
                    Photo photos[] = controller.shootAndDownload();
                    
                    for (Photo photo: photos) {
                        //
                        // TODO: remove this limitation; the current version of
                        // jrawio does not work
                        //
                        if (!photo.getName().toLowerCase().endsWith("cr2")) {
                            new ImageFrame(photo).setVisible(true);
                        }
                    }
                    return null;
                }

                @Override
                protected void done() {
                    setStatus("");
                }
            }.execute();
        } catch (Exception e) {
            error("Error capturing the picture: " + e.getMessage(), e);
        }
    }//GEN-LAST:event_shootMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        new AboutDialog(this, true).setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        try {
            BufferedImage img = ImageIO.read(ClassLoader.getSystemResourceAsStream("images/about.png"));
            for (String s: img.getPropertyNames()) {
                System.out.println(s);
            }
            new ImageFrame(img).setVisible(true);
        } catch (IOException e) {
            error(e.getMessage(), e);;
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CameraControlWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JLabel connectionLabel;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu menuCamera;
    private javax.swing.JMenu menuEdit;
    private javax.swing.JMenu menuFile;
    private javax.swing.JMenu menuHelp;
    private javax.swing.JMenuItem shootMenuItem;
    private javax.swing.JLabel statusLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables


    // ---------------------------------------------------------- Public methods

    public void setConnectionStatus(String status) {
        if (status != null) {
            connectionLabel.setIcon(getIcon(ICON_CAMERA_CONNECT));
            connectionLabel.setText(status);
        } else {
            connectionLabel.setIcon(getIcon(ICON_CAMERA_DISCONNECT));
            connectionLabel.setText("Not connected");
        }
    }

    /**
     * Displays a message in the status bar.
     *
     * @param status the status message
     *
     */
    public void setStatus(final String status) {
        if (status == null) {
            statusLabel.setText("");
        }
        
        statusLabel.setText(StringUtils.abbreviateMiddle(status, "...", 50));
    }
    
    /**
     * @return the controller
     */
    public CameraController getController() {
        return controller;
    }

    /**
     * @param controller the controller to set
     */
    public void setController(CameraController controller) {
        this.controller = controller;
    }
}
