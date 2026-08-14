package shipsensor.simulator.queue;

import org.springframework.stereotype.Component;
import shipsensor.simulator.model.SensorDataMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class SensorDataQueue {


    /**
     * 最大缓存数据量
     */
    private static final int QUEUE_CAPACITY = 1_000_000;



    /**
     * 有界阻塞队列
     */
    private final BlockingQueue<SensorDataMessage> queue =
            new ArrayBlockingQueue<>(QUEUE_CAPACITY);



    /**
     * 已生成数据数量
     */
    private final AtomicLong generatedCount =
            new AtomicLong(0);



    /**
     * 已成功入库数量
     */
    private final AtomicLong insertedCount =
            new AtomicLong(0);




    /**
     * 生产数据
     */
    public boolean offer(SensorDataMessage data) {


        if(data == null){

            return false;

        }


        try {


            queue.put(data);


            generatedCount.incrementAndGet();


            return true;


        } catch (InterruptedException e) {


            Thread.currentThread().interrupt();


            return false;

        }

    }






    /**
     * 批量获取数据
     */
    public List<SensorDataMessage> pollBatch(int batchSize) {


        List<SensorDataMessage> list =
                new ArrayList<>(batchSize);



        queue.drainTo(
                list,
                batchSize
        );


        return list;

    }






    public boolean isEmpty(){

        return queue.isEmpty();

    }






    /**
     * 当前队列大小
     */
    public int size(){

        return queue.size();

    }






    public long getGeneratedCount(){

        return generatedCount.get();

    }






    public long getInsertedCount(){

        return insertedCount.get();

    }






    public void increaseInserted(long count){

        insertedCount.addAndGet(count);

    }


}