//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;

public class SerialCommChannel implements CommChannel, SerialPortEventListener {
    private SerialPort serialPort;
    private BlockingQueue<String> queue = new ArrayBlockingQueue(100);
    private StringBuffer currentMsg = new StringBuffer("");

    public SerialCommChannel(String port, int rate) throws Exception {
        this.serialPort = new SerialPort(port);
        this.serialPort.openPort();
        this.serialPort.setParams(rate, 8, 1, 0);
        this.serialPort.setFlowControlMode(3);
        this.serialPort.addEventListener(this);
    }

    public void sendMsg(String msg) {
        char[] array = (msg + "\n").toCharArray();
        byte[] bytes = new byte[array.length];

        for(int i = 0; i < array.length; ++i) {
            bytes[i] = (byte)array[i];
        }

        try {
            synchronized(this.serialPort) {
                this.serialPort.writeBytes(bytes);
            }
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    public String receiveMsg() throws InterruptedException {
        return (String)this.queue.take();
    }

    public boolean isMsgAvailable() {
        return !this.queue.isEmpty();
    }

    public void close() {
        try {
            if (this.serialPort != null) {
                this.serialPort.removeEventListener();
                this.serialPort.closePort();
            }
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    public void serialEvent(SerialPortEvent event) {
        if (event.isRXCHAR()) {
            try {
                String msg = this.serialPort.readString(event.getEventValue());
                msg = msg.replaceAll("\r", "");
                this.currentMsg.append(msg);
                boolean goAhead = true;

                while(goAhead) {
                    String msg2 = this.currentMsg.toString();
                    int index = msg2.indexOf("\n");
                    if (index >= 0) {
                        this.queue.put(msg2.substring(0, index));
                        this.currentMsg = new StringBuffer("");
                        if (index + 1 < msg2.length()) {
                            this.currentMsg.append(msg2.substring(index + 1));
                        }
                    } else {
                        goAhead = false;
                    }
                }
            } catch (Exception var6) {
                var6.printStackTrace();
                System.out.println("Error in receiving string from COM-port: " + var6);
            }
        }

    }
}
