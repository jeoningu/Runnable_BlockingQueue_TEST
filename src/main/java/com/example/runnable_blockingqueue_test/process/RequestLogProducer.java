package com.example.runnable_blockingqueue_test.process;

import com.example.runnable_blockingqueue_test.repository.RequestLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Component
public class RequestLogProducer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RequestLogRepository requestLogRepository;

    private List<Future<?>> futureList;
    private ExecutorService executor;
    private BlockingQueue<com.example.runnable_blockingqueue_test.model.RequestLog> blockingQueue;

    private final int maxCoreCnt = Runtime.getRuntime().availableProcessors(); // 사용가능한 코어 개수만큼 스레드 풀 만들기 위함.

    @Value("${history.thread_cnt}")
    private int myThreadCnt;

    private final int queueCnt = 1000;

    public boolean queueAddRequest(com.example.runnable_blockingqueue_test.model.RequestLog data) {
        return blockingQueue.offer(data);
    }

    @PostConstruct
    public void init() {

        // ExecutorService docs - https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html
        // ExecutorService 정리 - https://backtony.github.io/java/2022-05-28-java-52/
        // Executor를 이용한 병렬 처리 - https://seongtak-yoon.tistory.com/59
        // BlockingQueue docs - https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/BlockingQueue.html
        // BlockingQueue 정리 - https://do-study.tistory.com/39

        // 스레드풀 작업 - https://passiflore.tistory.com/35
        //              - https://byul91oh.tistory.com/246
        //              - https://erim1005.tistory.com/entry/ExecutorService%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%B4-multi-thread-%ED%99%9C%EC%9A%A9%ED%95%98%EA%B8%B0-Java

        /*
        Future
         - 비동기로 수행한 쓰레드가 수행한 결과를 담는다.
         - Future 내부적으로 Thread-Safe 하도록 구현되어있다.
        ExecutorService, Runnable
         - java.util.concurrent.Executors와 java.util.concurrent.ExecutorService를 이용하면 간단히 쓰레드풀을 생성하여 병렬처리를 할 수 있습니다.
         - 인자 개수만큼 고정된 스레드를 생성하는 스레드 풀 : ExecutorService executor = Executors.newFixedThreadPool(int n);
        BlockingQueue
         - Queue가 꽉찼을때의 삽입 시도 / Queue가 비어있을때의 추출 시도를 막는 다는 것이다.이 자동으로 '막는' 기능이 있어 BlockingQueue 의 구현체는 모두 Thread-safe 하다.
         */

        futureList = new ArrayList<>();

        // 최대코어 수 보다 설정한스레드 수가 많으면 최대코어 수로 설정
        if (myThreadCnt>maxCoreCnt) {
            myThreadCnt = maxCoreCnt;
        }
        executor = Executors.newFixedThreadPool(myThreadCnt);

        blockingQueue = new ArrayBlockingQueue<com.example.runnable_blockingqueue_test.model.RequestLog>(queueCnt); // lock 기능이 없는 일반 큐로 테스트 해보려 했으나 동시성 문제 재현이 필요해서 일단 중단.  blockingQueue = new LinkedList<History>();

        for (int i=0; i<myThreadCnt; i++){
            futureList.add( executor.submit(new RequestLogConsumer(blockingQueue, requestLogRepository)));
        }

    }


    @PreDestroy
    public void destroy() {
        // TODO: 서버 종료 후에 Runnable이 계쏙 돔... 상관 없나?
        // TODO: 아래와 같이 메모리 해제가 꼭 필요한 건가?

        logger.info("HistoryProducer destroy start");
        for( Future future:futureList) {
            logger.info("HistoryProducer destroy 1");
            future.cancel( true );
        }
        if( null != futureList ) {
            logger.info("HistoryProducer destroy 2");
            futureList.clear();
            futureList = null;
        }
        if( null != blockingQueue ) {
            logger.info("HistoryProducer destroy 3");
            blockingQueue.clear();
            blockingQueue = null;
        }
        if( null != executor ) {
            logger.info("HistoryProducer destroy 4");
            executor.shutdownNow();
        }
        logger.info("HistoryProducer destroy end");
    }
}
