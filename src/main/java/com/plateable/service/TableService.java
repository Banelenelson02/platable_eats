package com.plateable.service;

import com.plateable.dto.request.CreateTableRequest;
import com.plateable.dto.response.TableResponse;
import com.plateable.model.Table;
import com.plateable.model.TableStatus;
import com.plateable.repository.TableRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TableService {
    private final TableRepository repository;

    public TableService(TableRepository repository) {
        this.repository = repository;
    }

    public List<TableResponse> getAllTables() {
        return repository.findAll().stream().map(TableResponse::from).toList();
    }

    public List<TableResponse> getAvailableTables(int minCapacity) {
        return repository.findByStatusAndCapacityGreaterThanEqual(TableStatus.AVAILABLE, minCapacity)
                .stream().map(TableResponse::from).toList();
    }

    public TableResponse createTable(CreateTableRequest request) {
        Table table = new Table(request.tableId(), request.capacity());
        return TableResponse.from(repository.save(table));
    }
}
