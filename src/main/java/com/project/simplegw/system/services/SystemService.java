package com.project.simplegw.system.services;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.simplegw.member.services.MemberService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SystemService {
    private final MemberService memberService;
    private final NotificationService notiService;

    @Autowired
    public SystemService(MemberService memberService, NotificationService notiService) {
        this.memberService = memberService;
        this.notiService = notiService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");

        diskUsageScan();
    }



    void diskUsageScan() {
        List<Long> admins = memberService.getAdmins();

        FileSystem fileSystem = FileSystems.getDefault();

        fileSystem.getFileStores().forEach(store -> {
            try {
                log.info("{}Drive name: {}", System.lineSeparator(), store.name());
                log.info("File system: {}", store.type());

                // 소수 2번째 자리까지 표시하기 위해서 * 100 / 100.0
                double fullSize = Math.round(store.getTotalSpace() / 1024.0 / 1024.0 / 1024.0 * 100) / 100.0;
                double usedSize = Math.round((store.getTotalSpace() - store.getUnallocatedSpace()) / 1024.0 / 1024.0 / 1024.0 * 100) / 100.0;
                double usableSize = Math.round(store.getUsableSpace() / 1024.0 / 1024.0 / 1024.0 * 100) / 100.0;
                double usedRatio = Math.round((usedSize / fullSize * 100) * 100) / 100.0;

                log.info("Full size: {} GB", fullSize);
                log.info("Used size: {} GB", usedSize);
                log.info("Used ratio: {} %", usedRatio);
                log.info("Usable size: {} GB{}", usableSize, System.lineSeparator());

                if(usedRatio >= 90.0)
                    admins.forEach(id -> notiService.create(id, new StringBuilder("서버 드라이브 '").append(store.name()).append("' 의 사용량이 ").append(usedRatio).append(" % 입니다.").toString()));
            
            } catch(Exception e) {
                e.printStackTrace();
            }
        });
    }
}
