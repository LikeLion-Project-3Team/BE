package likelion.devbreak.repository;

import likelion.devbreak.domain.BlogMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogMemberRepository extends JpaRepository<BlogMember, Long> {
    List<BlogMember> findBlogMemberByBlogId(Long blogId);

    List<BlogMember> findBlogMemberByUserName(String username);
}
