package com.project.simplegw.system.repositories;

import java.util.List;

import com.project.simplegw.system.entities.Notification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {
    long countByMemberIdAndChecked(Long memberId, boolean checked);
    List<Notification> findByMemberIdOrderByIdDesc(Long memberId);

    @Query(
        value = """
                    delete a
                    from notification a
                        join member_environment_setting b on a.member_id = b.member_id
                    where a.created_date < dateadd(day, (-1 * b.sys_noti_del_day), cast(getdate() as date))
                """,
        nativeQuery = true
    )
    void removeOldNotifications();
}
