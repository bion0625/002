package com.intranet.kch.model.vo;

import com.intranet.kch.model.entity.IVExcelEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IVExcelVo {
    private String name;
    private Long nights;

    public IVExcelEntity toEntity(Long price, Long bcId) {// TODO 추후 날짜 작업 추가
        IVExcelEntity entity = new IVExcelEntity();
        entity.setName(this.name);
        entity.setNights(this.nights);
        entity.setTotalPrice(price * this.nights);
        entity.setBcId(bcId);
        return entity;
    }

    public static IVExcelVo fromEntity(IVExcelEntity entity) {
        return new IVExcelVo(entity.getName(), entity.getNights());
    }
}
