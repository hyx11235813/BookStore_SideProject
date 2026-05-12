package yixian.website.service;

import jakarta.transaction.Transactional;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import yixian.website.common.Result;
import yixian.website.common.SuccessMessages;
import yixian.website.dto.UserDto;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.exception.ServiceException;
import yixian.website.model.Role;
import yixian.website.model.Users;
import yixian.website.repository.RoleRepository;
import yixian.website.repository.UserRepository;
import yixian.website.service.query.UserQueryService;
import yixian.website.util.CommonUtils;
import yixian.website.util.RandomUtil;

@Service
public class UserManagementService {
    public static final Boolean inactive = false;
    public static final Boolean active = true;
    private final UserRepository userRepository;
    private final UserQueryService userQueryService;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public UserManagementService(UserRepository userRepository, UserQueryService userQueryService1, RoleRepository roleRepository, EmailService emailService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userQueryService = userQueryService1;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public Result saveOrUpdateUser(Users users) {
        Users existingUser = getOrCreateUser(users.getId());
        setUserDetails(existingUser, users);
        userRepository.save(existingUser);

        return Result.saveSuccess(null, SuccessMessages.SAVE_OK);
    }

    private Users getOrCreateUser(Integer id) {
        if (id == null) {
            return new Users();
        }

        return userRepository.findById(id).orElse(new Users());
    }

    private void setUserDetails(Users existingUser, Users user) {
        existingUser.setName(CommonUtils.defaultIfNull(user.getName(), ""));
        existingUser.setEmail(CommonUtils.defaultIfNull(user.getEmail(), ""));
        existingUser.setRole(CommonUtils.defaultIfNull(user.getRole(), ""));
        existingUser.setIsActive(CommonUtils.defaultIfNull(user.getIsActive(), false));
        existingUser.setCreationDate(CommonUtils.defaultIfNull(user.getCreationDate(), null));
    }

    public void setUserName(UserDto dto) {
        Users user = userQueryService.findUserById(dto.getId());
        user.setName(dto.getName());
        userRepository.save(user);
    }


    private void setDefaultValues(Users user) {
        if (user.getRole() == null) {
            user.setRole("USER");
        }
    }

    public void updateUserAvatar(UserDto userDto) {
        Users user = userQueryService.findUserById(userDto.getId());
        user.setAvatar(userDto.getAvatar());
        userRepository.save(user);
    }

    public void removeUserAvatar(Integer id) {
        Users user = userQueryService.findUserById(id);
        user.setAvatar("");
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(UserDto userDto) {
        Users user = userQueryService.findUserById(userDto.getId());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
    }

    @Transactional
    protected void saveUser(Users user) {
        setDefaultValues(user);
        try {
            Users savedUser = userRepository.save(user);
            setUserRole(user, savedUser);
            if (inactive.equals(user.getIsActive())) {
                sentMail(savedUser);
            }
        } catch (MailException e) {
            throw new ServiceException("驗證信箱發送失敗", e);
        } catch (Exception e) {
            throw new ServiceException("保存用戶失敗", e);
        }
    }

    private void setUserRole(Users user, Users savedUser) {
        Role role = (user.getUser_Role() != null) ? user.getUser_Role() : new Role();
        role.setRole(user.getRole());
        role.setUser(savedUser);
        roleRepository.save(role);
    }

    private void sentMail(Users user) {
        String activationCode = generateActivationCode();
        user.setActivationCode(activationCode);   //設置開通碼，並發送驗證信，用戶點擊驗證信後比對開通碼來開通用戶
        emailService.sendActivationEmail(user.getEmail(), activationCode);//發送驗證信
    }

    private String generateActivationCode() {
        return RandomUtil.generateToken();
    }

    @Transactional
    public void activateAccount(String activationCode) {
        Users users = userRepository.findUserByActivationCode(activationCode)
                .orElseThrow(() -> new EntityNotFoundException("信箱驗證碼錯誤"));
        if (!active.equals(users.getIsActive())) {
            users.setIsActive(active);
            userRepository.save(users);
            deactivateActivateCode(users.getId());
        }
    }

    public void deactivateActivateCode(long userId) {
        userRepository.deactivateActivationCode(userId);
    }

}
