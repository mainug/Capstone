package com.example.union.service;

import com.example.union.dto.MemberDTO;
import com.example.union.entity.MemberEntity;
import com.example.union.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);

    public void save(MemberDTO memberDTO) {
        // 1. dto -> entity 변환
        // 2. repository의 save 메서드 호출

        // 1. DTO에서 일반 텍스트 비밀번호를 가져옴
        String rawPassword = memberDTO.getMemberPassword();

        // 2. PasswordEncoder를 사용하여 비밀번호를 해싱
        String encodedPassword = passwordEncoder.encode(rawPassword);

        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO);

        memberEntity.setMemberPassword(encodedPassword); // Entity의 비밀번호 필드에 해싱된 값 설정 (MemberEntity에 setMemberPassword 필요)

        memberRepository.save(memberEntity);
        // repository의 save 메서드 호출 (조건. entity 객체를 넘겨줘야 함)

        System.out.println("회원가입 완료 (비밀번호 해싱 저장): 이메일=" + memberDTO.getMemberEmail());
    }

    public MemberDTO login(MemberDTO memberDTO) {
        /*
            1. 회원이 입력한 이메일로 DB에서 조회를 함
            2. DB에서 조회한 비밀번호와 사용자가 입력한 비밀번호가 일치하는지 판단
         */
        String email = memberDTO.getMemberEmail(); // 사용자가 입력한 이메일

        // 로그: 로그인 시도 정보
        logger.info("로그인 시도: 이메일={}", email);

        Optional<MemberEntity> byMemberEmail = memberRepository.findByMemberEmail(memberDTO.getMemberEmail());
        if (byMemberEmail.isPresent()) {
            // 조회 결과가 있다 (해당 이메일을 가진 회원 정보가 있다)
            MemberEntity memberEntity = byMemberEmail.get();

            // 사용자가 입력한 일반 텍스트 비밀번호
            String rawPassword = memberDTO.getMemberPassword();

            // DB에서 조회한 해싱된 비밀번호 (MemberEntity에서 가져와야 함)
            String storedEncodedPassword = memberEntity.getMemberPassword();

//            logger.info("DB 조회 결과: 이메일={}", memberEntity.getMemberEmail());
//            logger.info("DB 조회 결과: 저장된 비밀번호={}", storedEncodedPassword);

            boolean passwordMatch = passwordEncoder.matches(rawPassword, storedEncodedPassword);

//            logger.info("비밀번호 일치 여부 결과: {}", passwordMatch);

            if (passwordEncoder.matches(rawPassword, storedEncodedPassword)) {
                // 비밀번호 일치
                logger.info("로그인 성공: 이메일={}", email); // 확인용 로그
                // entity -> dto 변환 후 리턴 (entity로 DB에서 조회를 해왔지만 컨트롤러 쪽으로 넘겨줄 때에는 DTO로 변환)
                MemberDTO dto = MemberDTO.toMemberDTO(memberEntity);
                return dto;
            } else {
                // 비밀번호 불일치 (로그인 실패)
                logger.info("로그인 실패: 비밀번호 불일치");
                return null;
            }
        } else {
            // 조회 결과가 없다 (해당 이메일을 가진 회원이 없다)
            logger.info("로그인 실패: 해당 이메일 회원이 없음");
            return null;
        }
    }

    public List<MemberDTO> findAll() {
        List<MemberEntity> memberEntityList = memberRepository.findAll();
        List<MemberDTO> memberDTOList = new ArrayList<>();
        for (MemberEntity memberEntity: memberEntityList) {
            memberDTOList.add(MemberDTO.toMemberDTO(memberEntity));
        }
        return memberDTOList;
    }

    public MemberDTO findById(Long id) {
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findById(id);
        if (optionalMemberEntity.isPresent()) {
            return MemberDTO.toMemberDTO(optionalMemberEntity.get());
        } else {
            return null;
        }
    }

    public MemberDTO updateForm(String myEmail) {
        Optional<MemberEntity> optionalMemberEntity = memberRepository.findByMemberEmail(myEmail);
        if (optionalMemberEntity.isPresent()) {
            return MemberDTO.toMemberDTO(optionalMemberEntity.get());
        } else {
            return null;
        }
    }

    public void update(MemberDTO memberDTO) {
        /*
            회원 정보 업데이트 로직:
            1. 업데이트 대상 회원의 기존 Entity를 DB에서 조회하여 기존 비밀번호 (해싱된 값)를 가져옴
            2. memberDTO에 새로운 비밀번호가 포함되어 있는지 확인
            3. 새로운 비밀번호가 있다면, 해당 비밀번호를 해싱
            4. 새로운 비밀번호가 없다면, 1번에서 가져온 기존 비밀번호를 사용
            5. memberDTO의 비밀번호 필드에 3번 또는 4번의 해싱된 비밀번호를 설정
            6. memberDTO를 Entity로 변환 (toUpdateMemberEntity 사용)
            7. 변환된 Entity를 Repository를 통해 저장
         */

        // 1. 업데이트 대상 회원의 기존 Entity를 DB에서 조회 (기존 비밀번호를 가져오기 위함)
        // memberDTO에 ID 값이 반드시 포함되어 있어야 합니다.
        Optional<MemberEntity> optionalExistingEntity = memberRepository.findById(memberDTO.getId());

        if (!optionalExistingEntity.isPresent()) {
            // 해당 ID의 회원이 DB에 없는 경우 (예외 처리 필요)
            System.err.println("Error: ID " + memberDTO.getId() + " 회원을 찾을 수 없어 업데이트를 진행할 수 없습니다.");
            // 실제 애플리케이션에서는 예외를 발생시키거나, 오류 메시지를 반환하는 등의 처리를 해야 합니다.
            return; // 또는 throw new EntityNotFoundException("...");
        }

        MemberEntity existingEntity = optionalExistingEntity.get();

        // 2. memberDTO에 새로운 비밀번호가 입력되었는지 확인
        String rawPassword = memberDTO.getMemberPassword();
        String passwordToSave; // 최종적으로 Entity에 저장할 해싱된 비밀번호

        if (rawPassword != null && !rawPassword.isEmpty()) {
            // 3. 새로운 비밀번호가 입력되었다면, 해당 비밀번호를 해싱합니다.
            passwordToSave = passwordEncoder.encode(rawPassword);
            System.out.println("회원 정보 업데이트: 새로운 비밀번호 해싱 적용");
        } else {
            // 4. 새로운 비밀번호가 입력되지 않았다면, 기존 비밀번호 (해싱된 값)를 그대로 사용합니다.
            passwordToSave = existingEntity.getMemberPassword(); // 기존 Entity에서 해싱된 비밀번호 가져옴
            System.out.println("회원 정보 업데이트: 기존 비밀번호 유지");
        }

        // 5. memberDTO의 비밀번호 필드에 최종적으로 저장할 해싱된 비밀번호를 설정합니다.
        // MemberEntity.toUpdateMemberEntity 메서드가 DTO에서 비밀번호를 가져가기 때문입니다.
        memberDTO.setMemberPassword(passwordToSave);


        // 6. 이제 비밀번호 필드에 해싱된 비밀번호가 담긴 memberDTO를 사용하여 Entity로 변환합니다.
        MemberEntity updatedEntity = MemberEntity.toUpdateMemberEntity(memberDTO);

        // 참고: memberDTO의 ID, 이메일, 이름 등 다른 필드 값들은 toUpdateMemberEntity에 의해 그대로 Entity에 복사됩니다.
        // toUpdateMemberEntity 구현 방식에서는 새로운 Entity 객체를 생성하므로,
        // 기존 Entity(existingEntity)를 직접 수정하는 방식(existingEntity.setEmail(...), existingEntity.setPassword(...) 등)도 가능합니다.
        // 어떤 방식을 사용하든 최종적으로 save 메서드에는 올바른 데이터가 담긴 Entity가 전달되어야 합니다.

        // 7. Repository의 save 메서드를 호출하여 업데이트된 Entity를 저장합니다.
        // JPA의 save 메서드는 ID 값이 있으면 update, 없으면 insert를 수행합니다.
        memberRepository.save(updatedEntity);
        System.out.println("회원 정보 업데이트 완료: ID=" + memberDTO.getId()); // 확인용 로그
    }

    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }
}
