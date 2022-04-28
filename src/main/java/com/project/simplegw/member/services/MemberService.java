package com.project.simplegw.member.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.project.simplegw.common.entities.Basecode;
import com.project.simplegw.common.repositories.BasecodeRepository;
import com.project.simplegw.common.vos.BasecodeType;
import com.project.simplegw.common.vos.Constants;
import com.project.simplegw.common.vos.RequestResult;
import com.project.simplegw.document.services.AttachmentsService;
import com.project.simplegw.member.dtos.MemberDTO;
import com.project.simplegw.member.dtos.MemberDTOforAdmin;
import com.project.simplegw.member.dtos.MemberInfoDTO;
import com.project.simplegw.member.dtos.PasswordDTO;
import com.project.simplegw.member.entities.Member;
import com.project.simplegw.member.entities.MemberDetails;
import com.project.simplegw.member.repositories.MemberDetailsRepository;
import com.project.simplegw.member.repositories.MemberRepository;
import com.project.simplegw.member.vos.MemberRole;
import com.project.simplegw.system.security.PwEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class MemberService {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private final MemberConverter converter;
    private final MemberRepository memberRepo;
    private final MemberDetailsRepository memberDetailsRepo;
    private final BasecodeRepository basecodeRepo;
    private final AttachmentsService attachmentsService;
    private final PwEncoder enc;

    /*
        스프링 캐시도 처리하지 않고 직접 구현
        성공 / 실패 시 호출하는 메서드 등 여러가지를 볼 때 캐시로 하려면 부가 기능을 처리하기가 어려워진다.
        내부 루틴도 있기 때문에 직접 구현으로 처리.

        멀티스레드에 안전한 멤버 디테일 ConcurrentHashMap
        유저 정보는 수시로 변경되지 않고, 또 매번 로그인할때나 데이터를 저장할 떄마다 참조해야 한다.
        빈번하게 정보가 변경되지 않는 유저 정보를 매번 DB에서 가져오는 것도 비효율적일 수 있다.
        SOHO 정도 규모의 서비스에서는 유저 정보를 서비스에서 가지고 있다가 필요할 때마다 즉시 반환해주는 게 더 좋을 수 있다.

        enabled = true 유저만 담는다.
    */
    private Map<String, Member> memberMap = new ConcurrentHashMap<>();
    private Map<Long, MemberDetails> memberDetailsMap = new ConcurrentHashMap<>();

    private static final String STRING_SYSTEM = "System";

    @Autowired
    public MemberService(
        MemberConverter converter, MemberRepository memberRepo, MemberDetailsRepository memberDetailsRepo,
        BasecodeRepository basecodeRepo, AttachmentsService attachmentsService, PwEncoder enc
    ) {
        this.converter = converter;
        this.memberRepo = memberRepo;
        this.memberDetailsRepo = memberDetailsRepo;
        this.basecodeRepo = basecodeRepo;
        this.attachmentsService = attachmentsService;
        this.enc = enc;

        setSystemAccount();
        setMemberMap();
    }

    private void setSystemAccount() {
        // 시스템 기본 사용자를 등록한다.
        // 서비스 재시작 시 중복등록하지 않기 위해서 존재하는 경우는 넘어간다.

        Optional<Member> findAdmin = memberRepo.findByUserId("admin");
        if(!findAdmin.isPresent()) {
            Member admin = Member.builder().userId("admin").password(enc.encode("admin123**!!")).role(MemberRole.ADMIN).enabled(true).build();
            MemberDetails adminDetails = MemberDetails.builder().member(admin).team(STRING_SYSTEM).jobTitle("").name("관리자").build();
            memberDetailsRepo.save(adminDetails);
            
            logger.info("시스템 관리자 계정을 생성하였습니다.");
        }

        // 제안 게시판에 익명 사용자로 게시글 작성할 때 사용하기 위해 추가.
        Optional<Member> findAnonymous = memberRepo.findByUserId("anonymous");
        if(!findAnonymous.isPresent()) {
            Member anonymous = Member.builder().userId("anonymous").password(enc.encode(UUID.randomUUID().toString())).role(MemberRole.USER).enabled(true).build();
            MemberDetails anonymousDetails = MemberDetails.builder().member(anonymous).team(STRING_SYSTEM).jobTitle("").name("익명 사용자").build();
            memberDetailsRepo.save(anonymousDetails);

            logger.info("시스템 익명 사용자 계정을 생성하였습니다.");
        }
    }

    private void setMemberMap() {
        memberDetailsRepo.findAllByRetired(false).forEach(details -> {
            Member member = details.getMember();
            memberMap.put(member.getUserId(), member);
            memberDetailsMap.put(member.getId(), details);   // Member 클래스의 id를 넣어야 한다.
        });
        logger.info("메모리에 멤버 map을 로드하였습니다.");
    }


    // ----- ----- ----- ----- ----- Searching ----- ----- ----- ----- ----- //
    // 로그인 할 때 유저 찾기.
    public Member searchByUserId(String userId) {
        // 현재 유저 맵에 있는지 찾고, 없으면 리포지터리를 이용해 가져온다.
        return memberMap.computeIfAbsent(userId, e -> memberRepo.findByUserId(e).orElseGet(Member::new));
    }

    public Member getMember(Long id) {   // memberMap에서 찾고 없으면 DB에서 찾아 리턴한다.
        // 공지사항 등록시 DocsService의 getAuthority에서 이 메서드를 호출하는데 간헐적인 NPE가 뜬다.
        // 지속적으로 발생하는 게 아니라서 현재 원인 파악이 어려워 우선 try-catch로 처리한다.

        try {
            Optional<Member> findTarget = memberMap.values().stream().filter(member -> member.getId().equals(id)).findFirst();
            if(findTarget.isPresent()) {
                return findTarget.get();
    
            } else {
                Optional<Member> result = memberRepo.findById(id);
                Member member = null;
                if(result.isPresent()) {
                    member = result.get();
                    memberMap.put(member.getUserId(), member);
                    logger.info("MemberService.getMember(Long id) method called. the memberMap key 'userId: {}, (memberId: {})' has been replaced.", member.getUserId(), Long.toString(id));
                }
                return member;
            }

        } catch(Exception e) {
            e.printStackTrace();
            logger.warn("MemberService.getMember(Long id) method exception has occurred.");

            if(id == null) {
                logger.warn("method parameter (Long id) was null.");
                return new Member();
            }
            logger.warn("parameter Long id: {}", id.toString());

            Optional<Member> result = memberRepo.findById(id);
            logger.info("MemberRepository findById method called.");

            Member member = null;
            if(result.isPresent()) {
                member = result.get();
                memberMap.put(member.getUserId(), member);
                logger.info("The memberMap key 'userId: {}, (memberId: {})' has been replaced.", member.getUserId(), id.toString());
            }
            return member;
        }
    }
    
    public List<MemberDTOforAdmin> getAllMember(boolean isRetired) {
        // 관리자 전용 메뉴
        List<MemberDTOforAdmin> userinfoList = new ArrayList<>();
        List<MemberDetails> memberDetails = null;

        if(isRetired) {
            memberDetails = memberDetailsRepo.findAllByRetired(isRetired);   // 퇴직자 리스트는 DB에서
        } else {
            // memberDetails = memberDetailsMap.values().stream().collect(Collectors.toList());   // 재직자 리스트는 초기 로딩해둔 map에서
            memberDetails = searchMemberDetailsByTeam("all");   // 재직자 리스트는 초기 로딩해둔 map에서
        }

        memberDetails.forEach(details -> {
            Member member = details.getMember();
            MemberDTOforAdmin userinfo = new MemberDTOforAdmin();
            
            userinfo.setId(member.getId()).setUserId(member.getUserId()).setRole(member.getRole()).setEnabled(member.isEnabled());
            userinfo.setTeam(details.getTeam()).setJobTitle(details.getJobTitle()).setName(details.getName()).setRetired(details.isRetired());
            
            userinfoList.add(userinfo);
        });

        return userinfoList;
    }

    public MemberDetails searchMemberDetailsById(Long memberId) {
        // getById를 사용하면 리포지터리가 프록시를 반환한다. 실제 사용전까진 DB에 접근하지 않는다.
        // 따라서 시스템 계정으로 접근하게 되면 프록시를 반환하게 되서 CustomAuthSuccessHandler 클래스에서 null 값을 이용해 컨버팅을 시도해서 Exception이 뜸.

        // getOrDefault를 사용하게 되면 키에 대응하는 밸류가 없을 경우 리턴할 값을 가지고 있어야 하므로 map에 값이 있더라도 repo를 호출한다.
        // 따라서 computeIfAbsent를 사용해야 키에 대응하는 값이 없는 경우에 값을 찾는다.

        // return memberDetailsMap.getOrDefault(memberId, memberDetailsRepo.getById(memberId));
        // return memberDetailsMap.getOrDefault(memberId, memberDetailsRepo.findById(memberId).get());

        return memberDetailsMap.computeIfAbsent(memberId, e -> memberDetailsRepo.findById(memberId).get());
    }

    public MemberInfoDTO getMemberInfo(Long memberId) {
        return converter.getDto(searchMemberDetailsById(memberId)).updateServiceDays();
    }
    
    private List<MemberDetails> searchMemberDetailsByTeam(String team) {
        Stream<MemberDetails> memberDetailsStream = memberDetailsMap.values().stream();

        if(team == null) {
            return memberDetailsStream.collect(Collectors.toList());

        } else if(team.toLowerCase().equals("all")) {
            return memberDetailsStream.sorted(Comparator.comparing(MemberDetails::getTeam).thenComparing(Comparator.comparing(MemberDetails::getName))).collect(Collectors.toList());

        } else {
            return memberDetailsStream.filter(details -> details.getTeam().equals(team)).sorted(Comparator.comparing(MemberDetails::getName)).collect(Collectors.toList());
        }
    }

    public List<MemberDTO> getTeamMembers(String team) {
        // 유저가 결재라인 설정에서 팀별 멤버 불러올 때, 임직원 현황 볼 때
        // 결재라인 설정에서는 전체 선택이 가능하므로 System 계정은 제외해야 한다.
        // Member 와 MemberDetails는 동시에 저장되고 같은 인덱스를 가지므로
        return searchMemberDetailsByTeam(team).stream().filter(e -> !e.getTeam().equals(STRING_SYSTEM) && !e.isRetired()).map(converter::getMemberDTO).collect(Collectors.toList());
    }
    public List<MemberDTO> getTeamMembers(Long memberId) {
        return getTeamMembers(searchMemberDetailsById(memberId).getTeam());
    }

    public List<MemberDTO> getDisabledIdList() {   // 미사용 계정만 조회
        return memberRepo.findAllByEnabled(false).stream().map(member -> converter.getDto(memberDetailsRepo.getById(member.getId()))).collect(Collectors.toList());
    }

    public List<String> getTeamList() {
        List<String> teamList = new ArrayList<>();
        memberDetailsMap.values().stream().forEach(details -> teamList.add(details.getTeam()));
        return teamList.stream().distinct().filter(team -> !team.equals(STRING_SYSTEM)).sorted().collect(Collectors.toList());
    }
    // ----- ----- ----- ----- ----- Searching ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- login 실패 또는 성공시 필요한 부분 ----- ----- ----- ----- ----- //
    public int getFailureCount(String userId) {
        Member member = searchByUserId(userId);
        if(member.getUserId() == null) {
            return 0;   // 해당 아이디의 member가 없을 경우
        } else {
            // 실패 카운트를 증가시키고 map에 업데이트 후 리턴.
            member.increaseFailureCount();
            memberMap.put(userId, member);
            
            int failureCount = member.getFailureCount();
            if(failureCount >= 5) {
                memberRepo.save(member);
            }
            searchMemberDetailsById(member.getId()).updateMember(member);
            return failureCount;
        }
    }
    public void clearMemberFailureCount(String userId) {
        Member member = memberMap.get(userId);   // 로그인 성공시에만 호출하므로 map에서 가져온다.
        if(member == null) return;
        member.clearFailureCount();
        memberMap.put(userId, member);
    }
    // ----- ----- ----- ----- ----- login 실패 또는 성공시 필요한 부분 ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- update my info ----- ----- ----- ----- ----- //
    public RequestResult updateMyInfo(MemberInfoDTO dto) {
        dto.setMobileNo(dto.getMobileNo().strip()).setNameEng(dto.getNameEng().strip()).setName(dto.getName().strip());
        
        if(dto.getMobileNo() != null && !dto.getMobileNo().isBlank() && !MemberDetails.checkMobileNo(dto.getMobileNo()))
            return RequestResult.getDefaultFail("모바일 전화번호 양식이 아닙니다.");

        if(dto.getNameEng() != null && !dto.getNameEng().isBlank() && dto.getNameEng().length() > Constants.COLUMN_LENGTH_NAME)
            return RequestResult.getDefaultFail("영문 이름이 문자 길이를 초과하였습니다. 허용 길이: " + Constants.COLUMN_DEFINE_NAME);

        if(!dto.updateBirthday())
            return RequestResult.getDefaultFail("입력한 생일 날짜가 잘못 되었습니다. 다시 입력하세요.");

        MemberDetails myDetails = searchMemberDetailsById(dto.getId());

        myDetails.updateMobileNo(dto.getMobileNo()).updateName(dto.getName()).updateNameEng(dto.getNameEng());

        memberDetailsMap.put(dto.getId(), memberDetailsRepo.save(myDetails));

        return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_UPDATED);
    }
    public RequestResult updateMyPw(PasswordDTO dto) {
        if(dto.getOriginalPw() == null || dto.getOriginalPw().isBlank()) {
            return RequestResult.getDefaultFail("현재 비밀번호를 입력하세요.");
        }
        if(dto.getNewPw() == null || dto.getNewPw().isBlank()) {
            return RequestResult.getDefaultFail("새 비밀번호를 입력하세요.");
        }
        if(dto.getNewPwCheck() == null || dto.getNewPwCheck().isBlank()) {
            return RequestResult.getDefaultFail("비밀번호 확인란을 입력하세요.");
        }

        if(dto.getNewPw().length() < Constants.PW_UPDATE_AT_LEAST_LENGTH) {
            return RequestResult.getDefaultFail("새 비밀번호를 8자 이상으로 작성하세요.");
        }
        if(!Member.checkPwPolicy(dto.getNewPw())) {
            return RequestResult.getDefaultFail("보안을 위해 영문자, 숫자, 특수문자를 포함해 작성하세요.");
        }
        if(!dto.getNewPw().equals(dto.getNewPwCheck())) {
            return RequestResult.getDefaultFail("새 비밀번호와 비밀번호 입력란이 일치하지 않습니다.");
        }

        MemberDetails details = searchMemberDetailsById(dto.getId());
        Member member = details.getMember();

        if(!Member.checkOriginalPw(member.getPassword(), dto.getOriginalPw())) {
            return RequestResult.getDefaultFail("현재 비밀번호가 일치하지 않습니다.");
        }

        if(!member.updatePassword(dto.getOriginalPw(), dto.getNewPw())) {
            return RequestResult.getDefaultFail("비밀번호 변경이 실패하였습니다.");
        }

        memberMap.put(member.getUserId(), memberRepo.save(member));
        memberDetailsMap.put(member.getId(), details.updateMember(member));

        return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_UPDATED);
    }
    // ----- ----- ----- ----- ----- update my info ----- ----- ----- ----- ----- //



    // ----- ----- ----- ----- ----- member list info ----- ----- ----- ----- ----- //
    public List<MemberInfoDTO> getMemberInfoList(String team) {
        return searchMemberDetailsByTeam(team).stream().filter(e -> !e.isRetired()).map(
            e -> getMemberInfo(e.getId()).bindingImgSrc(attachmentsService.getMemberPicture(e.getId()))
                .setAge(e.getBirthday() == null ? 0 : (LocalDate.now().getYear() - e.getBirthday().getYear()) + 1)

                // 아래 코드에서는 생일이 지나야 나이가 플러스된다. 해가 지났을 때 플러스되도록 하기 위해서 단순 계산하는 위 코드로 변경.
                // .setAge(e.getBirthday() == null ? 0 : ((int) ChronoUnit.YEARS.between(e.getBirthday(), LocalDate.now()) + 1))
                // 단순 연도 차이에서 태어난 연도를 더해줘야 하므로 + 1
        ).collect(Collectors.toList());
    }
    // ----- ----- ----- ----- ----- member list info ----- ----- ----- ----- ----- //




    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin section ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //
    public RequestResult saveMemberforAdmin(MemberDTOforAdmin dto) {
        if(dto.getUserId() == null || dto.getUserId().isBlank()) {
            return RequestResult.getDefaultFail("ID를 입력하세요.");
        }
        if(dto.getTeam() == null || dto.getTeam().isBlank()) {
            return RequestResult.getDefaultFail("부서를 입력하세요.");
        }
        if(dto.getJobTitle() == null || dto.getJobTitle().isBlank()) {
            return RequestResult.getDefaultFail("직위를 선택하세요.");
        }
        if(dto.getRole() == null) {
            return RequestResult.getDefaultFail("권한을 선택하세요.");
        }

        try {
            if(dto.getId() == null) {   // 신규 등록
                Member searchExistsMember = searchByUserId(dto.getUserId());
                if(searchExistsMember.getUserId() != null && searchExistsMember.getUserId().toUpperCase().equals(dto.getUserId().toUpperCase()))
                    return RequestResult.getDefaultFail("중복된 ID입니다. ID를 다시 작성하세요.");
    
                Member member = converter.getMember(dto);
                member.updatePasswordForAdmin(dto.getUserPw());
    
                MemberDetails details = converter.getEntity(dto);
                Optional<Basecode> searchJobTitleCode = basecodeRepo.findByTypeAndCode(BasecodeType.JOB_TITLE, dto.getJobTitle());
                if(searchJobTitleCode.isPresent())
                    details.updateJobTitle(searchJobTitleCode.get().getValue());

                MemberDetails savedMemberDetails = memberDetailsRepo.save(details.updateMember(member));
                memberDetailsMap.put(member.getId(), savedMemberDetails);
                
                if(!details.isRetired())
                    memberMap.put(savedMemberDetails.getMember().getUserId(), savedMemberDetails.getMember());
    
                return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_INSERTED);
    
            } else {   // 엔티티 id가 있으면 기존 유저
                MemberDetails details = searchMemberDetailsById(dto.getId());
                Member member = details.getMember();
    
                if(dto.getUserPw() != null && !dto.getUserPw().isBlank()) {   // pw를 입력한 경우에만 업데이트
                    member.updatePasswordForAdmin(dto.getUserPw());
                }

                member.updateRole(dto.getRole()).updateEnabled(dto.isEnabled());
    
                Optional<Basecode> searchJobTitleCode = basecodeRepo.findByTypeAndCode(BasecodeType.JOB_TITLE, dto.getJobTitle());
                if(searchJobTitleCode.isPresent()) {
                    details.updateJobTitle(searchJobTitleCode.get().getValue());
                }
                details.updateTeam(dto.getTeam()).updateName(dto.getName()).updateRetired(dto.isRetired()).updateMember(member);
                memberDetailsMap.put(member.getId(), memberDetailsRepo.save(details));
    
                if(details.isRetired()) {
                    memberMap.remove(member.getUserId());
                    memberDetailsMap.remove(member.getId());
                } else {
                    memberMap.put(member.getUserId(), member);
                    memberDetailsMap.put(member.getId(), details);
                }

                return RequestResult.getDefaultSuccess(Constants.RESULT_MESSAGE_UPDATED);
            }
        } catch(Exception e) {
            e.printStackTrace();
            logger.warn("{}{}유저 등록/수정 시 에러가 발생하였습니다.", e.getMessage(), System.lineSeparator());
            return RequestResult.getDefaultError(e.getMessage());
        }
    }
    // ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin section ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- //

    
    
    // ----- ----- ----- ----- ----- schedule: 퇴직자 계정 disabled 처리 ----- ----- ----- ----- ----- //
    // 퇴직자 계정은 disabled 처리해야 하는데 실수로 놓치게 될 경우를 대비해서 스케줄러로 disabled 처리한다.
    public void retireMemberToDisabled() {
        List<Member> enabledMember = memberRepo.findAllByEnabled(true);
        enabledMember.forEach(member -> {
            MemberDetails details = memberDetailsRepo.getById(member.getId());
            if(details.isRetired()) {
                memberRepo.save(member.updateEnabled(false));
                logger.info("퇴직자 계정을 비활성화 처리 하였습니다. 퇴직자: {} {} {} {}", member.getId(), details.getTeam(), details.getJobTitle(), details.getName());
            }
        });
    }
    // ----- ----- ----- ----- ----- schedule: 퇴직자 계정 disabled 처리 ----- ----- ----- ----- ----- //
}
