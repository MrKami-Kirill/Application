package main.rest.model.repositories;

import main.rest.model.entity.PostVote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostVoteRepository extends JpaRepository<PostVote, Integer> {
}
