package com.plateable.repository;
import com.plateable.model.Table;
import com.plateable.model.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface TableRepository extends JpaRepository<Table, String> {
    List<Table> findByStatusAndCapacityGreaterThanEqual(TableStatus status, int minCapacity);
}
