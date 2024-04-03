package com.group.groupsocial.command.service;

import com.group.groupsocial.command.entity.PostModel;
import com.group.groupsocial.command.repository.PostRespository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRespository postRespository;
    public PostModel newpost(PostModel post){
        return postRespository.save(post);
    }
}
