package likelion.devbreak.domain;

public class BlogMember {
    private String userName;
    private Long blogId;

    public BlogMember(String userName, Long blogId){
        this.userName = userName;
        this.blogId = blogId;
    }

    public String getUserName() {
        return userName;
    }


}
