package group.project.cursusonlinecoursemanagement.premium.repository;

import group.project.cursusonlinecoursemanagement.premium.domain.entity.UserPremium;
import group.project.cursusonlinecoursemanagement.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserPremiumRepository extends JpaRepository<UserPremium, Long> {
    Optional<UserPremium> findByUser(User user);
}
