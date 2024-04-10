package com.group.groupsocial.command.service;

import com.group.groupsocial.command.entity.GroupModel;
import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.repository.GroupRepository;
import com.group.groupsocial.command.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final GroupRepository groupRepository;

    public MemberService(MemberRepository memberRepository, GroupRepository groupRepository) {
        this.memberRepository = memberRepository;
        this.groupRepository = groupRepository;
    }

    // Các phương thức khác của MemberService

    // Định nghĩa phương thức getAllGroupsByMember để trả về danh sách tất cả các nhóm mà một thành viên đã tham gia
    public List<GroupModel> getAllGroupsByUserId(Long userId) {
        // Lấy danh sách các MemberModel dựa trên userId
        List<MemberModel> members = memberRepository.findByUserId(userId);

        // Tạo danh sách groupId từ danh sách các MemberModel
//        List<UUID> groupIds = new ArrayList<>();
//        for (MemberModel member : members) {
//            groupIds.add(member.getGroup());
//        }


        // Truy vấn danh sách các nhóm từ groupRepository
//        return groupRepository.findByGroupIdIn(members);
        return members.stream().map(MemberModel::getGroup).toList();
    }


    public MemberModel newMember(MemberModel memberModel) {
        return memberRepository.save(memberModel);
    }
}
