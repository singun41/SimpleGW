package com.project.simplegw.system.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.project.simplegw.common.vos.Constants;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class LoggingAop {
    // 로직 및 성능 개선을 위해 AOP로 성능을 측정하고, 기준 시간 이상 소요되는 로직을 로그로 남긴다.
    // 서비스 로직에 영향이 없어야 하기 때문에 비동기로 처리하고, 내용을 파일에 안전하게 쓰기 위해서 tread-safe한 ArrayBlockingQueue 를 사용한다.

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static int QUEUE_SIZE = 5000;

    // 서비스나 메서드 별로 로그파일을 따로 만들어서 보기 위해 hashmap에 이름으로 큐를 분리
    private final static ConcurrentHashMap<String, ArrayBlockingQueue<String>> logStorage = new ConcurrentHashMap<>();

    @Async
    public void logging(ProceedingJoinPoint pjp, long collapsedTime, String name) throws Exception {   // Aop 클래스에서 호출
        String className = pjp.getSignature().getDeclaringTypeName();
        String methodName = pjp.getSignature().getName();

        String logContent = String.format(
            "exec time: %s, collapsed time(ms): %d %s \t className: %s, method name: %s %s \t args: %s %s%s",

            LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).toString(), collapsedTime,
            System.lineSeparator(),
            
            className, methodName,
            System.lineSeparator(),

            // 메서드에 사용한 파라미터 Object를 toString으로 변경하기 위해서 stream으로 처리한다.
            Arrays.stream(pjp.getArgs()).map(Object::toString).collect(Collectors.toList()),
            System.lineSeparator(), System.lineSeparator()
        );

        if(logStorage.get(name) == null) {
            ArrayBlockingQueue<String> logQueue = new ArrayBlockingQueue<>(QUEUE_SIZE);
            logStorage.put(name, logQueue);
        }
        logStorage.get(name).put(logContent);
    }

    // logStorage에 key값인 이름으로 로그파일을 각각 생성
    private Path getLogFile(String name) throws Exception {
        String daily_path = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE).toString();
        
        Path logPath = Paths.get(new StringBuilder(Constants.SYSTEM_PATH).append("log_aop/").append(name).append("/").toString());
        Path logFile = Paths.get(
            new StringBuilder(logPath.toString()).append("/").append("log_").append(name).append("_").append(daily_path.replace("-", "").substring(2)).append(".txt").toString()
        );
        
        if(Files.notExists(logPath)) {
            Files.createDirectories(logPath);
        }
        if(Files.notExists(logFile)) {
            Files.createFile(logFile);
        }
        return logFile;
    }

    // Scheduler 클래스에서 매 30초 주기로 동작하도록 함.
    public void writingFileFromQueue() throws Exception {
        for(String name : logStorage.keySet()) {
            ArrayBlockingQueue<String> logQueue = logStorage.get(name);
            if(logQueue.remainingCapacity() == QUEUE_SIZE)
                return;
            
            Path filePath = getLogFile(name);
            int size = logStorage.get(name).size();

            for(int i=0; i<size; i++) {
                try {
                    Files.writeString(filePath, logQueue.take(), StandardOpenOption.APPEND);
                } catch(Exception e) {
                    logger.warn("{}{}aop logging 중 에러가 발생하였습니다.", e.getMessage(), System.lineSeparator());
                }
            }
        }
    }
}
