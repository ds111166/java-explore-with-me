package ru.practicum.ewm.event.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.dto.FindEventsParametrs;
import ru.practicum.ewm.event.model.Event;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {

    private final EntityManager entityManager;

    @Override
    public Page<Event> findEventsByParameters(FindEventsParametrs parameters, Pageable pageable) {

        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> query = builder.createQuery(Event.class);
        Root<Event> eventRoot = query.from(Event.class);
        List<Predicate> filterPredicates = new ArrayList<>();

        if (parameters.users != null) {
            filterPredicates.add(eventRoot.get("initiator").in(parameters.users));
        }
        if (parameters.states != null) {
            filterPredicates.add(eventRoot.get("state").in(parameters.states));
        }
        if (parameters.categories != null) {
            filterPredicates.add(eventRoot.get("category").in(parameters.categories));
        }
        if (parameters.text != null) {
            Predicate annotationPredicate = builder.like(builder.lower(eventRoot.get("annotation")),
                    "%" + parameters.text.toLowerCase() + "%");
            Predicate descriptionPredicate = builder.like(builder.lower(eventRoot.get("description")),
                    "%" + parameters.text.toLowerCase() + "%");
            filterPredicates.add(builder.or(annotationPredicate, descriptionPredicate));
        }
        if (parameters.paid != null) {
            Predicate paidPredicate = (parameters.paid)
                    ? builder.isTrue(eventRoot.get("paid"))
                    : builder.isFalse(eventRoot.get("paid"));
            filterPredicates.add(paidPredicate);
        }
        if (parameters.startDate != null && parameters.endDate != null) {
            filterPredicates.add(builder.between(eventRoot.get("eventDate"), parameters.startDate, parameters.endDate));
        }
        if (parameters.onlyAvailable != null) {
            Predicate onlyAvailablePredicate = (parameters.onlyAvailable)
                    ? builder.isTrue(eventRoot.get("onlyAvailable"))
                    : builder.isFalse(eventRoot.get("onlyAvailable"));
            filterPredicates.add(onlyAvailablePredicate);
        }
        query.where(filterPredicates.toArray(new Predicate[0]));

        if (pageable.getSort().isSorted()) {
            for (Sort.Order order : pageable.getSort()) {
                String property = order.getProperty();
                if (order.isAscending()) {
                    query.orderBy(builder.asc(eventRoot.get(property)));
                } else if (order.isDescending()) {
                    query.orderBy(builder.desc(eventRoot.get(property)));
                }
            }
        } else {
            query.orderBy(builder.desc(eventRoot.get("createdOn")));
        }
        TypedQuery<Event> typedQuery = entityManager.createQuery(query);
        if (pageable.isPaged()) {
            typedQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
            typedQuery.setMaxResults(pageable.getPageSize());
        }
        List<Event> events = typedQuery.getResultList();
        return new PageImpl<>(events);
    }
}
