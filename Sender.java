import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Sender implements Runnable {
    
    private SharedMemory shared;
    private int len_window;
    private int[] packet_transmit;
    private int[] elements_is_sent;
    private int[] sent;
    private int pointer;
    private boolean SR;
    //    constructor to sender an thir atributes
    public Sender(SharedMemory shared, int len_window, int[] packet, boolean SR) {
        this.shared = shared;
        this.len_window = len_window;
        this.packet_transmit = packet;
        this.SR = SR;
        this.elements_is_sent = new int[this.packet_transmit.length];
        this.sent = new int[this.packet_transmit.length];
    }
    //    function that have main work of sender
    @Override
    public void run() {
        while (pointer != packet_transmit.length) {
            if (sent[pointer] == 0) {
                for (int i = pointer; i < pointer + len_window; i++) {
                    Packet Packet = new Packet(i, packet_transmit[i]);
                    write("Console : Sender relaying Packet to buffer with index " + Packet.index + " and packet " + Packet.pckt
                            + "\n");
                    shared.send(Packet);
                    this.sent[i] = 1;
                }
            }
        // here we define a flag to check all of data is send or isn't
            int check_flag = 1;
            for (int i = pointer; i < pointer + len_window; i++) {
                if (elements_is_sent[i] != 1) {
                    check_flag = 0;
                    break;
                }
            }
            if (check_flag == 1) {
                pointer = pointer + len_window;
            }
            try {
                Thread.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    //    function do work if packet send on time
    public void ACK(int index) {
        this.elements_is_sent[index] = 1;
        write("Console : Received ACK relay from recevier for packet with index " + index + "\n");
    }
//    function do work if packet send on out of time
    public void NACK(int index) {
        if (SR) {
            Packet Packet = new Packet(index, packet_transmit[index]);
            write("Console : Sender relaying Packet with index " + index + " again because NACK was relayed.\n");
            shared.send(Packet);
        } else {
            for (int i = pointer; i < pointer + len_window; i++) {
                Packet Packet = new Packet(i, packet_transmit[i]);
                write("Console : relaying Packet to buffer with index : " + Packet.index + " and packet " + Packet.pckt
                        + " (GBN).\n");
                shared.send(Packet);
            }
        }
    }

    // function to write system output inside text files

    private void write(String message) {
        try {
            FileWriter fileWrite = new FileWriter("./sender.txt", true);
            BufferedWriter buffWrite = new BufferedWriter(fileWrite);
            buffWrite.write(message);
            buffWrite.newLine();
            buffWrite.close();
        } catch (IOException err) {
            System.out.println(err.getMessage());
        }
    }
}