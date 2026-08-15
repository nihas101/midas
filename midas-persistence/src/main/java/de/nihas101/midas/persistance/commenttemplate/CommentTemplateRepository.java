package de.nihas101.midas.persistance.commenttemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentTemplateRepository extends JpaRepository<CommentTemplateEntity, Integer> {
    List<CommentTemplateEntity> findAllByOrderByTextAsc();
}
