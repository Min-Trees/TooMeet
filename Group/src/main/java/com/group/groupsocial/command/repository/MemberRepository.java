package com.group.groupsocial.command.repository;

import com.group.groupsocial.command.entity.MemberModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberModel, UUID> {
    @Query("SELECT COUNT(m) FROM MemberModel m WHERE  m.group.groupId = :groupId")
    int countMember(@Param("groupId") UUID groupId);

    Page<MemberModel> findByGroupGroupId(UUID groupId, Pageable pageable);

    // Sửa đổi phương thức findById để sử dụng UUID thay vì Long

    List<MemberModel> findByUserId(Long userId);

}
