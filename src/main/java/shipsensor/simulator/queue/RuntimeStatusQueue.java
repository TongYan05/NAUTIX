package shipsensor.simulator.queue;


import org.springframework.stereotype.Component;
import shipsensor.simulator.model.RuntimeStatusMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;



@Component
public class RuntimeStatusQueue {


    private final BlockingQueue<RuntimeStatusMessage> queue =
            new ArrayBlockingQueue<>(500000);



    public void offer(RuntimeStatusMessage message){

        try {

            queue.put(message);

        }
        catch(Exception e){

            Thread.currentThread()
                    .interrupt();

        }

    }



    public List<RuntimeStatusMessage> pollBatch(
            int size
    ){

        List<RuntimeStatusMessage> list =
                new ArrayList<>(size);


        queue.drainTo(
                list,
                size
        );


        return list;

    }


}