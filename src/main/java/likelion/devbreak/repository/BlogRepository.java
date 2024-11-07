package likelion.devbreak.repository;

import likelion.devbreak.domain.Blog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findAllByUserId(Long userId);
    Optional<Blog> findByGitRepoUrl(String gitRepoUrl);
}
