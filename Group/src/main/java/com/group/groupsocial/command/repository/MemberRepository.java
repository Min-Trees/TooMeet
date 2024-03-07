package com.group.groupsocial.command.repository;

import com.group.groupsocial.command.entity.MemberModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MemberRepository extends JpaRepository<MemberModel, UUID> {
    int countMember(UUID groupId);
}
