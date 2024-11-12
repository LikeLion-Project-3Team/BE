package likelion.devbreak.repository;

import jdk.dynalink.Operation;
import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.BlogMember;
import likelion.devbreak.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogMemberRepository extends JpaRepository<BlogMember, Long> {
    List<BlogMember> findBlogMemberByBlogId(Long blogId);

    List<BlogMember> findBlogMemberByUserName(String username);
}
