/*
 * Camera Control
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


package ste.cameracontrol;

import java.util.ArrayList;

/**
 * This class is the centralized controlled of the camera. It controls all
 * aspects of the interaction with the camera, from connectivity to picture
 * download and other controlling functions.
 *
 * @author ste
 */
public class CameraController {

    private CameraConnection connection;

    private ArrayList<CameraListener> listeners;

    /**
     * Creates a new CameraController
     */
    public CameraController() {
        connection = new CameraConnection();
        listeners = new ArrayList<CameraListener>();
    }

    /**
     * Checks the connection with the camera.
     * 
     * @return true if the camera is connected, false otherwise
     */
    public boolean isConnected() {
        return connection.isConnected();
    }

    /**
     * Adds a CameraControllerListener to the list of listeners to be notified
     * of camera events.
     *
     * @param listener the listener to add - NOT NULL
     *
     * @trhows  IllegalArgumentException if listener is null
     *
     */
    public synchronized void addCameraListener(CameraListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }

        listeners.add(listener);
    }

    // --------------------------------------------------------- Private methods

    /**
     * Used to set the connection status. Setting the connection status is
     * considered a status change, therefore invokes the registered
     * CameraListeners (if any).
     *
     * TODO: return the connected UsbDevice
     *
     * @param status the status of the connection: true if the camera is
     *        connected false otherwise.
     */
    private synchronized void setConnected(final boolean status) {
        if (listeners == null) {
            return;
        }

        for(CameraListener listener: listeners) {
            if (status) {
                listener.cameraConnected(null);
            } else {
                listener.cameraDisconnected(null);
            }
        }
    }
}
