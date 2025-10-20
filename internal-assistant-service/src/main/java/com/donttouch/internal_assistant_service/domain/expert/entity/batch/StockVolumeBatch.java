package com.donttouch.internal_assistant_service.domain.expert.entity.batch;

import com.donttouch.common_service.auth.entity.InvestmentType;
import com.donttouch.internal_assistant_service.domain.member.entity.Side;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.io.Serializable;

@Entity
@Table(name = "stock_volume_batch")
@IdClass(StockVolumeBatchId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockVolumeBatch {

    @Id
    @Column(name = "stock_id")
    private String stockId;

    @Id
    @Column(name = "stock_period")
    @Enumerated(EnumType.STRING)
    private InvestmentType stockPeriod;

    @Id
    @Column(name = "side")
    @Enumerated(EnumType.STRING)
    private Side side;

    public StockVolumeBatch(String stockId, Side side, InvestmentType stockPeriod) {
        this.stockId = stockId;
        this.side = side;
        this.stockPeriod = stockPeriod;
    }
}

class StockVolumeBatchId implements Serializable {
    private String stockId;
    private InvestmentType stockPeriod;
    private Side side;

    public StockVolumeBatchId() {}
    public StockVolumeBatchId(String stockId, Side side, InvestmentType stockPeriod) {
        this.stockId = stockId;
        this.side = side;
        this.stockPeriod = stockPeriod;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockVolumeBatchId)) return false;
        StockVolumeBatchId that = (StockVolumeBatchId) o;
        return stockId.equals(that.stockId) && stockPeriod == that.stockPeriod && side == that.side;
    }

    @Override
    public int hashCode() {
        return stockId.hashCode() + stockPeriod.hashCode() + side.hashCode();
    }
}
