package com.example.movie.service;

import com.example.movie.domain.user.User;
import com.example.movie.dto.UserDTO;
import com.example.movie.domain.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserService {
    private final UserRepository userRepo;
    public AdminUserService(UserRepository userRepo){ this.userRepo = userRepo; }

    @Transactional(readOnly = true)
    public Page<UserDTO> list(String q, Pageable pageable){
        // Đơn giản: lấy tất cả rồi map sang DTO (nếu dữ liệu lớn, bổ sung query findByUsernameContainingOrEmailContaining)
        return userRepo.findAll(pageable).map(AccountService::toDTO);
    }

    @Transactional
    public UserDTO updateRole(Long id, String role){
        if(!"ADMIN".equals(role) && !"USER".equals(role)){
            throw new IllegalArgumentException("Role không hợp lệ");
        }
        User u = userRepo.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        u.setRole(role);
        return AccountService.toDTO(u);
    }
}
