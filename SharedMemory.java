import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SharedMemory {
    
    private ArrayList<Packet> Packets;
    private static ReentrantReadWriteLock lock;

    public SharedMemory(){
        Packets = new ArrayList<Packet>();
        lock = new ReentrantReadWriteLock();
    }
    //        in this part just send packet and read locked
    public void send(Packet Packet){
        lock.writeLock().lock();
        Packets.add(Packet);
        lock.writeLock().unlock();
    }
    //        in this part just read packet and write locked
    public Packet read(int index){
        Packet Packet;
        lock.readLock().lock();
        try {
            Packet = Packets.get(index);
        } catch (IndexOutOfBoundsException e) {
            Packet = null;
        }
        lock.readLock().unlock();
        return Packet;
    }
    
}
//        here we make a class contain informations of our packet
class Packet {
    public int index;
    public int pckt;
    public Packet(int index , int pckt){
        this.pckt = pckt;
        this.index = index;
    }
}
