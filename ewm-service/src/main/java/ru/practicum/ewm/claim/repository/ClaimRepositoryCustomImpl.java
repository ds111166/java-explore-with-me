package ru.practicum.ewm.claim.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.claim.data.CauseClaim;
import ru.practicum.ewm.claim.model.Claim;

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
public class ClaimRepositoryCustomImpl implements ClaimRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public List<Claim> findClaimsByParameters(List<Long> userIds, List<Long> commentIds, List<CauseClaim> causes,
                                              LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Claim> query = builder.createQuery(Claim.class);
        Root<Claim> claimRoot = query.from(Claim.class);
        List<Predicate> filterPredicates = new ArrayList<>();

        if (userIds != null && !userIds.isEmpty()) {
            filterPredicates.add(claimRoot.get("author").in(userIds));
        }
        if (commentIds != null && !commentIds.isEmpty()) {
            filterPredicates.add(claimRoot.get("comment").in(commentIds));
        }
        if (causes != null && !causes.isEmpty()) {
            filterPredicates.add(claimRoot.get("cause").in(causes));
        }
        if (startDate != null && endDate != null) {
            filterPredicates.add(builder.between(claimRoot.get("createdOn"), startDate, endDate));
        }
        query.where(filterPredicates.toArray(new Predicate[0]));
        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();
                if (order.isAscending()) {
                    query.orderBy(builder.asc(claimRoot.get(property)));
                } else if (order.isDescending()) {
                    query.orderBy(builder.desc(claimRoot.get(property)));
                }
            }
        } else {
            query.orderBy(builder.desc(claimRoot.get("id")));
        }

        TypedQuery<Claim> typedQuery = entityManager.createQuery(query);
        if (pageable.isPaged()) {
            typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            typedQuery.setMaxResults(pageable.getPageSize());
        }
        return typedQuery.getResultList();
    }
}
