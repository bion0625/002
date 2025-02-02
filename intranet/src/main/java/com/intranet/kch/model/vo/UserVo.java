package com.intranet.kch.model.vo;

import com.intranet.kch.model.entity.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVo implements UserDetails {
    @NotBlank(message = "로그인 아이디는 비워둘 수 없습니다.")
    @Size(min = 4, max = 50, message = "로그인 아이디는 4자 이상, 50자 이하여야 합니다.")
    private String loginId;
    @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;
    @NotBlank(message = "사용자 이름은 비워둘 수 없습니다.")
    @Size(max = 100, message = "사용자 이름은 최대 100자 이하여야 합니다.")
    private String name;
    @Email(message = "유효한 이메일 주소를 입력해야 합니다.")
    private String email;
    @Pattern(
            regexp = "^\\+?[0-9. ()-]{7,25}$",
            message = "전화번호는 7자리 이상 25자리 이하의 숫자 및 허용된 문자만 포함해야 합니다."
    )
    private String phone;
    private Boolean isAccountNonExpired = true;

    public UserEntity toEntity(PasswordEncoder encoder) {
        return new UserEntity(this.loginId, encoder.encode(this.password), this.name, this.email, this.phone);
    }

    public static UserVo fromEntity(UserEntity entity) {
        return new UserVo(entity.getLoginId(), entity.getPassword(), entity.getName(), entity.getEmail(), entity.getPhone(), entity.getDeletedAt() == null);
    }

    @Override
    public String getUsername() {
        return this.loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();//TODO
    }
}
