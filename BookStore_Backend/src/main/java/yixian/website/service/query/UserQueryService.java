package yixian.website.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import yixian.website.common.ErrorMessages;
import yixian.website.dto.PageRequestDto;
import yixian.website.dto.SearchUserRequestDto;
import yixian.website.exception.EntityNotFoundException;
import yixian.website.exception.InvalidDataException;
import yixian.website.model.Users;
import yixian.website.repository.UserRepository;

@Service
public class UserQueryService {
    private final UserRepository userRepository;

    public UserQueryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<Users> findUsersByPage(PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestDto.toPageable();
        return userRepository.findAll(pageable);
    }

    public Page<Users> getUserByParams(SearchUserRequestDto searchUserRequestDto, PageRequestDto pageRequestDto) {
        Pageable pageable = pageRequestDto.toPageable();
        return userRepository.findUserByParams(
                searchUserRequestDto.getName(),
                searchUserRequestDto.getAccount(),
                searchUserRequestDto.getEmail(),
                searchUserRequestDto.getRole(),
                searchUserRequestDto.getIsActive(),
                searchUserRequestDto.getCreationDateStart(),
                searchUserRequestDto.getCreationDateEnd(),
                searchUserRequestDto.getEditDateStart(),
                searchUserRequestDto.getEditDateEnd(),
                pageable);

    }

    public Users findUserById(Integer id) {
        if (id == null) {
            throw new InvalidDataException(ErrorMessages.PARAM_CANNOT_BE_NULL, " [Method: findUserById, Param: id]");
        }
        return userRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("找不到 USER_ID 為 " + id + " 的用戶"));
    }

    public Users findUserByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessages.CAN_NOT_FIND_USER));
    }

    public boolean isAccountExists(String account) {
        return userRepository.existsByAccount(account);
    }

    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }


}
