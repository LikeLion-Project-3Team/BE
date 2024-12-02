package likelion.devbreak.repository;

import likelion.devbreak.domain.Favorites;
import likelion.devbreak.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    Optional<Favorites> findByUserIdAndBlogId(Long userId, Long blogId);

    List<Favorites> findByUserIdAndIsFavoritedTrue(Long userId);

    void deleteAllByUser(User user);

}
