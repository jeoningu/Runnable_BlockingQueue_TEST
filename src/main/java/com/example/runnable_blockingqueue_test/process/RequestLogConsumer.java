package com.example.runnable_blockingqueue_test.process;

import com.example.runnable_blockingqueue_test.repository.RequestLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;


public class RequestLogConsumer implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Queue<com.example.runnable_blockingqueue_test.model.RequestLog> blockingQueue;

    private RequestLogRepository requestLogRepository;

    public RequestLogConsumer(Queue<com.example.runnable_blockingqueue_test.model.RequestLog> blockingQueue, RequestLogRepository requestLogRepository) {
        this.blockingQueue = blockingQueue;
        this.requestLogRepository = requestLogRepository;
    }

    @Override
    public void run() {
        while( true ) {
            //logger.debug("HistoryConsumer running~~~~~~~~~");

            try {
                // poll 데이터 확인
                logger.debug("blockingQueue.size = {}", blockingQueue.size() );
                if( blockingQueue.size() > 0 ) {

                    com.example.runnable_blockingqueue_test.model.RequestLog blockingQueueData = blockingQueue.poll();
                    logger.debug("blockingQueueData = {}",blockingQueueData);

                    requestLogRepository.save(blockingQueueData);
                }

                Thread.sleep(3000);
            } catch (InterruptedException e) {
                logger.error( "No stop keep going! HistoryConsumer InterruptedException : {}", e );
            } catch (Exception e) {
                logger.error( "No stop keep going! HistoryConsumer Exception : {}", e );
            }
        }
    }


}
