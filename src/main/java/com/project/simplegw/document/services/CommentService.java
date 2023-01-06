package com.project.simplegw.document.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.simplegw.document.dtos.receive.DtorCommentSave;
import com.project.simplegw.document.dtos.send.DtosComment;
import com.project.simplegw.document.entities.Comment;
import com.project.simplegw.document.entities.Docs;
import com.project.simplegw.document.helpers.DocsConverter;
import com.project.simplegw.document.repositories.CommentRepo;
import com.project.simplegw.document.vos.DocsType;
import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.member.services.MemberService;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.services.CommentNotificationService;
import com.project.simplegw.system.vos.ServiceMsg;
import com.project.simplegw.system.vos.ServiceResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class CommentService {
    private final CommentRepo commentRepo;
    private final DocsService docsService;
    private final DocsConverter docsConverter;
    private final MemberService memberService;
    private final CommentNotificationService notiService;

    @Autowired
    public CommentService(CommentRepo commentRepo, DocsService docsService, DocsConverter docsConverter, MemberService memberService, CommentNotificationService notiService) {
        this.commentRepo = commentRepo;
        this.docsService = docsService;
        this.docsConverter = docsConverter;
        this.memberService = memberService;
        this.notiService = notiService;

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }




    public List<DtosComment> getComments(Long docsId) {
        return commentRepo.findByDocsIdOrderById(docsId).stream().map(docsConverter::getDtosComment).collect(Collectors.toList());
    }


    public ServiceMsg save(DocsType docsType, Long docsId, DtorCommentSave dto, LoginUser loginUser) {
        Docs docs = docsService.getDocsEntity(docsId, docsType);

        if(docs == null || docs.getId() == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("댓글을 작성할 대상 문서가 없습니다.");
        
        if(docs.getType() == DocsType.ARCHIVE)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("댓글을 작성할 수 있는 문서가 아닙니다.");

        MemberData memberData =  memberService.getMemberData(loginUser);
        Comment comment = Comment.builder().comment(dto.getComment()).build().bindDocs(docs).setMemberData(memberData);

        try {
            commentRepo.save(comment);
            if( ! docs.getWriterId().equals( loginUser.getMember().getId() ))   // 게시글 작성자 본인이 댓글을 쓰면 알림을 주지 않는다.
                notiService.create(docs);

            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("save exception.");
            log.warn("parameters: {}, {}", dto.toString(), memberData.toString());
            log.warn("Docs value: {}", docs.toString());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("댓글 작성시 에러입니다. 관리자에게 문의하세요.");
        }
    }


    public ServiceMsg delete(Long id, LoginUser loginUser) {
        Optional<Comment> target = commentRepo.findById(id);

        if(target.isPresent()) {
            Comment comment = target.get();

            if( !comment.getWriterId().equals(loginUser.getMember().getId()) )
                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("다른 사용자가 작성한 댓글은 삭제할 수 없습니다.");

            commentRepo.delete(comment);
            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } else {
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("댓글이 이미 삭제되었습니다. 새로고침 하세요.");
        }
    }
}
