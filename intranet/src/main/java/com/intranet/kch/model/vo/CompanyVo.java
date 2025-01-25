package com.intranet.kch.model.vo;

import com.intranet.kch.model.entity.CompanyEntity;
import com.intranet.kch.util.DateFormatUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyVo {
    Long id;
    @NotBlank(message = "회사 정보 제목은 비워둘 수 없습니다.")
    private String companyTitle;
    @NotBlank(message = "회사명은 비워둘 수 없습니다.")
    private String companyName;
    @NotBlank(message = "회사 주소는 비워둘 수 없습니다.")
    private String companyAddr;
    @NotBlank(message = "인보이스 약어는 비워둘 수 없습니다.")
    private String companyInvoiceAcronym;
    private String createdAt;
    private String updatedAt;

    public CompanyEntity toEntity() {
        CompanyEntity entity = new CompanyEntity();
        entity.setCompanyTitle(this.companyTitle);
        entity.setCompanyName(this.companyName);
        entity.setCompanyAddr(this.companyAddr);
        entity.setCompanyInvoiceAcronym(this.companyInvoiceAcronym);
        return entity;
    }

    public static CompanyVo fromEntity(CompanyEntity entity) {
        return new CompanyVo(
                entity.getId(),
                entity.getCompanyTitle(),
                entity.getCompanyName(),
                entity.getCompanyAddr(),
                entity.getCompanyInvoiceAcronym(),
                DateFormatUtil.localDateTimeToString(entity.getCreatedAt(), "yyyy-MM-dd HH:mm:ss"),
                DateFormatUtil.localDateTimeToString(entity.getUpdatedAt(), "yyyy-MM-dd HH:mm:ss"));
    }
}
