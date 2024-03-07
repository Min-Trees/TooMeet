package com.group.groupsocial.command.repository;
import com.group.groupsocial.command.entity.GroupModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface GroupRepository extends JpaRepository<GroupModel,UUID> {

}
