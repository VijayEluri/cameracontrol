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

import java.awt.Image;
import ste.cameracontrol.Photo;

/**
 *
 * @author ste
 */
public class ImageFrame extends BaseFrame {

    private final String[] ZOOM_VALUES = {
        "10%", "25%", "50%", "75%", "100%",
        "150%", "200%", "300%", "500%", "1000%"
    };
    public final int MAX_ZOOM = 1000;

    private ImagePanel imagePanel;

    /** Creates new ImageFrame */
    public ImageFrame(Image image) {
        initCustomComponents();
        imagePanel.setImage(image);
        initComponents();
        setZoomValue(100);
    }

    /** Creates new ImageFrame */
    public ImageFrame(Photo photo) {
        initCustomComponents();
        imagePanel.setImage(photo.getImage());
        initComponents();
        setTitle(photo.getName());
        setZoomValue(100);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane(imagePanel);
        bottomPanel = new javax.swing.JPanel();
        zoomScrollbar = new javax.swing.JScrollBar();
        zoomValueBox = new javax.swing.JComboBox(ZOOM_VALUES);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(getImage(ICON_CAMERACONTROL));
        getContentPane().add(scrollPane, java.awt.BorderLayout.CENTER);

        bottomPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        zoomScrollbar.setMaximum(1000);
        zoomScrollbar.setMinimum(10);
        zoomScrollbar.setOrientation(javax.swing.JScrollBar.HORIZONTAL);
        zoomScrollbar.setUnitIncrement(10);
        zoomScrollbar.setValue(100);
        zoomScrollbar.setVisibleAmount(1);
        zoomScrollbar.setPreferredSize(new java.awt.Dimension(100, 17));
        zoomScrollbar.addAdjustmentListener(new java.awt.event.AdjustmentListener() {
            public void adjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {
                zoomScrollbarAdjustmentValueChanged(evt);
            }
        });
        bottomPanel.add(zoomScrollbar);

        zoomValueBox.setEditable(true);
        zoomValueBox.setMinimumSize(new java.awt.Dimension(80, 19));
        zoomValueBox.setPreferredSize(new java.awt.Dimension(75, 19));
        zoomValueBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomValueBoxActionPerformed(evt);
            }
        });
        bottomPanel.add(zoomValueBox);

        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Set the zoom value for the picture. The zoom value is an int representing
     * the zoom percentage (e.g. 10%, 100%).
     *
     * @param zoom the zoom percentage
     */
    private void setZoomValue(int zoom) {
        zoomValueBox.setSelectedItem(zoom + "%");
        imagePanel.setScale(zoom / 100.0);
        imagePanel.revalidate();
    }

    private void zoomValueBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomValueBoxActionPerformed
        String zoomValue = (String)zoomValueBox.getSelectedItem();
        int zoom = -1;
        
        if (zoomValue == null) {
            return;
        }

        if (zoomValue.endsWith("%")) {
            zoomValue = zoomValue.substring(0, zoomValue.length()-1);
        }

        try {
            zoom = Integer.parseInt(zoomValue);
            if ((zoom < 1) || (zoom > MAX_ZOOM)) {
                throw new IllegalArgumentException("zoom out of range [1," + MAX_ZOOM + "]");
            }
            setZoomValue(zoom);
        } catch (Exception e) {
            //
            // do nothing
            //
        }
}//GEN-LAST:event_zoomValueBoxActionPerformed

    private void zoomScrollbarAdjustmentValueChanged(java.awt.event.AdjustmentEvent evt) {//GEN-FIRST:event_zoomScrollbarAdjustmentValueChanged
        setZoomValue(evt.getValue());
    }//GEN-LAST:event_zoomScrollbarAdjustmentValueChanged

    private void initCustomComponents() {
        imagePanel = new ImagePanel();
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JScrollBar zoomScrollbar;
    private javax.swing.JComboBox zoomValueBox;
    // End of variables declaration//GEN-END:variables

}
