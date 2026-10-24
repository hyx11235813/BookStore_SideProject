package yixian.website.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yixian.website.common.ErrorMessages;
import yixian.website.common.Roles;
import yixian.website.dto.request.AdminUserSaveRequest;
import yixian.website.dto.request.UserSearchRequest;
import yixian.website.entity.User;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.exception.InvalidDataException;
import yixian.website.repository.UserRepository;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User findById(Integer id) {
        if (id == null) {
            throw new InvalidDataException(ErrorMessages.PARAM_CANNOT_BE_NULL);
        }
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 USER_ID 為 " + id + " 的用戶"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.CAN_NOT_FIND_USER));
    }

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public Page<User> search(UserSearchRequest search, Pageable pageable) {
        return userRepository.searchUsers(
                search.getName(),
                search.getAccount(),
                search.getEmail(),
                search.getRole(),
                search.getIsActive(),
                search.getCreationDateStart(),
                search.getCreationDateEnd(),
                search.getEditDateStart(),
                search.getEditDateEnd(),
                pageable);
    }

    public boolean isAccountTaken(String account) {
        return userRepository.existsByAccount(account);
    }

    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 管理員新增或編輯使用者
     */
    @Transactional
    public User saveByAdmin(AdminUserSaveRequest request) {
        User user = request.id() == null ? new User() : findById(request.id());
        user.setName(Objects.requireNonNullElse(request.name(), ""));
        user.setEmail(Objects.requireNonNullElse(request.email(), ""));
        user.setRole(Objects.requireNonNullElse(request.role(), Roles.USER));
        user.setIsActive(Objects.requireNonNullElse(request.isActive(), false));
        return userRepository.save(user);
    }

    @Transactional
    public void rename(Integer userId, String name) {
        findById(userId).setName(name.trim());
    }

    @Transactional
    public void changeAvatar(Integer userId, String avatar) {
        findById(userId).setAvatar(avatar);
    }

    @Transactional
    public void removeAvatar(Integer userId) {
        findById(userId).setAvatar("");
    }

    @Transactional
    public void changePassword(Integer userId, String rawPassword) {
        findById(userId).setPassword(passwordEncoder.encode(rawPassword));
    }

    public boolean passwordMatches(Integer userId, String rawPassword) {
        return passwordEncoder.matches(rawPassword, findById(userId).getPassword());
    }

    @Transactional
    public void activateAccount(String activationCode) {
        User user = userRepository.findByActivationCode(activationCode)
                .orElseThrow(() -> new EntityNotFoundException("信箱驗證碼錯誤"));
        user.setIsActive(true);
        user.setActivationCode(null);
    }
}
