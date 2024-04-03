package com.group.groupsocial.command.service;
import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.entity.User;
import com.group.groupsocial.command.repository.GroupRepository;
import com.group.groupsocial.command.repository.MemberRepository;
import com.group.groupsocial.command.response.GroupResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GroupService {

//    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;
    @Autowired
    RestTemplate restTemplate;

    public GroupModel newGroup(GroupModel group){
        return groupRepository.save(group);
    }
    public int getMemberQuantity(UUID groupId) {
        return memberRepository.countMember(groupId);
    }

    public List<MemberModel> getAllMember(){
        return memberRepository.findAll();
    }
    private final String url= "http://user-service:8082/users/info/";
    public User fetchDataFromExternalService(Long userId) {
        if (userId == null) {
            userId = 1L; // Gán userId mặc định là 1 (hoặc bất kỳ giá trị mặc định nào bạn muốn)
        }
        return restTemplate.getForObject(url+userId.toString(), User.class);
    }

    public List<GroupResponse> getAllGroups() {
        List<GroupModel> groupModels = groupRepository.findAll();
        List<GroupResponse> groupResponses = new ArrayList<>();
        for(GroupModel x : groupModels){
            User user = fetchDataFromExternalService(x.getUserId());
            GroupResponse groupTemp = new GroupResponse();
            groupTemp.setGroupId(x.getGroupId());
            groupTemp.setName(x.getName());
            groupTemp.setDescription(x.getDescription());
            groupTemp.setAvatar(x.getAvatar());
            groupTemp.setUser(user);
            groupResponses.add(groupTemp);
        }
        return groupResponses;
    }

    public GroupResponse getDetailGroup(UUID groupId) {
        // Sử dụng GroupRepository để truy xuất thông tin về nhóm từ cơ sở dữ liệu
        // Đây chỉ là một ví dụ, bạn cần thay đổi phần này tùy theo thiết kế của ứng dụng và cơ sở dữ liệu của bạn

        GroupModel group = groupRepository.findById(groupId).orElse(null);
        SimpleJpaRepository<User, Long> userRepository = null;
        if (group != null) {
            // Tạo một đối tượng GroupResponse từ dữ liệu nhóm và trả về
            GroupResponse response = new GroupResponse();
            response.setGroupId(group.getGroupId());
            response.setName(group.getName());
            response.setDescription(group.getDescription());
            response.setUserId(group.getUserId());
            response.setAvatar(group.getAvatar());
            response.setQuantityMember(group.getQuantityMember());
            response.setCreatedAt(group.getCreatedAt());
            response.setUpdatedAt(group.getUpdatedAt());
            User user = userRepository.findById(group.getUserId()).orElse(null);
            if (user != null) {
                response.setUser(user);
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
}
