package com.group.groupsocial.command.repository;

import com.group.groupsocial.command.entity.MemberId;
import com.group.groupsocial.command.entity.MemberModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Component
public interface MemberRepository extends JpaRepository<MemberModel, Long> {
    @Query("SELECT COUNT(m) FROM MemberModel m WHERE  m.group.groupId = :groupId")
    int countMember(@Param("groupId") UUID groupId);

    Page<MemberModel> findByGroupGroupId(UUID groupId, Pageable pageable);

    List<MemberModel> findByUserId(Long userId);
    Page<MemberModel> findByUserId(Long userId, Pageable pageable);

    default MemberId findById(MemberId memberId) {
        return memberId.withUserIdOrDefault(null); // or specify your default value
    }

    @Override
    <S extends MemberModel> S save(S entity);
}