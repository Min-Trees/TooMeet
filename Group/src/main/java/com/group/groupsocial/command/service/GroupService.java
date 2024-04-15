package com.group.groupsocial.command.service;
import com.group.groupsocial.command.Producer.PostProducer;
import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.entity.User;
import com.group.groupsocial.command.mesage.PostMessage;
import com.group.groupsocial.command.repository.GroupRepository;
import com.group.groupsocial.command.repository.MemberRepository;
import com.group.groupsocial.command.response.GroupResponse;
import com.group.groupsocial.command.response.GroupResponseOfUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service

public class GroupService {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    @Autowired
    private final PostProducer postProducer;
    RestTemplate restTemplate;



    @Autowired
    public GroupService(GroupRepository groupRepository, MemberRepository memberRepository, PostProducer postProducer, RestTemplate restTemplate) {
        this.groupRepository = groupRepository;
        this.memberRepository = memberRepository;
        this.postProducer = postProducer;
        this.restTemplate = restTemplate; // Inject RestTemplate
    }
    public GroupModel newGroup(GroupModel group){
        return groupRepository.save(group);
    }
    public int getMemberQuantity(UUID groupId) {
        return memberRepository.countMember(groupId);
    }
    public List<MemberModel> getAllMember(){
        return memberRepository.findAll();
    }
    @Value("${user.info.url}")
    private String url;
    public User fetchDataFromExternalService(Long userId) {
        if (userId == null) {
            userId = 1L;
        }
        return restTemplate.getForObject(url+userId.toString(), User.class);
    }
    public List<GroupResponse> getAllGroups() {
        List<GroupModel> groupModels = groupRepository.findAll();
        List<GroupResponse> groupResponses = new ArrayList<>();
        for(GroupModel x : groupModels){
            User user = fetchDataFromExternalService(x.getAdmin());
            GroupResponse groupTemp = new GroupResponse();
            groupTemp.setGroupId(x.getGroupId());
            groupTemp.setName(x.getName());
            groupTemp.setDescription(x.getDescription());
            groupTemp.setAvatar(x.getAvatar());
            groupTemp.setAdmin(user);
            groupResponses.add(groupTemp);
        }
        return groupResponses;
    }
    public GroupResponse getDetailGroup(UUID groupId) {
        GroupModel group = groupRepository.findById(groupId).orElse(null);
        SimpleJpaRepository<User, Long> userRepository = null;
        if (group != null) {
            GroupResponse response = new GroupResponse();
            User user = fetchDataFromExternalService(group.getAdmin());
            response.setGroupId(group.getGroupId());
            response.setName(group.getName());
            response.setDescription(group.getDescription());
            response.setAdmin(user);
            response.setAvatar(group.getAvatar());
            response.setQuantityMember(group.getQuantityMember());
            response.setCreatedAt(group.getCreatedAt());
            response.setUpdatedAt(group.getUpdatedAt());
            user = userRepository.findById(group.getAdmin()).orElse(null);
            if (user != null) {
                response.setAdmin(user);
            }
            return response;
        } else {
            return null;
        }
    }
    public void updateGroup(GroupModel groupModel) {
    }
    public GroupModel getGroupById(UUID groupId) {
        return groupRepository.findById(groupId).orElse(null);
    }
    public void deleteGroup(UUID groupId) {
        groupRepository.deleteById(groupId);
    }
    public void updatePostStatus(PostMessage postMessage) {
        postMessage = new PostMessage();
        postMessage.setGroupId(postMessage.getGroupId());
        postMessage.setUserId(postMessage.getUserId());
        postMessage.setContent(postMessage.getContent());
        postMessage.setImages(postMessage.getImages());
        postProducer.sendUpdatePostStatusMessage(postMessage);
    }

    public Page<GroupResponseOfUser> getGroupByUserId(int page, int limit, Long userId) {
        Pageable pageable = PageRequest.of(page, limit, Sort.by("createdAt").descending());
        Page<MemberModel> memberModels = memberRepository.findByUserId(userId, pageable);
        return memberModels.map(memberModel -> GroupResponseOfUser.convert(memberModel.getGroup(), userId));
    }
}