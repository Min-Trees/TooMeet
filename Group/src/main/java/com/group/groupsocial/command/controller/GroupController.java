package com.group.groupsocial.command.controller;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.entity.PostModel;
import com.group.groupsocial.command.repository.GroupRepository;
import com.group.groupsocial.command.response.GroupResponse;
import com.group.groupsocial.command.response.PostResponse;
import com.group.groupsocial.command.service.GroupService;
import com.group.groupsocial.command.service.ImageUpload;
import com.group.groupsocial.command.service.MemberService;
import com.group.groupsocial.command.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import com.group.groupsocial.command.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    @Autowired
    private GroupRepository groupRepository;
    private final ImageUpload imageUpload;

    //    config
    @Autowired
    RestTemplate restTemplate;
    @Getter
    @Autowired
    private final PostService postService;
    @Value("${user.info.url}")
    private String url;
    public User fetchDataFromExternalService(Long userId) {
        if (userId == null) {
            userId = 1L; // Gán userId mặc định là 1 (hoặc bất kỳ giá trị mặc định nào bạn muốn)
        }
        return restTemplate.getForObject(url+userId.toString(), User.class);
    }

    @GetMapping("/{groupId}")
    public ResponseEntity<?> getGroup(@PathVariable UUID groupId){

        if (groupResponse != null) {
            GroupModel groupModel = groupService.getGroupById(groupId);
            User user = fetchDataFromExternalService(groupModel.getAdmin());
            GroupResponse groups = new GroupResponse();
            groups.setGroupId(groupModel.getGroupId());
            groups.setName(groups.getName());
            groups.setAvatar(groupModel.getAvatar());
            groups.setAdmin(user);
            groups.setDescription(groupModel.getDescription());
            groups.setQuantityMember(groupModel.getQuantityMember());
            groups.setCreatedAt(groupModel.getCreatedAt());
            groups.setUpdatedAt(groupModel.getUpdatedAt());
            return ResponseEntity.ok(groups);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
    @PostMapping("")
    public GroupResponse newGroup(
            @RequestHeader(value = "x-user-id") Long userId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("avatar") MultipartFile avatar,
            HttpServletRequest request) throws IOException {

        String filename = UUID.randomUUID().toString() + "_" + avatar.getOriginalFilename();
        Map uploadResult = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("public_id", "groups/" + filename));
        String avatarUrl = (String) uploadResult.get("url");

        // Sử dụng userId lấy từ header
        User user = fetchDataFromExternalService(userId);

        GroupModel groupModel = new GroupModel();
        groupModel.setName(name);
        groupModel.setAvatar(avatarUrl);
        groupModel.setAdmin(userId);
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
        groups.setAdmin(user);
        groups.setDescription(description);
        groups.setQuantityMember(totalMember);
        groups.setCreatedAt(groupModel.getCreatedAt());
        groups.setUpdatedAt(groupModel.getUpdatedAt());
        return groups;
    }

//    post to group
    @PostMapping("/{groupId}/post")
    public PostResponse newPost(
            @RequestHeader(value = "x-user-id") Long userId,
            @PathVariable("groupId") UUID groupId,
            @RequestParam("memberId") UUID memberId,
            @RequestParam("content") String content,
            @RequestParam("images") List<MultipartFile> images,
            HttpServletRequest request) throws IOException{
        User user = fetchDataFromExternalService(userId);
        PostModel postModel = new PostModel();
        postModel.setGroupId(groupId);
        postModel.setMemberId(memberId);
        postModel.setContent(content);
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image:images){
            try {
                String imageUrl= imageUpload.uploadImage(image);
                imageUrls.add(imageUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        postModel.setImages(imageUrls);
        postModel.setCreateAt(postModel.getCreateAt());
        postModel.setUpdatedAt(postModel.getUpdatedAt());
        PostModel post = postService.newpost(postModel);
        PostResponse postResponse = new PostResponse();
        postResponse.setGroupId(groupId);
        postResponse.setPostId(postModel.getPostId());
        postResponse.setMemberId(memberId);
        postResponse.setUser(user);
        postResponse.setStatus(String.valueOf(postModel.getStatus()));
        postResponse.setImages(imageUrls);
        postResponse.setContent(content);
        return postResponse;
    }


// update status post.status = pending to post.status = accepted
    @PutMapping("/{groupId}/{postId}")
    public ResponseEntity<?> updatePost(
            @PathVariable("groupId") UUID groupId,
            @PathVariable("postId") UUID postId,
            @RequestParam("status") String status,
            HttpServletRequest request) throws IOException {

            PostModel postModel = postService.getPostById(postId);
            if (postModel == null) {
                String message = "Không tìm thấy nhóm với ID: " + groupId;
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
            }
            postModel.setStatus(PostModel.Choice.valueOf(status));
            postModel.setUpdatedAt(postModel.getUpdatedAt());
            postService.updatePost(postModel);
            User user = fetchDataFromExternalService(postModel.getUserId());
            PostResponse postResponse = new PostResponse();
            postResponse.setGroupId(postModel.getGroupId());
            postResponse.setPostId(postModel.getPostId());
            postResponse.setContent(postModel.getContent());
            postResponse.setImages(postModel.getImages());
            postResponse.setStatus(String.valueOf(postModel.getStatus()));
            postResponse.setUser(user);
            postResponse.setUpdatedAt(postModel.getUpdatedAt());
            postResponse.setMemberId(postModel.getMemberId());
            return ResponseEntity.ok(postResponse);

    }
    private File convertMultipartFileToFile(String file) throws IOException {
        return null;
    }
    private UUID getUserId() {
        return null;
    }
    @PutMapping("/{groupId}")
    public ResponseEntity<?> updateGroup(
            @RequestHeader(value = "x-user-id") Long userId,
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
        groupResponse.setAdmin(user);
        groupResponse.setDescription(description);
        groupResponse.setCreatedAt(groupModel.getCreatedAt());
        groupResponse.setUpdatedAt(groupModel.getUpdatedAt());

        return ResponseEntity.ok(groupResponse);
    }

    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable UUID groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok("Nhóm đã được xóa thành công");
    }
    @GetMapping
    public Page<GroupResponse> getAllGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<GroupModel> listGroup = groupRepository.findAll(PageRequest.of(page, size));

        return listGroup.map((groupModel -> {
            User user = fetchDataFromExternalService(groupModel.getAdmin());
            GroupResponse groupResponse = new GroupResponse();
            groupResponse.setGroupId(groupModel.getGroupId());
            groupResponse.setName(groupModel.getName());
            groupResponse.setAvatar(groupModel.getAvatar());
            groupResponse.setAdmin(user);
            groupResponse.setDescription(groupModel.getDescription());
            groupResponse.setCreatedAt(groupModel.getCreatedAt());
            groupResponse.setUpdatedAt(groupModel.getUpdatedAt());
            return groupResponse;
        }));
    }

    @PutMapping("/updateadmin/{groupId}")
    public ResponseEntity<?> updateAdmin(
            @RequestHeader(value = "x-user-id") Long userId,
            @PathVariable UUID groupId,
            @RequestParam("admin") Long admin,
            HttpServletRequest request) throws IOException{
        User user = fetchDataFromExternalService(userId);
        GroupModel groupModel = groupService.getGroupById(groupId);
        groupModel.setAdmin(admin);
        groupService.updateGroup(groupModel);
        GroupResponse groupResponse = new GroupResponse();
        groupResponse.setGroupId(groupId);
        groupResponse.setName(groupModel.getName());
        groupResponse.setAvatar(groupModel.getAvatar());
        groupResponse.setAdmin(user);
        groupResponse.setDescription(groupModel.getDescription());
        groupResponse.setCreatedAt(groupModel.getCreatedAt());
        groupResponse.setUpdatedAt(groupModel.getUpdatedAt());
        return ResponseEntity.ok(groupResponse);
    }
}
