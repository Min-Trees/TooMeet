package com.group.groupsocial.command.service;

import com.group.groupsocial.command.entity.MemberModel;
import com.group.groupsocial.command.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Member;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Page<MemberModel> getAllMemberByGroupId(UUID groupId , Pageable pageable){
        return memberRepository.findByGroupId(groupId, pageable);
    }

    public MemberModel newMember(MemberModel member){
        return memberRepository.save(member);
    }
    public MemberModel MemberByGroupId(MemberModel member){
        return memberRepository.save(member);
    }

    public MemberModel joinGroup(UUID memberId, UUID groupId){
        Optional<MemberModel> existingMember = memberRepository.findById(memberId);
        if (existingMember.isPresent()) {
            MemberModel member = existingMember.get();
            member.setGroupId(groupId);
            return memberRepository.save(member);
        } else {
            throw new RuntimeException("Member not found with ID: " + memberId);
        }
    }
    }


