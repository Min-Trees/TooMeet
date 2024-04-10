package com.group.groupsocial.command.repository;
import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.response.GroupResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface GroupRepository extends JpaRepository<GroupModel,UUID> {


    void deleteById(UUID groupId);

    List<GroupModel> findByGroupIdIn(List<UUID> groupIds);
}
