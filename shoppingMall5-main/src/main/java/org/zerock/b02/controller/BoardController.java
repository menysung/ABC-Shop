package org.zerock.b02.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.zerock.b02.dto.*;
import org.zerock.b02.security.dto.MemberSecurityDTO;
import org.zerock.b02.service.BoardService;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Controller
@RequestMapping("/board")
@Log4j2
@RequiredArgsConstructor
public class BoardController {
    //업로드된 파일들의 경로
    @Value("C:\\upload")
    private String uploadPath;

    private final BoardService boardService;

    @GetMapping("/list")
    public String list(PageRequestDTO pageRequestDTO, Model model) {
        PageResponseDTO<BoardAllListDTO> responseDTO = boardService.listWithAll(pageRequestDTO);
        model.addAttribute("responseDTO", responseDTO);
        model.addAttribute("pageRequestDTO", pageRequestDTO); // 검색 및 페이지 정보 유지
        return "board/list"; // 뷰 이름
    }

    @PreAuthorize("isAuthenticated()") // 인증된 사용자만 접근 가능
    @GetMapping("/register")
    public String register(Model model, Authentication authentication) {
        if (authentication.getPrincipal() instanceof MemberSecurityDTO) {
            MemberSecurityDTO memberSecurityDTO = (MemberSecurityDTO) authentication.getPrincipal();
            if (memberSecurityDTO.isSocial()) {
                log.info("소셜 로그인 사용자로 인증됨.");
            }
        }
        return "board/register"; // 등록 페이지로 이동
    }

    @PostMapping("/register")
    public String registerPost(@Valid BoardDTO boardDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        log.info("board Post register......");
        if (bindingResult.hasErrors()) {
            log.error("errors.....");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            return "redirect:/board/register";
        }
        log.info(boardDTO.toString());

        Long bno = boardService.register(boardDTO);
        redirectAttributes.addFlashAttribute("result", bno);

        return "redirect:/board/list";
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/read")
    public String read(Long bno, PageRequestDTO pageRequestDTO, Model model, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            // 게시글 정보 가져오기
            BoardDTO boardDTO = boardService.readOne(bno);
            if (boardDTO == null) {
                return "redirect:/board/list";
            }

            // 현재 로그인한 사용자의 정보 가져오기
            String currentUsername = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

            // 수정 가능 여부를 판단하는 플래그 추가
            boolean canModify = currentUsername.equals(boardDTO.getWriter()) || isAdmin;

            // 모델에 필요한 정보 추가
            model.addAttribute("dto", boardDTO);
            model.addAttribute("isAdmin", isAdmin);
            model.addAttribute("canModify", canModify);  // 수정 권한 여부를 모델에 추가
            model.addAttribute("currentUsername", currentUsername);

            return "board/read";
        } catch (Exception e) {
            e.printStackTrace(); // 로그에 오류를 기록
            return "error/500"; // 사용자 정의 오류 페이지로 리디렉션
        }
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String modifyGet(Long bno, PageRequestDTO pageRequestDTO, Model model, Authentication authentication) {
        try {
            BoardDTO boardDTO = boardService.readOne(bno);
            if (boardDTO == null) {
                return "redirect:/board/list";
            }

            String currentUsername = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

            // 관리자는 모든 게시글을 수정할 수 있음
            if (!currentUsername.equals(boardDTO.getWriter()) && !isAdmin) {
                return "redirect:/board/list"; // 작성자와 현재 사용자가 다르고 관리자가 아니면 리스트로 리디렉션
            }

            model.addAttribute("dto", boardDTO);
            model.addAttribute("pageRequestDTO", pageRequestDTO); // 페이지 정보 유지
            model.addAttribute("isAdmin", isAdmin); // 관리자 여부를 모델에 추가
            return "board/modify";
        } catch (Exception e) {
            e.printStackTrace(); // 로그에 오류를 기록
            return "error/500"; // 사용자 정의 오류 페이지로 리디렉션
        }
    }



    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String modify(PageRequestDTO pageRequestDTO,
                         @Valid BoardDTO boardDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Authentication authentication) {
        log.info("board Modify Post......");
        String link = pageRequestDTO.getLink(); // 페이지 유지
        long bno = boardDTO.getBno();
        link = link + "&bno=" + bno; // 링크에 bno 추가

        if (bindingResult.hasErrors()) {
            log.error("errors.....");
            redirectAttributes.addFlashAttribute("errors", bindingResult.getAllErrors());
            redirectAttributes.addFlashAttribute("bno", bno);
            // 유효성검사 에러시 다시 수정페이지로 돌아감
            return "redirect:/board/modify?" + link;
        }

        // 현재 사용자와 게시글 작성자를 비교하고 관리자의 권한을 확인
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        BoardDTO existingBoardDTO = boardService.readOne(bno);
        if (existingBoardDTO == null || (!currentUsername.equals(existingBoardDTO.getWriter()) && !isAdmin)) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/board/list";
        }

        boardService.modify(boardDTO);
        redirectAttributes.addFlashAttribute("result", "modified");
        redirectAttributes.addFlashAttribute("bno", bno);
        return "redirect:/board/read?" + link; // 수정 후 읽기 페이지로 이동
    }


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/remove")
    public String remove(BoardDTO boardDTO, RedirectAttributes redirectAttributes, Authentication authentication) {
        Long bno = boardDTO.getBno();

        // 현재 사용자와 게시글 작성자를 비교하고 관리자의 권한을 확인
        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));

        BoardDTO existingBoardDTO = boardService.readOne(bno);
        if (existingBoardDTO == null || (!currentUsername.equals(existingBoardDTO.getWriter()) && !isAdmin)) {
            redirectAttributes.addFlashAttribute("error", "권한이 없습니다.");
            return "redirect:/board/list";
        }

        boardService.remove(bno);
        log.info(boardDTO.getFileNames());
        List<String> fileNames = boardDTO.getFileNames();
        if (fileNames != null && fileNames.size() > 0) {
            removeFiles(fileNames);
        }
        redirectAttributes.addFlashAttribute("result", "removed");
        return "redirect:/board/list";
    }

    private void removeFiles(List<String> fileNames) {
        for (String fileName : fileNames) {
            Resource resource = new FileSystemResource(uploadPath + File.separator + fileName);
            //String resourceName = resource.getFilename();
            try {
                String contentType = Files.probeContentType(resource.getFile().toPath());
                resource.getFile().delete();
                if(contentType.startsWith("image")) {
                    File thumbnailFile = new File(uploadPath + File.separator + "s_" + fileName);
                    thumbnailFile.delete();
                }
            } catch (Exception e){
                log.error(e.getMessage());
            }
        }
    }

}

