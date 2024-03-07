package com.group.groupsocial.command.controller;

import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.repository.MemberRepository;
import com.group.groupsocial.command.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {
    @Autowired
    GroupService groupService;
    @PostMapping("")
    public GroupModel newGroup(@RequestParam("name") String name,
                               @RequestParam("avatar") File avatar,
                               @RequestParam("quantityMember") Integer quantityMember) throws IOException {
        GroupModel groups = new GroupModel();
        groups.setName(name);
        groups.setAvatar(avatar);
        groups.setUserId(groups.getUserId());
        UUID groupId = groups.getGroupId();
        int totalMember = groupService.getMemberQuantity(groupId);
        groups.setQuantityMember(totalMember);
        return groupService.newGroup(groups);
    }
    // update info group
    @PutMapping("/{groupId")
    public GroupModel updateGroup(
            @RequestParam ("name") String name,
            @RequestParam ("avata") File avatar) throws IOException{
        GroupModel groups = new GroupModel();
        groups.setName(name);
        groups.setAvatar(avatar);
        return groupService.newGroup(groups);
    }

    // get member follow page and limit
    private MemberRepository memberRepository;
    @GetMapping("/member")
    public Page<MemberModel> getMember(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){
        return memberRepository.findAll(PageRequest.of(page, size));
    }

}
