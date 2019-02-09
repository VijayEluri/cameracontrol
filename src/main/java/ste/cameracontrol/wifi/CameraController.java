/*
 * cameracontrol
 * Copyright (C) 2018 Stefano Fornari
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
package ste.cameracontrol.wifi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.apache.commons.io.input.TeeInputStream;
import org.apache.commons.io.output.TeeOutputStream;
import ste.cameracontrol.CameraNotAvailableException;
import ste.ptp.PTPException;
import ste.ptp.ip.Constants;
import ste.ptp.ip.InitCommandAcknowledge;
import ste.ptp.ip.InitCommandRequest;
import ste.ptp.ip.InitError;
import ste.ptp.ip.InitEventAcknowledge;
import ste.ptp.ip.InitEventRequest;
import ste.ptp.ip.PTPIPContainer;
import ste.ptp.ip.PacketInputStream;
import ste.ptp.ip.PacketOutputStream;

/**
 * See http://www.gphoto.org/doc/ptpip.php for some information on PTPIP protocol
 */
public class CameraController {

    public static final int TIMEOUT_CONNECT = 2000;
    public static final int PORT = 15740;

    //public static final String CLIENT_NAME = "Camera Control";
    public static final String CLIENT_NAME = "MyName.name";
    // MD5(CLIENT_NAME);
    /*
    public static final byte[] CLIENT_ID = new byte[] {
        (byte)0x69, (byte)0x61, (byte)0x75, (byte)0x16,
        (byte)0x6c, (byte)0x22, (byte)0x82, (byte)0xe8,
        (byte)0x95, (byte)0xf3, (byte)0x60, (byte)0x30,
        (byte)0xbd, (byte)0x25, (byte)0x56, (byte)0x8f
    };
    */
    public static final byte[] CLIENT_ID = new byte[] {
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0xff, (byte)0xff, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00
    };
    public static final String CLIENT_VERSION = "1.0";

    private Socket socket;
    private InputStream socketInput;
    private OutputStream socketOutput;

    private byte[] cameraId;
    private int    sessionId;
    private String cameraName;
    private String cameraSwVersion;

    private int reqCounter = 0, resCounter = 0;

    public void connect(String host)
    throws CameraNotAvailableException, PTPException {
        final String[] errors = new String[] {
            "command request error", "event request error"
        };

        int step = 0;
        try {
            if (InetAddress.getByName(host).isReachable(TIMEOUT_CONNECT)) {
                socket = new Socket();
                socket.setSoTimeout(0);
                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true);
                socket.connect(new InetSocketAddress(host, PORT));

                socketInput = socket.getInputStream();
                socketOutput = socket.getOutputStream();

                request(
                    new PTPIPContainer(new InitCommandRequest(CLIENT_ID, CLIENT_NAME, CLIENT_VERSION))
                );

                PTPIPContainer response = response();
                if (response.payload.getType() == Constants.PacketType.INIT_COMMAND_ACK.type()) {
                    InitCommandAcknowledge payload = (InitCommandAcknowledge)response.payload;
                    cameraId = payload.guid;
                    cameraName = payload.hostname;
                    sessionId = payload.sessionId;
                    cameraSwVersion = payload.version;

                    ++step;
                    request(
                        new PTPIPContainer(new InitEventRequest(sessionId))
                    );

                    response = response();

                }

                return;
            }
        } catch (PTPException x) {
            x.printStackTrace();
            throw new PTPException(errors[step], x.getErrorCode());
        } catch (ConnectException x) {
            //
            // nothing to do, a CameranNotAvailableException will be thrown
            //
        } catch (IOException x) {
            x.printStackTrace();
        } finally {
            if (socket != null) {
                try {socket.close();} catch (IOException x) {};
            }
        }

        throw new CameraNotAvailableException();
    }

    public boolean isConnected() {
        return ((socket != null) && socket.isConnected());
    }

    public String getCameraName() {
        return cameraName;
    }

    public byte[] getCameraGUID() {
        return cameraId;
    }

    public String getCameraSwVersion() {
        return cameraSwVersion;
    }

    public int getSessionId() {
        return sessionId;
    }

    // --------------------------------------------------------- private methods

    private void request(PTPIPContainer packet) throws IOException {
        PacketOutputStream out = new PacketOutputStream(
            new TeeOutputStream(
                socketOutput,
                new FileOutputStream(new File("cc-req-" + (++reqCounter) + ".dump"))
            )
        );

        out.write(packet); out.flush();
    }

    private PTPIPContainer response() throws IOException, PTPException {
        PacketInputStream in = new PacketInputStream(
            new TeeInputStream(
                socketInput,
                new FileOutputStream(new File("cc-res-" + (++resCounter) + ".dump"))
            )
        );
        /*
        PacketInputStream in = new PacketInputStream(
            socket.getInputStream()
        );
        */


        int size = in.readLEInt();
        int type = in.readLEInt();

        System.out.println("size: " + size);
        System.out.println("type: " + type);

        //
        // TODO move in PacketInputStream
        //
        if (type == Constants.PacketType.INIT_COMMAND_ACK.type()) {
            InitCommandAcknowledge ack = in.readInitCommandAcknowledge();
            cameraId = ack.guid;

            return new PTPIPContainer(ack);
        } else if (type == Constants.PacketType.INIT_EVENT_ACK.type()) {
            return new PTPIPContainer(new InitEventAcknowledge());
        } else if (type == Constants.PacketType.INIT_COMMAND_FAIL.type()) {
            InitError err = in.readInitError();
            throw new PTPException(err.error);
        }

        throw new IOException("protocol error (type:  " + type + ")");
    }
}