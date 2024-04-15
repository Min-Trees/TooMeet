package com.group.groupsocial.command.response;

import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class MemberResponse {
    GroupResponse group;
    Date createAt;
    String role;
    User user;

    public interface MemberRepository extends JpaRepository<MemberModel, Long> {
        // Phương thức để lấy tất cả các MemberModel của một người dùng với phân trang
        Page<MemberModel> findByUserId(Long userId, Pageable pageable);

    }
}
