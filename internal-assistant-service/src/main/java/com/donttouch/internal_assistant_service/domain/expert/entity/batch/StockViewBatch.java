package com.donttouch.internal_assistant_service.domain.expert.entity.batch;

import com.donttouch.common_service.auth.entity.InvestmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Table(name = "stock_view_batch")
@IdClass(StockViewBatchId.class)
@Getter
@Setter
@NoArgsConstructor
public class StockViewBatch {

    @Id
    @Column(name = "stock_id")
    private String stockId;

    @Id
    @Column(name = "stock_period")
    @Enumerated(EnumType.STRING)
    private InvestmentType stockPeriod;

    public StockViewBatch(String stockId, InvestmentType stockPeriod) {
        this.stockId = stockId;
        this.stockPeriod = stockPeriod;
    }
}

class StockViewBatchId implements Serializable {
    private String stockId;
    private InvestmentType stockPeriod;

    public StockViewBatchId() {}
    public StockViewBatchId(String stockId, InvestmentType stockPeriod) {
        this.stockId = stockId;
        this.stockPeriod = stockPeriod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockViewBatchId)) return false;
        StockViewBatchId that = (StockViewBatchId) o;
        return stockId.equals(that.stockId) && stockPeriod == that.stockPeriod;
    }

    @Override
    public int hashCode() {
        return stockId.hashCode() + stockPeriod.hashCode();
    }
}
