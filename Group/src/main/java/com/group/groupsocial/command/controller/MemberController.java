package com.group.groupsocial.command.controller;

import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.entity.User;
import com.group.groupsocial.command.repository.MemberRepository;
import com.group.groupsocial.command.request.MemberRequest;
import com.group.groupsocial.command.response.MemberResponse;
import com.group.groupsocial.command.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
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
    @Autowired
    MemberService memberService;
    @Autowired
    RestTemplate restTemplate;

    private MemberRepository memberRepository;
    private final String url= "http://user-service:8082/users/info/";

    public User fetchDataFromExternalService(Long userId) {
        return restTemplate.getForObject(url+userId.toString(), User.class);
    }

    @PostMapping("/{groupId}")
    public MemberResponse newMember(@RequestBody MemberRequest memberRequest,
                                    @RequestHeader("user_id") Long userId,
                                    HttpServletRequest request)  throws IOException{
        MemberModel memberModel = new MemberModel();
        MemberModel.Role role = MemberModel.Role.USER;
        User user = fetchDataFromExternalService(userId);
        memberModel.setGroupId(memberRequest.getGroupId());
        memberModel.setUserId(userId);
        memberModel.setRole(role);
        MemberModel member = memberService.newMember(memberModel);
        MemberResponse members =  new MemberResponse();
        members.setGroupId(member.getGroupId());
        members.setUser(user);
        members.setRole(String.valueOf(role));
        return members;
    }

    @Autowired
    public MemberController(MemberService memberService, MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/{groupId}")
    public Page<MemberResponse> getMember(
            @PathVariable UUID groupId ,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size){

        Page<MemberModel> memberList =  memberRepository.findByGroupId(groupId, PageRequest.of(page, size));
        return memberList.map((memberModel -> {
            User user = fetchDataFromExternalService(memberModel.getUserId());
            MemberResponse memberResponse = new MemberResponse();
            memberResponse.setGroupId(memberModel.getGroupId());
            memberResponse.setRole(memberModel.getRole().name());
            memberResponse.setUser(user);
            return memberResponse;
        }));
    }
}
//admin -> duyet , xoa