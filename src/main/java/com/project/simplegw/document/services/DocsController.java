package com.project.simplegw.document.services;

import java.time.LocalDate;
import java.util.List;

import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.document.dtos.CommentDTO;
import com.project.simplegw.document.dtos.DocsDTO;
import com.project.simplegw.document.dtos.DocsOptionsDTO;
import com.project.simplegw.document.dtos.DocsSearchDTO;
import com.project.simplegw.document.dtos.DocsShareDTO;
import com.project.simplegw.document.vos.DocumentKind;
import com.project.simplegw.document.vos.DocumentType;
import com.project.simplegw.member.entities.Member;
import com.project.simplegw.system.security.SecurityUser;
import com.project.simplegw.system.services.ResponseEntityConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DocsController {

    private final DocsService docsService;
    private final DocsShareService docsShareService;

    @Autowired
    public DocsController(DocsService docsService, DocsShareService docsShareService) {
        this.docsService = docsService;
        this.docsShareService = docsShareService;
    }

    // 일반문서 유형만 검색
    private List<DocsDTO> searchBoardList(DocumentKind kind, LocalDate dateStart, LocalDate dateEnd, boolean isRegistered) {
        DocsSearchDTO searchConditions = new DocsSearchDTO();
        searchConditions.setType(DocumentType.BOARD).setKind(kind).setDateStart(dateStart).setDateEnd(dateEnd).setRegistered(isRegistered);
        return docsService.getDocsDtoList(searchConditions);
    }

    @GetMapping("/docs/count/temporary")
    public long getTemporarySavedCount(@AuthenticationPrincipal SecurityUser loginUser) {
        return docsService.getTemporarySavedCount(loginUser.getMember().getId());
    }
    
    
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Board ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- notice ----- ----- ----- ----- ----- //
    @GetMapping(path = "/notice/list-top-7")
    public List<DocsDTO> getFixedNoticeList() {
        return docsService.getFixedNoticeList();
    }

    @GetMapping(path = "/notice/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsDTO> searchNoticeList(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateEnd) {
        return searchBoardList(DocumentKind.NOTICE, dateStart, dateEnd, true);
    }

    @PostMapping(path = "/notice")
    public ResponseEntity<Object> insertNotice(@RequestBody DocsDTO docsDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        docsDTO.setMemberId(loginUser.getMember().getId());
        RequestResult result = docsService.insertAndReturnResult(docsDTO, DocumentType.BOARD, DocumentKind.NOTICE);
        return ResponseEntityConverter.getFromRequestResult(result);
    }

    @PutMapping(path = "/notice")
    public ResponseEntity<Object> updateNotice(@RequestBody DocsDTO docsDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        docsDTO.setMemberId(loginUser.getMember().getId());
        RequestResult result = docsService.updateAndReturnResult(docsDTO, DocumentType.BOARD, DocumentKind.NOTICE);
        return ResponseEntityConverter.getFromRequestResult(result);
    }

    @DeleteMapping(path = "/notice/{docsId}")
    public ResponseEntity<Object> deleteNotice(@PathVariable Long docsId, @AuthenticationPrincipal SecurityUser loginUser) {
                RequestResult result = docsService.deleteDocs(docsId, loginUser.getMember().getId(), DocumentType.BOARD, DocumentKind.NOTICE);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    // ----- ----- ----- ----- ----- notice ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- freeboard ----- ----- ----- ----- ----- //
    @GetMapping(path = "/freeboard/list-top-5")
    public List<DocsDTO> getFixedFreeboardList() {
        return docsService.getFixedFreeboardList();
    }
    
    @GetMapping(path = "/freeboard/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsDTO> searchFreeboardList(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateEnd) {
        return searchBoardList(DocumentKind.FREEBOARD, dateStart, dateEnd, true);
    }

    @PostMapping(path = "/freeboard")
    public ResponseEntity<Object> insertFreeboard(@RequestBody DocsDTO docsDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        docsDTO.setMemberId(loginUser.getMember().getId());
        RequestResult result = docsService.insertAndReturnResult(docsDTO, DocumentType.BOARD, DocumentKind.FREEBOARD);
        return ResponseEntityConverter.getFromRequestResult(result);
    }

    @PutMapping(path = "/freeboard")
    public ResponseEntity<Object> updateFreeboard(@RequestBody DocsDTO docsDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        docsDTO.setMemberId(loginUser.getMember().getId());
        RequestResult result = docsService.updateAndReturnResult(docsDTO, DocumentType.BOARD, DocumentKind.FREEBOARD);
        return ResponseEntityConverter.getFromRequestResult(result);
    }

    @DeleteMapping(path = "/freeboard/{docsId}")
    public ResponseEntity<Object> deleteFreeboard(@PathVariable Long docsId, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = docsService.deleteDocs(docsId, loginUser.getMember().getId(), DocumentType.BOARD, DocumentKind.FREEBOARD);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    // ----- ----- ----- ----- ----- freeboard ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- meeting minutes ----- ----- ----- ----- ----- //
    @GetMapping(path = "/meeting/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsDTO> searchMeetingMinutesList(@AuthenticationPrincipal SecurityUser loginUser,
        @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateEnd
    ) {
        return docsService.getMeetingMinutes(loginUser.getMember().getId(), dateStart, dateEnd);
    }

    @PostMapping(path = "/meeting")
    public ResponseEntity<Object> saveMeeting(@RequestBody DocsDTO docsDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        docsDTO.setMemberId(loginUser.getMember().getId());
        RequestResult result = docsService.insertAndReturnResult(docsDTO, DocumentType.BOARD, DocumentKind.MEETING);
        return ResponseEntityConverter.getFromRequestResult(result);
    }

    @PutMapping(path = "/meeting")
    public ResponseEntity<Object> updateMeeting(@RequestBody DocsDTO docsDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        docsDTO.setMemberId(loginUser.getMember().getId());
        RequestResult result = docsService.updateAndReturnResult(docsDTO, DocumentType.BOARD, DocumentKind.MEETING);
        return ResponseEntityConverter.getFromRequestResult(result);
    }

    @DeleteMapping(path = "/meeting/{docsId}")
    public ResponseEntity<Object> deleteMeeting(@PathVariable Long docsId, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = docsService.deleteDocs(docsId, loginUser.getMember().getId(), DocumentType.BOARD, DocumentKind.MEETING);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    // ----- ----- ----- ----- ----- meeting minutes ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- archive ----- ----- ----- ----- ----- //
    @GetMapping(path = "/archive/list", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public List<DocsDTO> searchArchiveList(@RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateStart, @RequestParam @DateTimeFormat(iso = ISO.DATE) LocalDate dateEnd) {
        return searchBoardList(DocumentKind.ARCHIVE, dateStart, dateEnd, true);
    }

    @PostMapping(path = "/archive")
    public ResponseEntity<Object> insertArchive(@RequestBody DocsDTO docsDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        docsDTO.setMemberId(loginUser.getMember().getId());
        RequestResult result = docsService.insertAndReturnResult(docsDTO, DocumentType.BOARD, DocumentKind.ARCHIVE);
        return ResponseEntityConverter.getFromRequestResult(result);
    }

    @PutMapping(path = "/archive")
    public ResponseEntity<Object> updateArchive(@RequestBody DocsDTO docsDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        docsDTO.setMemberId(loginUser.getMember().getId());
        RequestResult result = docsService.updateAndReturnResult(docsDTO, DocumentType.BOARD, DocumentKind.ARCHIVE);
        return ResponseEntityConverter.getFromRequestResult(result);
    }

    @DeleteMapping(path = "/archive/{docsId}")
    public ResponseEntity<Object> deleteArchive(@PathVariable Long docsId, @AuthenticationPrincipal SecurityUser loginUser) {
        RequestResult result = docsService.deleteDocs(docsId, loginUser.getMember().getId(), DocumentType.BOARD, DocumentKind.ARCHIVE);
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    // ----- ----- ----- ----- ----- archive ----- ----- ----- ----- ----- //


    // ----- ----- ----- ----- ----- temporary saved list ----- ----- ----- ----- ----- //
    @GetMapping(path = "/temporary/list")
    public List<DocsDTO> getTemporaryList(@AuthenticationPrincipal SecurityUser loginUser) {
        return docsService.getTemporaryList(loginUser.getMember().getId());
    }
    // ----- ----- ----- ----- ----- temporary saved list ----- ----- ----- ----- ----- //
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Board ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- content ----- ----- ----- ----- ----- //
    // 다시 확인해보니 타임리프에서 textarea에 바인딩 할 때에는 th:value가 아니라 th:text로 해야 된다.
    // textarea에 content를 바인딩 후 ckeditor로 렌더링 해도 내용이 유지됨.

    // document-view --> document-modify 이동 시 content만 별도로 리턴한다.
    // ckeditor 초기화 시간이 걸려서 ViewController에서 Model에 넣어서 한 번에 전달하면 바인딩이 안 됨.
    // content를 가져올 때 url path를 이용하면 메서드 추가하지않고 종류별로 가져올 수 있어 수정함.
    // @GetMapping(path = "/{docsKind}/content/{docsId}")
    // public String getContentForDocsUpdate(@PathVariable(name = "docsKind") String docsKind, @PathVariable(name = "docsId") Long docsId) {
    //     try {
    //         boolean isBoard = false;
    //         DocumentKind kind = DocumentKind.valueOf(docsKind.toUpperCase().replace("-", "_"));
    //         switch(kind) {
    //             case NOTICE:
    //             case FREEBOARD:
    //             case MEETING:
    //                 isBoard = true;
    //                 break;
    //             default:
    //                 break;
    //         }
    //         return docsService.getContentString(docsId, (isBoard ? DocumentType.BOARD : DocumentType.APPROVAL), kind);

    //     } catch(IllegalArgumentException e) {
    //         logger.warn("{}{}파라미터에 해당하는 enum 클래스가 없습니다.", e.getMessage(), System.lineSeparator());
    //         return "";

    //     } catch(NullPointerException e) {
    //         logger.warn("{}{}인수가 없거나 파라미터가 null 또는 공백이 입력되었습니다.", e.getMessage(), System.lineSeparator());
    //         return "";
    //     }
    // }
    // ----- ----- ----- ----- ----- content ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- comment ----- ----- ----- ----- ----- //
    @GetMapping(path = "/comments/{docsId}")
    public List<CommentDTO> getComments(@PathVariable(name = "docsId") Long docsId) {
        return docsService.findCommentsByDocsId(docsId);
    }

    @PostMapping(path = "/comments")
    public ResponseEntity<Object> insertComment(@RequestBody CommentDTO commentDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        commentDTO.setWriterId(loginUser.getMember().getId());
        RequestResult result = docsService.insertComment(commentDTO);
        return ResponseEntityConverter.getFromRequestResult(result);
    }

    @DeleteMapping(path = "/comments")
    public ResponseEntity<Object> deleteComment(@RequestBody CommentDTO commentDTO, @AuthenticationPrincipal SecurityUser loginUser) {
        Member writer = loginUser.getMember();
        commentDTO.setWriterId(writer.getId());
        RequestResult result = docsService.deleteComment(commentDTO, writer.getRole());
        return ResponseEntityConverter.getFromRequestResult(result);
    }
    // ----- ----- ----- ----- ----- comment ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- docs options ----- ----- ----- ----- ----- //
    @PostMapping(path = "/docs-options")
    public void saveDocsOptions(@RequestBody DocsOptionsDTO dto) {
        // 공지사항만 적용되어 있으므로, 공지사항만
        docsService.saveDocsOptions(dto);
    }
    @GetMapping(path = "/docs-options/{docsId}")
    public DocsOptionsDTO getDocsOptions(@PathVariable Long docsId) {
        return docsService.getDocsOptions(docsId);
    }
    // ----- ----- ----- ----- ----- docs options ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- docs share ----- ----- ----- ----- ----- //
    @PostMapping(path = "/docs/share")
    public void docsShare(@RequestBody DocsShareDTO dto) {
        docsShareService.saveReferrer(dto);
    }
    // ----- ----- ----- ----- ----- docs share ----- ----- ----- ----- ----- //



    // 관리자 메뉴: 기간으로 문서 전체 조회
    public List<DocsDTO> findByDate(DocsSearchDTO searchConditions) {
        return docsService.findByDate(searchConditions);
    }
}
