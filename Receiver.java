import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class Receiver implements Runnable {
    private Sender sender;
    private SharedMemory shared;
    private int len_window;
    private int[] packet_transmit;
    private int packetSize;
    private int is_recevied[];
    private int pointer;
    private int point_to_sharedMe;
    //    constructor to make Receiver an thir atributes
    public Receiver(int len_window, Sender sender, int packetSize , SharedMemory shared) {
        this.len_window = len_window;
        this.sender = sender;
        this.packetSize = packetSize;
        this.shared = shared;
        packet_transmit = new int[this.packetSize];
        is_recevied = new int[this.packetSize];
        pointer = 0;
        point_to_sharedMe = 0;
    }

    @Override
    //    function that have main work of Receiver
    public void run() {
        while(pointer != packetSize){
            Packet Packet = shared.read(point_to_sharedMe);
            //    here we generate a random number and check time out to call Nack function or Ack function
            if (Packet != null){
                int time_out = new Random().nextInt(101);
                if (time_out < 50){
                    write("Console : Received Packet with index " + Packet.index + " from buffer , relaying NACK to sender.\n");
                    sender.NACK(Packet.index);
                } else {
                    packet_transmit[Packet.index] = Packet.pckt;
                    is_recevied[Packet.index] = 1;
                    write("Console : Received Packet with index " + Packet.index + " from buffer , relaying ACK to sender.\n");
                    sender.ACK(Packet.index);
                }
                point_to_sharedMe = point_to_sharedMe + 1;
            } else {
                write("Console : Receiver has not added index " + point_to_sharedMe + " yet.\n");
            }

            write("Receiver : [");
            for(int i=0 ;i<packetSize;i++){
                write("" + packet_transmit[i]);
            }
            write("]");

            // here we check to see if all packets are received . if answer was negetive we do above work again
            boolean allRelaysDone = true;
            for(int i = pointer ; i < pointer + len_window ; i++){
                if(is_recevied[i] == 0){
                    allRelaysDone = false;
                    break;
                }
            }
            if(allRelaysDone == true){
                pointer = pointer + len_window;
            }
        }

        System.out.print("Process was completed with Window Size : " + this.len_window + " -and the used packet : { ");
        for(int i = 0 ; i < packet_transmit.length ; i++){
            System.out.print(packet_transmit[i] + " ");
        }
        System.out.print("}\n");
    
    }

    // function to write system output inside text files

    private void write(String message) {
        try{
            FileWriter fileWrite = new FileWriter("./Receiver.txt", true);
            BufferedWriter buffWrite = new BufferedWriter(fileWrite);
            buffWrite.write(message);
            buffWrite.newLine();
            buffWrite.close();
        }catch(IOException err){
            System.out.println(err.getMessage());
        }     
    }
}