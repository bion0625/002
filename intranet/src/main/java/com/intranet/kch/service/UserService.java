package com.intranet.kch.service;

import com.intranet.kch.model.vo.UserVo;
import com.intranet.kch.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public void join(UserVo vo) {
        userRepository.save(vo.toEntity(encoder));
    }

    public Optional<UserVo> getUserByLoginId(String loginId) {
        return userRepository.findByLoginIdAndDeletedAtIsNull(loginId).map(UserVo::fromEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return getUserByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + "is not found !"));
    }
}
