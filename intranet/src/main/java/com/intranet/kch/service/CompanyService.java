package com.intranet.kch.service;

import com.intranet.kch.model.vo.CompanyVo;
import com.intranet.kch.repository.CompanyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional(readOnly = true)
@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Transactional
    public void save(CompanyVo vo) {
        companyRepository.save(vo.toEntity(getLoginId()));
    }

    @Transactional
    public void update(CompanyVo vo) {
        companyRepository.findByCompanyTitleAndDeletedAtIsNull(vo.getCompanyTitle())
                .map(entity -> {
                    entity.setCompanyName(vo.getCompanyName());
                    entity.setCompanyAddr(vo.getCompanyAddr());
                    entity.setCompanyInvoiceAcronym(vo.getCompanyInvoiceAcronym());
                    entity.setUpdateUser(getLoginId());
                    entity.setUpdatedAt(LocalDateTime.now());
                    return true;
                });
    }

    public boolean existByTitle(String title) {
        return companyRepository.findByCompanyTitleAndDeletedAtIsNull(title).isPresent();
    }

    public boolean existByInvoiceAcronym(CompanyVo companyVo) {
        return companyRepository.findByCompanyInvoiceAcronymAndDeletedAtIsNull(companyVo.getCompanyInvoiceAcronym()).filter(entity -> !entity.getCompanyTitle().equals(companyVo.getCompanyTitle())).isPresent();
    }

    public List<CompanyVo> getAll() {
        return companyRepository.findAllByDeletedAtIsNullOrderByCompanyTitle().stream()
                .map(CompanyVo::fromEntity)
                .toList();
    }

    public Page<CompanyVo> getAll(Pageable pageable) {
        return companyRepository.findAllByDeletedAtIsNullOrderByCreatedAtDesc(pageable)
                .map(CompanyVo::fromEntity);
    }

    public Page<CompanyVo> search(String keyword, Pageable pageable) {
        return companyRepository.searchByKeyword(keyword , pageable)
                .map(CompanyVo::fromEntity);
    }

    public CompanyVo getById(Long id) {
        return companyRepository.findById(id)
                .filter(entity -> entity.getDeletedAt() == null)
                .map(CompanyVo::fromEntity)
                .orElseThrow(() -> new RuntimeException(id + " is not found"));
    }

    @Transactional
    public void deleteById(Long id) {
        companyRepository.findById(id).map(entity -> {
            entity.setDeletedAt(LocalDateTime.now());
            entity.setDeleteUser(getLoginId());
            return entity;
        });
    }

    private String getLoginId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
