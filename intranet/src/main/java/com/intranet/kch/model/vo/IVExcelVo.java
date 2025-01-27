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

    public IVExcelEntity toEntity(Double price, Long bcId, String title) {
        IVExcelEntity entity = new IVExcelEntity();
        entity.setName(title + "_" + this.name);
        entity.setNights(this.nights);
        entity.setTotalPrice(price * (double)this.nights);
        entity.setBcId(bcId);
        return entity;
    }

    public static IVExcelVo fromEntity(IVExcelEntity entity) {
        return new IVExcelVo(entity.getName().substring(entity.getName().lastIndexOf("M")), entity.getNights());
    }
}
