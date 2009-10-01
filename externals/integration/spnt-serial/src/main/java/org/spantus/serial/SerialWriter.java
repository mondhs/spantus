package org.spantus.serial;

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import org.spantus.logger.Logger;

public class SerialWriter extends Writer implements SerialPortEventListener{

	CommPortIdentifier portId;
	CommPortIdentifier saveportId;
	InputStream inputStream;
	SerialPort serialPort;
	Logger log = Logger.getLogger(getClass());
	OutputStreamWriter outputStreamWriter;
	
	enum supportedOs{linux, windows}


	public SerialWriter() {
		this(getDefaultPort());
	}

	public SerialWriter(String portName) {
		boolean portFound = false;

		log.debug("Set default port to " + portName);

		Enumeration<CommPortIdentifier> portList = getPortIdentifiers();

		while (portList.hasMoreElements()) {
			portId = portList.nextElement();
			if (portId.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				log.debug("Available port: " + portId.getName());
				if (portId.getName().equals(portName)) {
					log.debug("Found port: " + portName);
					portFound = true;
					break;
				}
			}

		}
		if (!portFound) {
			log.fatal("port " + portName + " not found.");
			System.exit(1);
		}

		try {
			serialPort = (SerialPort) portId.open("SimpleReadApp", 2000);
			inputStream = serialPort.getInputStream();
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
			serialPort.setSerialPortParams(9600, SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			initWriteToPort();
		} catch (UnsupportedCommOperationException e) {
		} catch (PortInUseException e) {
		} catch (TooManyListenersException e) {
		} catch (IOException e) {
		}
	}

	public void initWriteToPort() {
		try {
			outputStreamWriter = new OutputStreamWriter(serialPort.getOutputStream());
		} catch (IOException e) {
			log.error("Error: " + e.getMessage());
		}

		try {
			serialPort.notifyOnOutputEmpty(true);
		} catch (Exception e) {
			log.fatal("Error setting event notification" + e.getMessage());
			System.exit(-1);
		}

	}
	
	@Override
	public void write(String string) throws IOException{
		super.write(string);
		log.debug("Send: " + string);
	}

	public static String getDefaultPort() {
		String defaultPort = null;
		String osname = System.getProperty("os.name", "").toLowerCase();
		switch (supportedOs.valueOf(osname)) {
		case linux:
			defaultPort = "/dev/ttyS0";
			break;
		case windows:
			defaultPort = "COM1";
			break;
		default:
			break;
		}
		return defaultPort;
	}

	@SuppressWarnings("unchecked")
	public Enumeration<CommPortIdentifier> getPortIdentifiers() {
		return CommPortIdentifier.getPortIdentifiers();
	}
	public void serialEvent(SerialPortEvent serialPortEvent) {
		log.debug(serialPortEvent.toString());
	}

	@Override
	public void close() throws IOException {
		outputStreamWriter.close();		
	}

	@Override
	public void flush() throws IOException {
		outputStreamWriter.flush();		
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		outputStreamWriter.write(cbuf, off, len);
	}

}
