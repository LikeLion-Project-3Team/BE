package likelion.devbreak.repository;

import likelion.devbreak.domain.Blog;
import likelion.devbreak.domain.BlogMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogMemberRepository extends JpaRepository<BlogMember, Long> {
    List<BlogMember> findBlogMemberByBlogId(Long blogId);

    Optional<BlogMember> findBlogMemberByUserNameAndBlog(String username, Blog blog);

    List<BlogMember> findBlogMemberByBlog(Blog blog);
    List<BlogMember> findBlogMemberByUserName(String userName);

}
