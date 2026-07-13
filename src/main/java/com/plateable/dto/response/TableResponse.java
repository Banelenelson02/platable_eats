package com.plateable.dto.response;

import com.plateable.model.Table;

public record TableResponse(String tableId, int capacity, String status, String currentOrderId) {
    public static TableResponse from(Table table) {
        return new TableResponse(table.getTableId(), table.getCapacity(),
                table.getStatus().name(), table.getCurrentOrderId());
    }
}
