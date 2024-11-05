package likelion.devbreak.domain;

public class BlogMember {
    private String userName;
    private Long userId;
    private Long blogId;

    public BlogMember(String userName, Long userId, Long blogId){
        this.userName = userName;
        this.userId = userId;
        this.blogId = blogId;
    }

    public String getUserName() {
        return userName;
    }


}
