package com.group.groupsocial.command.service;
import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.repository.GroupRepository;
import com.group.groupsocial.command.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    public GroupModel newGroup(GroupModel group){
        return groupRepository.save(group);
    }
    public int getMemberQuantity(UUID groupId) {
        return memberRepository.countMember(groupId);
    }

    public List<MemberModel> getAllMember(){
        return memberRepository.findAll();
    }
}
