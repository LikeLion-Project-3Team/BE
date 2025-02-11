package likelion.devbreak.repository;

import likelion.devbreak.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByUserNameOrderByCreatedAtDesc(String userName);
    List<Notice> findByUserName(String userName);
    int countByUserNameAndIsViewedFalse(String userName);
}
