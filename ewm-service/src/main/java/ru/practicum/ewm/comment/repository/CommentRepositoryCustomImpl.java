package ru.practicum.ewm.comment.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comment.data.StateComment;
import ru.practicum.ewm.comment.model.Comment;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Comment> findCommentsByParameters(
            List<Long> userIds, List<Long> eventIds, List<StateComment> states,
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Comment> query = builder.createQuery(Comment.class);
        Root<Comment> commentRoot = query.from(Comment.class);
        List<Predicate> filterPredicates = new ArrayList<>();

        if (userIds != null && !userIds.isEmpty()) {
            filterPredicates.add(commentRoot.get("author").in(userIds));
        }
        if (eventIds != null && !eventIds.isEmpty()) {
            filterPredicates.add(commentRoot.get("event").in(eventIds));
        }
        if (states != null && !states.isEmpty()) {
            filterPredicates.add(commentRoot.get("state").in(states));
        }
        if (startDate != null && endDate != null) {
            filterPredicates.add(builder.between(commentRoot.get("createdOn"), startDate, endDate));
        }
        query.where(filterPredicates.toArray(new Predicate[0]));
        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();
                if (order.isAscending()) {
                    query.orderBy(builder.asc(commentRoot.get(property)));
                } else if (order.isDescending()) {
                    query.orderBy(builder.desc(commentRoot.get(property)));
                }
            }
        } else {
            query.orderBy(builder.desc(commentRoot.get("id")));
        }

        TypedQuery<Comment> typedQuery = entityManager.createQuery(query);
        if (pageable.isPaged()) {
            typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            typedQuery.setMaxResults(pageable.getPageSize());
        }
        return typedQuery.getResultList();
    }
}
