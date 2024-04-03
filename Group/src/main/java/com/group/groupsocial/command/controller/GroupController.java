package com.group.groupsocial.command.controller;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.entity.PostModel;
import com.group.groupsocial.command.response.GroupResponse;
import com.group.groupsocial.command.response.PostResponse;
import com.group.groupsocial.command.service.GroupService;
import com.group.groupsocial.command.service.MemberService;
import com.group.groupsocial.command.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.group.groupsocial.command.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
@RestController
@RequiredArgsConstructor
@RequestMapping("/group")
public class GroupController {
    @Autowired
    private final GroupService groupService;
    @Autowired
    private final GroupResponse groupResponse;
    @Autowired
    private Cloudinary cloudinary;
    @Autowired
    private MemberService memberService;
    //    config
    @Autowired
    RestTemplate restTemplate;
    @Getter
    @Autowired
    private final PostService postService;
    private final String url= "http://user-service:8082/users/info/";
    public User fetchDataFromExternalService(Long userId) {
        if (userId == null) {
            userId = 1L; // Gán userId mặc định là 1 (hoặc bất kỳ giá trị mặc định nào bạn muốn)
        }
        return restTemplate.getForObject(url+userId.toString(), User.class);
    }

    @GetMapping("{groupId}")
    public ResponseEntity<GroupResponse> getGroup(@PathVariable UUID groupId){

        if (groupResponse != null) {
            return ResponseEntity.ok(groupResponse);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PostMapping("")
    public GroupResponse newGroup(
            @RequestHeader("user_id") Long userId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("avatar") MultipartFile avatar,
            @RequestParam("role") Integer role,
            HttpServletRequest request) throws IOException {

        String filename = UUID.randomUUID().toString() + "_" + avatar.getOriginalFilename();
        Map uploadResult = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("public_id", "groups/" + filename));
        String avatarUrl = (String) uploadResult.get("url");

        // Sử dụng userId lấy từ header
        User user = fetchDataFromExternalService(userId);

        GroupModel groupModel = new GroupModel();
        groupModel.setName(name);
        groupModel.setAvatar(avatarUrl);
        groupModel.setUserId(userId);
        groupModel.setDescription(description);
        groupModel.setAdmin(userId);
        UUID groupId = groupModel.getGroupId();
        int totalMember = groupService.getMemberQuantity(groupId) + 1;
        GroupModel group = groupService.newGroup(groupModel);


        MemberModel memberModel = new MemberModel();
        memberModel.setUserId(userId);
        memberModel.setRole(MemberModel.Role.ADMIN);
        memberModel.setGroupId(groupModel.getGroupId());
        MemberModel member = memberService.newMember(memberModel);

        GroupResponse groups = new GroupResponse();
        groups.setGroupId(group.getGroupId());
        groups.setName(name);
        groups.setAvatar(avatarUrl);
        groups.setUser(user);
        groups.setDescription(description);
        groups.setQuantityMember(totalMember);
        return groups;
    }

//    post to group
    @PostMapping("/post")
    public PostResponse newPost(
            @RequestHeader("user_id") Long userId,
            @RequestHeader("postId") UUID postId,
            @RequestParam("groupId") UUID groupId,
            @RequestParam("memberId") UUID memberId,
            @RequestParam("status") String status,
            HttpServletRequest request) throws IOException{
        User user = fetchDataFromExternalService(userId);
        PostModel postModel = new PostModel();
        postModel.setPostId(postId);
        postModel.setGroupId(groupId);
        postModel.setMemberId(memberId);
        postModel.setStatus(PostModel.Choice.valueOf(status));
        PostModel post = postService.newpost(postModel);

        PostResponse postResponse = new PostResponse();
        postResponse.setGroupId(groupId);
        postResponse.setPostId(postId);
        postResponse.setMemberId(memberId);
        postResponse.setUser(user);
        postResponse.setStatus(status);
        return postResponse;
    }
    private File convertMultipartFileToFile(String file) throws IOException {
        return null;
    }
    private UUID getUserId() {
        return null;
    }
    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(
            @RequestHeader("user_id") Long userId,
            @PathVariable UUID groupId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("avatar") MultipartFile avatar,
            HttpServletRequest request) throws IOException {

        String filename = UUID.randomUUID().toString() + "_" + avatar.getOriginalFilename();
        Map uploadResult = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("public_id", "groups/" + filename));
        String avatarUrl = (String) uploadResult.get("url");

        // Sử dụng userId lấy từ header
        User user = fetchDataFromExternalService(userId);

        GroupModel groupModel = groupService.getGroupById(groupId);
        if (groupModel == null) {
            String message = "Không tìm thấy nhóm với ID: " + groupId;
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
        }
        groupModel.setName(name);
        groupModel.setAvatar(avatarUrl);
        groupModel.setDescription(description);
        groupService.updateGroup(groupModel);

        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setGroupId(groupId);
        groupResponse.setName(name);
        groupResponse.setAvatar(avatarUrl);
        groupResponse.setUser(user);
        groupResponse.setDescription(description);

        return ResponseEntity.ok(groupResponse);
    }

    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable UUID groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok("Nhóm đã được xóa thành công");
    }
    @GetMapping
    public List<GroupResponse> getAllGroups() {
        return groupService.getAllGroups();
    }

}
