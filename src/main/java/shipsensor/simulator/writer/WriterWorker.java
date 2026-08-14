package shipsensor.simulator.writer;


import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Component;
import shipsensor.simulator.model.SensorDataMessage;
import shipsensor.simulator.queue.SensorDataQueue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;



@Component
public class WriterWorker implements Runnable {


    private static final int WORKER_COUNT = 16;


    private static final int BATCH_SIZE = 50000;



    private final SensorDataQueue queue;


    private final BatchWriter writer;



    private volatile boolean running = true;



    private final List<Thread> workers =
            new CopyOnWriteArrayList<>();




    public WriterWorker(
            SensorDataQueue queue,
            BatchWriter writer
    ){

        this.queue = queue;
        this.writer = writer;

    }







    @PostConstruct
    public void start(){


        for(int i = 0; i < WORKER_COUNT; i++){


            Thread thread =
                    new Thread(
                            this,
                            "sensor-writer-" + i
                    );


            thread.setDaemon(false);


            workers.add(thread);


            thread.start();


        }



        System.out.println(
                "WriterWorker started : "
                        + WORKER_COUNT
                        + " threads"
        );


    }








    @Override
    public void run(){


        while(running || !queue.isEmpty()){


            try{


                List<SensorDataMessage> messages =
                        queue.pollBatch(
                                BATCH_SIZE
                        );



                if(messages.isEmpty()){


                    Thread.sleep(5);


                    continue;

                }



                writer.write(messages);



                queue.increaseInserted(
                        messages.size()
                );



            }
            catch(Exception e){


                e.printStackTrace();



                try{

                    Thread.sleep(100);

                }
                catch(InterruptedException ignored){

                    Thread.currentThread()
                            .interrupt();

                }


            }


        }


    }








    /**
     * Spring关闭时执行
     */
    @PreDestroy
    public void shutdown(){


        System.out.println(
                "Stopping WriterWorker..."
        );


        running = false;



        for(Thread worker : workers){


            try {


                worker.join(10000);


            }
            catch(InterruptedException e){


                Thread.currentThread()
                        .interrupt();


            }


        }



        System.out.println(
                "WriterWorker stopped"
        );


    }






    public void stop(){


        running = false;


    }


}