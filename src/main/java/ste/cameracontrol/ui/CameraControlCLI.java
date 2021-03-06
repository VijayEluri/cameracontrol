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

import java.io.IOException;
import java.net.InetAddress;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.codec.binary.Hex;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParameterException;
import picocli.CommandLine.Parameters;
import ste.cameracontrol.wifi.CameraController;
import ste.ptp.PTPException;

/**
 * This is a command line tool to access EOS PTP cameras.
 *
 * @author Stefano Fornari
 */
public class CameraControlCLI {

    static private CameraController camera = null;

    protected void launch(String... args) {
        CameraControlCLI.CameraControlOptions options = new CameraControlCLI.CameraControlOptions();

        CommandLine cli = new CommandLine(options);

        if (args.length == 0) {
            cli.usage(System.out);
            return;
        }

        try {
            cli.parse(args);
        } catch (ParameterException x) {
            System.out.println("\nInvalid arguments: " + x.getMessage() + "\n");
            cli.usage(System.out);
            return;
        }

        if (options.help) {
            cli.usage(System.out);
            return;
        }

        if (options.connect) {
            connect(options);
        } else if (options.check) {
            check(options);
        }
    }

    /**
     *
     */
    public static void main(String... args) throws Exception {
        new CameraControlCLI().launch(args);
    }

    // --------------------------------------------------------- private methods
    private void connect(CameraControlOptions options) {
        CameraController cc = new CameraController();

        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ServerSocket s = new ServerSocket(15740);
                    s.accept();
                    System.out.println("life on 15740!");
                } catch (IOException x) {
                    x.printStackTrace();
                }

            }
        }).start();
         */
        try {
            cc.connect(options.host.getHostName());

            System.out.println("Found " + cc.getCameraName() + " on " + options.host);
            System.out.println("GUID: " + Hex.encodeHexString(cc.getCameraGUID()));
            System.out.println("SW version: " + cc.getCameraSwVersion());

            cc.startRemoteSesssion();
        } catch (PTPException x) {
            x.printStackTrace();
        }
    }

    private void check(CameraControlOptions options) {

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.parse("http://" + options.host.getHostName() + ":49152/upnp/CameraDevDesc.xml");

            System.out.println("Camera: " + d.getElementsByTagName("modelName").item(0).getTextContent());
            System.out.println("Manufacturer: " + d.getElementsByTagName("manufacturer").item(0).getTextContent());
            System.out.println("Serial Number: " + d.getElementsByTagName("serialNumber").item(0).getTextContent());
            System.out.println("UDN: " + d.getElementsByTagName("UDN").item(0).getTextContent());

        } catch (IOException x) {
            System.out.println("No camera seems to be available at " + options.host.getHostName() + " (connection refused)");
        } catch (ParserConfigurationException | SAXException x) {
            System.out.println("No camera seems to be available at " + options.host.getHostName() + " (no or invalid descriptor)");
        }

    }

    /*
    private static void devinfo()
    throws PTPException {
        camera.devinfo();
    }

    private static void getEvents()
    throws PTPException {
        camera.dumpEvents();
    }


    private static void shoot()
    throws IOException, PTPException {
        Photo[] photos = camera.shootAndDownload();

        for (Photo photo: photos) {
            camera.savePhoto(photo);
        }
    }
     */
    // ---------------------------------------------------- command line options
    @Command(
            name = "ste.cameracontrol.ui.CameraControlCLI",
            description = "Remote controller for your Canon EOS camera."
    )
    protected static class CameraControlOptions {

        @Option(
                names = "--help, -h, help",
                description = "This help message",
                usageHelp = true
        )
        public boolean help;

        @Option(
                names = "connect",
                description = "Connect to the camera at the given address"
        )
        public boolean connect;

        @Option(
                names = "check",
                description = "Check if there is a camera at the given address"
        )
        public boolean check;

        @Parameters(
                paramLabel = "HOSTNAME",
                description = "the camera hostname or ip address",
                arity = "1"
        )
        InetAddress host;
    }
}
