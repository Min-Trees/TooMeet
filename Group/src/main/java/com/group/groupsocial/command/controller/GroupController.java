package com.group.groupsocial.command.controller;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.entity.PostModel;
import com.group.groupsocial.command.mesage.PostMessage;
import com.group.groupsocial.command.mesage.PostMessageAccepted;
import com.group.groupsocial.command.repository.GroupRepository;
import com.group.groupsocial.command.response.GroupResponse;
import com.group.groupsocial.command.response.PostResponse;
import com.group.groupsocial.command.service.*;
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
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private AMQPService amqpService;

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
            groups.setName(groupModel.getName());
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

        // Tạo mới một nhóm và lấy groupId
        GroupModel groupModel = new GroupModel();
        // Không cần gán groupId ở đây
        groupModel.setName(name);
        groupModel.setAvatar(avatarUrl);
        groupModel.setAdmin(userId);
        groupModel.setDescription(description);
        groupModel.setAdmin(userId);
        groupModel.setQuantityMember(1);
        groupModel.setCreatedAt(groupModel.getCreatedAt());
        groupModel.setUpdatedAt(groupModel.getUpdatedAt());
        groupModel.setMemberList(new ArrayList<>());

        MemberModel memberModel = new MemberModel();
        memberModel.setUserId(userId);
        memberModel.setRole(MemberModel.Role.ADMIN);
        memberModel.setGroup(groupModel);
        groupModel.getMemberList().add(memberModel);

        GroupModel group = groupService.newGroup(groupModel);
        UUID groupId = group.getGroupId();

        GroupResponse groups = new GroupResponse();
        groups.setGroupId(groupId); // Sử dụng groupId của nhóm mới
        groups.setName(name);
        groups.setAvatar(avatarUrl);
        groups.setAdmin(user);
        groups.setDescription(description);
        groups.setQuantityMember(group.getQuantityMember());
        groups.setCreatedAt(group.getCreatedAt());
        groups.setUpdatedAt(group.getUpdatedAt());
        return groups;
    }


//    post to group
    @PostMapping("/{groupId}/post")
    public PostResponse newPost(
            @RequestHeader(value = "x-user-id") Long userId,
            @PathVariable("groupId") UUID groupId,
            @RequestParam("content") String content,
            @RequestParam("images") List<MultipartFile> images,
            HttpServletRequest request) throws IOException{
        User user = fetchDataFromExternalService(userId);
        PostModel postModel = new PostModel();
        postModel.setGroupId(groupId);
        postModel.setMemberId(userId);
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

        PostMessage postMessage = new PostMessage();
        postMessage.setContent(postModel.getContent());
        postMessage.setImages(postModel.getImages());
        postMessage.setUserId(userId);
        postMessage.setGroupId(postModel.getGroupId());
        amqpService.sendMessage(postMessage);

        PostResponse postResponse = new PostResponse();
        postResponse.setGroupId(groupId);
        postResponse.setPostId(postModel.getPostId());
        postResponse.setMemberId(userId);
        postResponse.setUser(user);
        postResponse.setStatus(String.valueOf(postModel.getStatus()));
        postResponse.setImages(imageUrls);
        postResponse.setContent(content);
        return postResponse;
    }

// update status post.status = pending to post.status = accepted
    @PostMapping("/{postId}")
    public ResponseEntity<?> updateStatusPost(
            @RequestHeader("x-user-id") Long userId,
            @PathVariable("postId") UUID postId,
            @RequestParam("groupId") UUID groupId,
            HttpServletRequest request) throws IOException {
            GroupModel groupModel = groupService.getGroupById(groupId);
            Long admin = groupModel.getAdmin();
            if(Objects.equals(admin, userId)) {
                PostMessageAccepted postMessage = new PostMessageAccepted();
                postMessage.setGroupId(groupId);
                postMessage.setPostId(postId);
                amqpService.sendMessageAccepted(postMessage);
                PostResponse postResponse = new PostResponse();
                postResponse.setPostId(postId);
                return ResponseEntity.ok(postResponse);
            }
            String message = "user not admin";
        return ResponseEntity.badRequest().body(message);                                                                                      
    }
    private File convertMultipartFileToFile(String file) throws IOException {
        return null;
    }
    private UUID getUserId() {
        return null;
    }
//    @PutMapping("/{groupId}")
//    public ResponseEntity<?> updateGroup(
//            @RequestHeader(value = "x-user-id") Long userId,
//            @PathVariable UUID groupId,
//            @RequestParam("name") String name,
//            @RequestParam("description") String description,
//            @RequestParam("avatar") MultipartFile avatar,
//            HttpServletRequest request) throws IOException {
//
//        String filename = UUID.randomUUID().toString() + "_" + avatar.getOriginalFilename();
//        Map uploadResult = cloudinary.uploader().upload(avatar.getBytes(), ObjectUtils.asMap("public_id", "groups/" + filename));
//        String avatarUrl = (String) uploadResult.get("url");
//
//        // Sử dụng userId lấy từ header
//        User user = fetchDataFromExternalService(userId);
//
//        GroupModel groupModel = groupService.getGroupById(groupId);
//        if (groupModel == null) {
//            String message = "Không tìm thấy nhóm với ID: " + groupId;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
//        }
//        groupModel.setName(name);
//        groupModel.setAvatar(avatarUrl);
//        groupModel.setDescription(description);
//        groupService.updateGroup(groupModel);
//
//        GroupResponse groupResponse = new GroupResponse();
//        groupResponse.setGroupId(groupId);
//        groupResponse.setName(name);
//        groupResponse.setAvatar(avatarUrl);
//        groupResponse.setAdmin(user);
//        groupResponse.setDescription(description);
//        groupResponse.setQuantityMember(groupModel.getQuantityMember());
//        groupResponse.setCreatedAt(groupModel.getCreatedAt());
//        groupResponse.setUpdatedAt(groupModel.getUpdatedAt());
//
//        return ResponseEntity.ok(groupResponse);
//    }

    @DeleteMapping("/groups/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable UUID groupId) {
        groupService.deleteGroup(groupId);
        return ResponseEntity.ok("Nhóm đã được xóa thành công");
    }
    @GetMapping
    public Page<GroupResponse> getAllGroups(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        Page<GroupModel> listGroup = groupRepository.findAll(PageRequest.of(page, limit));

        return listGroup.map((groupModel -> {
            User user = fetchDataFromExternalService(groupModel.getAdmin());
            GroupResponse groupResponse = new GroupResponse();
            groupResponse.setGroupId(groupModel.getGroupId());
            groupResponse.setName(groupModel.getName());
            groupResponse.setAvatar(groupModel.getAvatar());
            groupResponse.setAdmin(user);
            groupResponse.setDescription(groupModel.getDescription());
            groupResponse.setQuantityMember(groupModel.getQuantityMember());
            groupResponse.setCreatedAt(groupModel.getCreatedAt());
            groupResponse.setUpdatedAt(groupModel.getUpdatedAt());
            return groupResponse;
        }));
    }

// need update ***************
    @GetMapping("/{groupId}/{userId}")
    public ResponseEntity<?> MemberCheck(
            @PathVariable("groupId") UUID groupId,
            @RequestHeader(value = "x-user-id") Long userId,
            HttpServletRequest request) throws IOException{
        GroupModel groupModel = groupService.getGroupById(groupId);
        User user = fetchDataFromExternalService(userId);
        String message;

        if(groupModel!= null){
            return ResponseEntity.ok(GroupResponse.convert(groupModel,user));
        }
        else{
            message = "member is not exist group";
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(message);
    }
    @PostMapping("/{userId}/groups")
    public ResponseEntity<List<GroupResponse>> getGroupsOfMember(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {

        List<GroupModel> userGroups = memberService.getAllGroupsByUserId(userId);

        List<GroupResponse> groupResponses = userGroups.stream()
                .map(groupModel -> {
                    GroupResponse groupResponse = new GroupResponse();
                    groupResponse.setGroupId(groupModel.getGroupId());
                    groupResponse.setAvatar(groupModel.getAvatar());
                    groupResponse.setName(groupModel.getName());
                    return groupResponse;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(groupResponses);
    }


}

