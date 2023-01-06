package com.project.simplegw.member.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.project.simplegw.member.data.MemberData;
import com.project.simplegw.member.dtos.admin.receive.DtorMemberCreate;
import com.project.simplegw.member.dtos.admin.receive.DtorMemberUpdate;
import com.project.simplegw.member.dtos.admin.receive.DtorPwForceUpdate;
import com.project.simplegw.member.dtos.admin.send.DtosMember;
import com.project.simplegw.member.dtos.admin.send.DtosMemberDetails;
import com.project.simplegw.member.dtos.receive.DtorProfile;
import com.project.simplegw.member.dtos.receive.DtorPwChange;
import com.project.simplegw.member.dtos.send.DtosProfile;
import com.project.simplegw.member.entities.Member;
import com.project.simplegw.member.entities.MemberAddOn;
import com.project.simplegw.member.entities.MemberDetails;
import com.project.simplegw.member.entities.MemberEnvSetting;
import com.project.simplegw.member.helpers.MemberConverter;
import com.project.simplegw.member.repositories.MemberAddOnRepo;
import com.project.simplegw.member.repositories.MemberDetailsRepo;
import com.project.simplegw.member.repositories.MemberEnvSettingRepo;
import com.project.simplegw.member.repositories.MemberRepo;
import com.project.simplegw.system.security.LoginUser;
import com.project.simplegw.system.security.PwEncoder;
import com.project.simplegw.system.vos.Constants;
import com.project.simplegw.system.vos.Role;
import com.project.simplegw.system.vos.ServiceResult;
import com.project.simplegw.system.vos.ServiceMsg;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, isolation = Isolation.READ_COMMITTED)
public class MemberService {
    private final MemberRepo repo;
    private final MemberDetailsRepo detailsRepo;
    private final MemberEnvSettingRepo envSettingRepo;
    private final MemberAddOnRepo addOnRepo;
    private final MemberConverter memberConverter;
    private final PwEncoder pwEncoder;

    // <Member @Id, MemberData>
    private final Map<Long, MemberData> memberDataStorage = new ConcurrentHashMap<>(100);

    @Autowired
    public MemberService(
        MemberRepo repo, MemberDetailsRepo detailsRepo, MemberAddOnRepo addOnRepo, MemberEnvSettingRepo envSettingRepo,
        MemberConverter memberConverter, PwEncoder pwEncoder
    ) {
        this.repo = repo;
        this.detailsRepo = detailsRepo;
        this.envSettingRepo = envSettingRepo;
        this.addOnRepo = addOnRepo;
        this.memberConverter = memberConverter;
        this.pwEncoder = pwEncoder;

        setSystemAccounts();
        setMemberDataStorage();

        log.info("Component '" + this.getClass().getName() + "' has been created.");
    }



    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Set system accounts ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    private void setSystemAccounts() {
        Optional<Member> findAdmin = repo.findByUserId("admin");
        if(findAdmin.isPresent()) {
            log.info("Skip creating an administrator account.");
        
        } else {
            Member savedAdmin = repo.save(
                Member.builder().userId("admin").password(encodingPassword("admin")).role(Role.ADMIN).enabled(true).build()
            );

            MemberDetails adminDetails = MemberDetails.builder().member(savedAdmin).team(Constants.STRING_SYSTEM).jobTitle("").name("관리자").build();
            
            detailsRepo.save(adminDetails);
            envSettingRepo.save( MemberEnvSetting.builder().member(savedAdmin).build().init() );
            addOnRepo.save( MemberAddOn.builder().member(savedAdmin).build() );

            log.info("An administrator account has been created.");
        }

        // 제안 게시판에 익명 사용자로 게시글 작성할 때 사용.
        Optional<Member> findAnonymous = repo.findByUserId("anonymous");
        if(findAnonymous.isPresent()) {
            log.info("Skip creating an anonymous account.");

        } else {
            Member savedAnonymous = repo.save(
                Member.builder().userId("anonymous").password(encodingPassword(UUID.randomUUID().toString())).role(Role.USER).enabled(true).build()
            );

            MemberDetails anonymousDetails = MemberDetails.builder().member(savedAnonymous).team(Constants.STRING_SYSTEM).jobTitle("").name("익명 사용자").build();
            
            detailsRepo.save(anonymousDetails);
            envSettingRepo.save( MemberEnvSetting.builder().member(savedAnonymous).build().init() );
            addOnRepo.save( MemberAddOn.builder().member(savedAnonymous).build() );

            log.info("An anonymous account has been created.");
        }
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- Set system accounts ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- MemberData setting ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    private void setMemberDataStorage() {
        // Member entity를 먼저 1차 캐시로 로드한 뒤 아래 MemberDetails.getMember() 실행하면 개별 쿼리를 날리지 않고 1차 캐시에서 가져옴.
        repo.findByResignedMember(false);

        getMemberDetailsEntities(false).stream().forEach(e -> {   // 시스템 계정도 포함.
            memberDataStorage.put( e.getMember().getId(), memberConverter.getMemberData(e).setId(e.getMember().getId()) );
        });

        log.info("memberData loaded.");
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- MemberData setting ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- login ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    Member getMember(String userId) {   // @Id값 Long id가 아닌 userId로 찾기, Login할 때 사용.
        log.info("login id: {}", userId);
        return repo.findByUserId(userId).orElseGet(Member::new);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- login ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- common ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    private List<MemberDetails> getMemberDetailsEntities(boolean isResigned) {
        return detailsRepo.findByResignedOrderByName(isResigned);
    }

    private Member getMember(Long id) {
        return repo.findById(id).orElseGet(Member::new);
    }

    private String encodingPassword(String rawPassword) {
        if(rawPassword == null || rawPassword.isBlank())
            return "";
        return pwEncoder.encode(rawPassword);
    }
    




    private MemberDetails getDetails(Long memberId) {   // MemberDetails의 FK값, Member 클래스의 @Id값으로 찾기
        return detailsRepo.findByMemberId(memberId).orElseGet(MemberDetails::new);
    }

    public MemberData getMemberData(Long memberId) {
        MemberData data = memberDataStorage.get(memberId);
        if(data == null)
            data = memberConverter.getMemberData( getDetails(memberId) );
        
        return data;
    }

    public MemberData getMemberData(LoginUser loginUser) {
        return getMemberData(loginUser.getMember().getId());
    }





    public List<String> getTeams() {   // 시스템 계정을 제외한 재직중인 멤버들의 부서명을 리스트로 리턴.
        return memberDataStorage.values().stream().filter(e -> !e.getTeam().equals( Constants.STRING_SYSTEM) ).map(MemberData::getTeam).distinct().sorted().collect(Collectors.toList());
    }

    public List<MemberData> getTeamMembers(String team) {   // 팀 멤버를 이름순 정렬해서 리턴
        return memberDataStorage.values().stream().filter(e -> e.getTeam().equals(team)).sorted(Comparator.comparing(MemberData::getName)).collect(Collectors.toList());
    }

    public List<MemberData> getTeamMembersWithoutMe(String team, LoginUser loginUser) {
        List<MemberData> list = getTeamMembers(team);
        list.removeIf(e -> e.getId().equals( loginUser.getMember().getId() ));
        return list;
    }

    public long workingEmployeeCount() {
        return memberDataStorage.values().stream().filter(e -> !e.getTeam().equals( Constants.STRING_SYSTEM) ).count();
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- common ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- System ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    public List<Long> getAdmins() {   // SystemService 클래스에서 호출.
        return repo.findByResignedMember(false).stream().filter(e -> Role.ADMIN.equals(e.getRole())).map(Member::getId).collect(Collectors.toList());
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- System ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    List<DtosMember> getMembers(boolean isResigned) {
        List<DtosMember> members = new ArrayList<>();
        repo.findByResignedMember(isResigned);   // Member entity를 1차 캐시에 먼저 로드한다.

        if(isResigned) {
            getMemberDetailsEntities(isResigned).forEach(e -> {
                DtosMember dto = memberConverter.getDtosMember(e.getMember());
                dto.setTeam(e.getTeam()).setJobTitle(e.getJobTitle()).setName(e.getName());
                members.add(dto);
            });

        } else {
            memberDataStorage.values().stream().sorted(Comparator.comparing(MemberData::getName)).filter(e -> !e.getTeam().equals(Constants.STRING_SYSTEM)).forEach(e -> {
                DtosMember dto = memberConverter.getDtosMember(e);
                Member member = getMember(e.getId());
                dto.setUserId(member.getUserId()).setRole(member.getRole()).setEnabled(member.isEnabled());
                members.add(dto);
            });
        }
        return members;
    }

    DtosMember getDtosMember(Long memberId) {
        return memberConverter.getDtosMember( getMember(memberId) );
    }


    DtosMemberDetails getMemberDetails(Long memberId) {
        MemberDetails details = getDetails(memberId);
        Member member = details.getMember();

        DtosMemberDetails dtosMemberDetails = memberConverter.getDtosMemberDetails( details );
        dtosMemberDetails.setId(member.getId()).setUserId(member.getUserId()).setRole(member.getRole()).setEnabled(member.isEnabled());

        return dtosMemberDetails;
    }


    ServiceMsg create(DtorMemberCreate dto, LoginUser loginUser) {
        log.info("create() method called by admin({}). user profile: {}", loginUser.getMember().getId(), dto.toString());

        Optional<Member> findMember = repo.findByUserId(dto.getId());
        if(findMember.isPresent()) {
            log.warn("duplicated ID. create failed.");
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("중복된 ID입니다.");
        }

        try {
            Member savedMember = repo.save(
                memberConverter.getMember(dto).updateRole(Role.USER).updatePw(encodingPassword(dto.getPw())).updateEnabled(true)
            );

            MemberDetails details = memberConverter.getDetails(dto);
            detailsRepo.save( details.bindMember(savedMember) );
            envSettingRepo.save( MemberEnvSetting.builder().member(savedMember).build().init() );
            addOnRepo.save( MemberAddOn.builder().member(savedMember).build() );

            memberDataStorage.put( savedMember.getId(), memberConverter.getMemberData(details).setId(savedMember.getId()) );

            return new ServiceMsg().setResult(ServiceResult.SUCCESS);

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("createMember exception.");
            log.warn("paramters: {}", dto.toString());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("사용자 등록 에러입니다. 로그를 확인하세요.");
        }
    }

    @Caching(
        evict = {
            @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_USER_PROFILES, allEntries = false, key = "#memberId"),
            @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_USER_ENVIRONMENT_SETTING, allEntries = false, key = "#memberId", condition = "#dto.isResigned() == true")
        }
    )
    public ServiceMsg update(Long memberId, DtorMemberUpdate dto, LoginUser loginUser) {   // CacheEvict 사용을 위해 public으로 전환
        log.info("CacheEvict method 'update()' called by admin({}). target user: {}", loginUser.getMember().getId(), memberId);

        try {
            Optional<MemberDetails> findDetails = detailsRepo.findByMemberId(memberId);
            
            if(findDetails.isPresent()) {
                MemberDetails details = findDetails.get();
                Member member = details.getMember();
    
                Member savedMember = repo.save(
                    member.updateRole(Role.valueOf(dto.getRole())).updateEnabled(dto.isEnabled())
                );

                detailsRepo.save( details.updateDetails(dto).bindMember(savedMember) );

                memberDataStorage.put(savedMember.getId(), memberConverter.getMemberData(details).setId(savedMember.getId()));

                return new ServiceMsg().setResult(ServiceResult.SUCCESS);
                
            } else {
                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("등록되지 않은 사용자입니다.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("updateMember exception.");
            log.warn("paramters: {}", dto.toString());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("사용자 업데이트 에러입니다. 로그를 확인하세요.");
        }
    }

    ServiceMsg updateMemberPw(Long memberId, DtorPwForceUpdate dto, LoginUser loginUser) {
        Member member = getMember(memberId);

        if(member == null || member.getId() == null)
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("등록되지 않은 사용자입니다.");
        
        member.updatePw( encodingPassword(dto.getPw()) );
        repo.save(member);
        return new ServiceMsg().setResult(ServiceResult.SUCCESS);
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- admin ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //





    // ↓ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- user ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↓ //
    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_USER_PROFILES, key = "#loginUser.getMember().getId()")
    public DtosProfile getProfile(LoginUser loginUser) {   // Cacheable 사용을 위해 public으로 전환
        log.info("Cacheable method 'getProfile()' called. user: {}", loginUser.getMember().getId());
        return memberConverter.getDtosProfile(getDetails(loginUser.getMember().getId()));
    }

    @Cacheable(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_USER_PROFILES, key = "#memberId")
    public DtosProfile getProfile(Long memberId) {   // Cacheable 사용을 위해 public으로 전환
        // 임직원 정보(employee profiles) 에서 캐시를 공통으로 사용하기 위해 추가한 메서드.
        return memberConverter.getDtosProfile(getDetails(memberId));
    }

    @CacheEvict(cacheManager = Constants.CACHE_MANAGER, cacheNames = Constants.CACHE_USER_PROFILES, allEntries = false, key = "#loginUser.getMember().getId()")
    public ServiceMsg updateProfile(DtorProfile dto, LoginUser loginUser) {   // CacheEvict 사용을 위해 public으로 전환
        log.info("CacheEvict method 'updateProfile()' called. user: {}", loginUser.getMember().getId());

        try {
            Optional<MemberDetails> target = detailsRepo.findByMemberId(loginUser.getMember().getId());

            if(target.isPresent()) {
                MemberDetails details = target.get();
                detailsRepo.save( details.updateDetails(dto) );
                return new ServiceMsg().setResult(ServiceResult.SUCCESS);

            } else {
                return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("등록되지 않은 사용자입니다.");
            }

        } catch(Exception e) {
            e.printStackTrace();
            log.warn("updateMember exception.");
            log.warn("parameters: {}", dto.toString());
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("업데이트 에러입니다. 관리자에게 문의하세요.");
        }
    }



    ServiceMsg updateMyPassword(DtorPwChange dto, LoginUser loginUser) {
        Member member = loginUser.getMember();

        if(!pwEncoder.matches(dto.getOldPw(), member.getPassword()))
            return new ServiceMsg().setResult(ServiceResult.FAILURE).setMsg("기존 패스워드가 일치하지 않습니다.");
        
        repo.save( member.updatePw(encodingPassword(dto.getNewPw())) );
        return new ServiceMsg().setResult(ServiceResult.SUCCESS);
    }

    boolean isOldPasswordMatched(String oldPw, LoginUser loginUser) {
        return pwEncoder.matches(oldPw, loginUser.getPassword());
    }
    // ↑ ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- user ----- ----- ----- ----- ----- ----- ----- ----- ----- ----- ↑ //
}
