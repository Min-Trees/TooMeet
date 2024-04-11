package com.group.groupsocial.command.controller;

import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.entity.User;
import com.group.groupsocial.command.repository.GroupRepository;
import com.group.groupsocial.command.repository.MemberRepository;
import com.group.groupsocial.command.request.MemberRequest;
import com.group.groupsocial.command.response.GroupResponse;
import com.group.groupsocial.command.response.MemberResponse;
import com.group.groupsocial.command.service.GroupService;
import com.group.groupsocial.command.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;



// admin out -> bat buoc phai co admin tiep theo
// orderby member --- pass
// 0 user > delete group
// response data group JPA format --- pass
// created , updated group --- pass


// message queue -> notification service
// update groupName, change admin ( notification )
// update group -> (message -> socket -> AMQP)
// accepted post -> message -> ( socket, postService)

@RestController
@RequiredArgsConstructor
@RequestMapping("/group/member")
public class MemberController {
    private final MemberService memberService;
    private final RestTemplate restTemplate;
    private final MemberRepository memberRepository;
    private final GroupService groupService;
    private final String url;
    private final GroupRepository groupRepository;

    @Autowired
    public MemberController(MemberService memberService, MemberRepository memberRepository, RestTemplate restTemplate, GroupService groupService, @Value("${user.info.url}") String url, GroupRepository groupRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.restTemplate = restTemplate;
        this.groupService = groupService;
        this.url = url;
        this.groupRepository = groupRepository;
    }

    public User fetchDataFromExternalService(Long userId) {
        return restTemplate.getForObject(url + userId, User.class);
    }

    @PostMapping("/{groupId}")
    public MemberResponse newMember(@RequestBody MemberRequest memberRequest,
                                    @RequestHeader(value = "x-user-id") Long userId,
                                    @PathVariable("groupId") UUID newGroupId,
                                    HttpServletRequest request) throws IOException {
        GroupModel groupModel = groupService.getGroupById(newGroupId);
        User user = fetchDataFromExternalService(groupModel.getAdmin());

        MemberModel memberModel = new MemberModel();
        memberModel.setUserId(userId);
        memberModel.setRole(MemberModel.Role.USER);
        memberModel.setGroup(groupModel);

        Integer quantityMember = groupModel.getQuantityMember();
        if (quantityMember != null) {
            groupModel.setQuantityMember(quantityMember + 1);
        } else {
            groupModel.setQuantityMember(1);
        }
        groupRepository.save(groupModel);
        MemberResponse memberResponse = new MemberResponse();
        memberResponse.setGroup(GroupResponse.convert(groupModel,user));
        memberResponse.setUser(fetchDataFromExternalService(userId));
        memberResponse.setRole(memberModel.getRole().toString());

        return memberResponse;
    }

    @GetMapping("/{groupId}")
    public Page<MemberResponse> getMember(
            @PathVariable UUID groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        Page<MemberModel> memberList = memberRepository.findByGroupGroupId(groupId, PageRequest.of(page, limit));
        return memberList.map((memberModel -> {
            GroupModel groupModel = groupService.getGroupById(groupId);
            if (groupModel != null) {
                User user = fetchDataFromExternalService(groupModel.getAdmin());
                MemberResponse memberResponse = new MemberResponse();
                memberResponse.setGroup(GroupResponse.convert(groupModel,user));
                GroupResponse groupResponse = new GroupResponse();
                memberResponse.setRole(memberModel.getRole().name());
                memberResponse.setUser(user);
                return memberResponse;
            } else {
                return null;
            }
        }));
    }
}

//admin -> duyet , xoa