package com.group.groupsocial.command.service;

import com.group.groupsocial.command.entity.PostModel;
import com.group.groupsocial.command.entity.User;
import com.group.groupsocial.command.repository.PostRespository;
import com.group.groupsocial.command.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.group.groupsocial.command.entity.PostModel.Choice.accepted;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRespository postRespository;
    public PostModel newpost(PostModel post){
        return postRespository.save(post);
    }

    public PostResponse getDetailPost(UUID postId) {
        PostModel postModel = postRespository.findById(postId).orElse(null);
        SimpleJpaRepository<User, Long> userRepository = null;

        if( postModel != null){
            PostResponse postResponse = new PostResponse();
            postResponse.setGroupId(postModel.getGroupId());
            postResponse.setPostId(postModel.getPostId());
            postResponse.setContent(postModel.getContent());
            postResponse.setImages(postModel.getImages());
            postResponse.setContent(postModel.getContent());
            postResponse.setStatus(String.valueOf(postModel.getStatus()));
            postResponse.setMemberId(postModel.getMemberId());
            postResponse.setUpdatedAt(postModel.getUpdatedAt());
            User user = userRepository.findById(postModel.getUserId()).orElse(null);
            postResponse.setUser(user);
            return postResponse;
        }
        return null;
    }

    public PostModel getPostById(UUID postId) {
        return postRespository.findById(postId).orElse(null);
    }

    public void updatePost(PostModel postModel) {
    }
}
