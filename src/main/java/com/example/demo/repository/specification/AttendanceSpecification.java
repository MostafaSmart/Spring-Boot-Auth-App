package com.example.demo.repository.specification;

import com.example.demo.entity.transactions.DailyAttendanceReport;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class AttendanceSpecification {

    public static Specification<DailyAttendanceReport> getReport(
            LocalDate startDate,
            LocalDate endDate,
            Long employeeId,
            Long areaId,
            Long departmentId,
            String status
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Avoid N+1 problem by fetching associated entities
            if (Long.class != query.getResultType()) { // Only fetch for entity queries, not count queries
                root.fetch("employee", JoinType.LEFT).fetch("department", JoinType.LEFT);
                root.fetch("employee", JoinType.LEFT).fetch("area", JoinType.LEFT);
            }

            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("date"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("date"), endDate));
            }
            if (employeeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("employee").get("id"), employeeId));
            }
            if (areaId != null) {
                predicates.add(criteriaBuilder.equal(root.get("employee").get("area").get("id"), areaId));
            }
            if (departmentId != null) {
                predicates.add(criteriaBuilder.equal(root.get("employee").get("department").get("id"), departmentId));
            }
            if (status != null && !status.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
