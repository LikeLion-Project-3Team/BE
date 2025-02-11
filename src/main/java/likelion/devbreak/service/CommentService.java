package likelion.devbreak.service;

import likelion.devbreak.domain.Article;
import likelion.devbreak.domain.Comment;
import likelion.devbreak.domain.Notice;
import likelion.devbreak.domain.User;
import likelion.devbreak.domain.dto.request.CommentRequest;
import likelion.devbreak.domain.dto.response.CommentResponse;
import likelion.devbreak.oAuth.domain.CustomUserDetails;
import likelion.devbreak.repository.CommentRepository;
import likelion.devbreak.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final GlobalService globalService;
    private final CommentRepository commentRepository;
    private final NoticeRepository noticeRepository;

    @Transactional
    public CommentResponse createComment(CustomUserDetails customUserDetails, CommentRequest commentRequest){
        User user = globalService.findUser(customUserDetails);
        Article article = globalService.findArticleById(commentRequest.getArticleId());

        Comment comment = new Comment();
        comment.setUserName(user.getUserName());
        comment.setContent(commentRequest.getContent());
        comment.setArticle(article);

        Comment saved = commentRepository.save(comment);

        Notice notice = new Notice(article.getUser().getUserName(),"새로운 댓글", user.getUserName(), article.getId(), false, article.getBlog().getBlogName());
        noticeRepository.save(notice);

        return new CommentResponse(saved.getArticle().getId(), saved.getId(), saved.getUserName(), saved.getContent(), saved.getCreatedAt(), true , true);
    }

    public List<CommentResponse> getComment(CustomUserDetails customUserDetails, Long articleId) {
        // 로그인하지 않은 경우 user는 null 처리
        User user = customUserDetails != null ? globalService.findUser(customUserDetails) : null;
        Article article = globalService.findArticleById(articleId);

        List<CommentResponse> commentResponseList = commentRepository.findCommentByArticle(article)
                .stream()
                .map(comment -> {
                    // 로그인하지 않은 경우 isSameUser는 false
                    boolean isSameUser = user != null && comment.getUserName().equals(user.getUserName());
                    return new CommentResponse(
                            comment.getArticle().getId(),
                            comment.getId(),
                            comment.getUserName(),
                            comment.getContent(),
                            comment.getUpdatedAt(),
                            isSameUser,
                            isSameUser
                    );
                })
                .collect(Collectors.toList());

        return commentResponseList;
    }


    @Transactional
    public CommentResponse updateComment(CustomUserDetails customUserDetails, Long commentId, CommentRequest commentRequest){
        User user = globalService.findUser(customUserDetails);
        Article article = globalService.findArticleById(commentRequest.getArticleId());
        Comment comment = globalService.findCommentById(commentId);

        boolean isSameUser = comment.getUserName().equals(user.getUserName());

        if(!isSameUser){
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        comment.setContent(commentRequest.getContent());
        Comment save = commentRepository.save(comment);

        Notice notice = new Notice(article.getUser().getUserName(),"수정된 댓글", user.getUserName(), article.getId(), false, article.getBlog().getBlogName());
        noticeRepository.save(notice);

        return new CommentResponse(
                save.getArticle().getId(),
                save.getId(),
                save.getUserName(),
                save.getContent(),
                save.getUpdatedAt(),
                isSameUser,
                isSameUser
        );
    }

    @Transactional
    public void deleteComment(CustomUserDetails customUserDetails,Long articleId, Long commentId){
        User user = globalService.findUser(customUserDetails);
        globalService.findArticleById(articleId);
        Comment comment = globalService.findCommentById(commentId);

        boolean isSameUser = comment.getUserName().equals(user.getUserName());

        if(!isSameUser){
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        commentRepository.deleteById(commentId);
    }
}
