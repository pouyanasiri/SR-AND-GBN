import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        //  at the first we ask question about mode of protocol
        //  then we get size of data that we want to transmit
        //  then we every element of data and store them in an array
        //  then we get the size of every packet of data's that we want to transmit

        Scanner Scanner = new Scanner(System.in);
        System.out.println( "\n"+"1 : to run Selective Repeat ARQ  " +
                "\n" + "2 : to run GBN  "+"\nEnter your choice : " );
        int sr_arq = Scanner.nextInt();
        boolean SR = (sr_arq == 1);

        System.out.print("Enter the number of data to send you want to store : ");
        int size_data_sends = Scanner.nextInt();
        int[] datas_send =  new int[size_data_sends];

        for(int i=0; i < datas_send.length; i++) {
            System.out.print("Enter data[ " + i + " ] : ");
            datas_send[i] = Scanner.nextInt();
        }

        System.out.println("Enter a Window Size ( size of data send should be divided by Window Size )  : ");

        int pack_lenght = Scanner.nextInt();

        Scanner.close();
        // so here we make a thread Pool to have two thread to execute togeter ( another way is socket)
        SharedMemory shared = new SharedMemory();
        Sender sender = new Sender(shared, pack_lenght, datas_send, SR);
        Receiver receiver = new Receiver(pack_lenght, sender, size_data_sends, shared);

        ExecutorService threads = Executors.newFixedThreadPool(2);
        threads.execute(receiver);
        threads.execute(sender);
        threads.shutdown();
    }
}


