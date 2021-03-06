package inti.SAhomepage.Board.controller;

import inti.SAhomepage.Board.dto.FileDto;
import inti.SAhomepage.Board.service.BoardService;
import inti.SAhomepage.Board.dto.BoardDto;
import inti.SAhomepage.Board.service.FileService;
import inti.SAhomepage.Board.util.MD5Generator;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class BoardController {
    private BoardService boardService;
    private FileService fileService;

    public BoardController(BoardService boardService, FileService fileService){
        this.boardService = boardService;
        this.fileService = fileService;
    }

    @GetMapping("/board")
    public String list(Model model){
        List<BoardDto> boardDtoList = boardService.getBoardList();
        model.addAttribute("postList", boardDtoList);
        return "board/list.html";
    }

    @GetMapping("/board/post")
    public String post(){
        return "board/post.html";
    }

    @PostMapping("/board/post")
    public String write(@RequestParam("file") MultipartFile files, BoardDto boardDto){
        try{
            String origFilename = files.getOriginalFilename();

            String filename = new MD5Generator(origFilename).toString();
            /*실행되는 위치의 'files' 폴더에 파일이 저장됩니다.*/
            String savePath = System.getProperty("user.dir") + "\\files";
            /*파일이 저장되는 폴더가 없으면 폴더를 생성합니다.*/
            if(!new File(savePath).exists()){
                try{
                    new File(savePath).mkdir();
                }
                catch(Exception e) {
                    e.getStackTrace();
                }
            }
            String filePath = savePath + "\\" + filename;
            files.transferTo(new File(filePath));

            FileDto fileDto = new FileDto();
            fileDto.setOrigFilename(origFilename);
            fileDto.setFilename(filename);
            fileDto.setFilePath(filePath);

            Long fileId = fileService.saveFile(fileDto);
            boardDto.setFileId(fileId);
            boardDto.setFname(origFilename);
            boardService.savePost(boardDto);
        }catch(Exception e){
            e.printStackTrace();
        }
        return "redirect:/board";
    }

    @GetMapping("/board/post/{id}")
    public String detail(@PathVariable("id") Long id, Model model){
        BoardDto boardDto = boardService.getPost(id);
        model.addAttribute("post", boardDto);
        return "board/detail.html";
    }

    @GetMapping("/board/post/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model){
        BoardDto boardDto = boardService.getPost(id);
        model.addAttribute("post", boardDto);
        return "board/edit.html";
    }

    @PutMapping("/board/post/edit/{id}")
    public String update(BoardDto boardDto) {
        boardService.savePost(boardDto);
        return "redirect:/board";
    }

    @DeleteMapping("/board/post/{id}")
    public String delete(@PathVariable("id") Long id){
        boardService.deletePost(id);
        return "redirect:/board";
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> fileDownload(@PathVariable("fileId") Long fileId) throws IOException {
        FileDto fileDto = fileService.getFile(fileId);
        Path path = Paths.get(fileDto.getFilePath());
        Resource resource = new InputStreamResource(Files.newInputStream(path));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDto.getOrigFilename() + "\"")
                .body(resource);
    }
}
