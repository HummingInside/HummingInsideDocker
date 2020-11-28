package com.backend.api;

import com.backend.api.exception.ResourceNotFoundException;
import com.backend.application.dto.concert.*;
import com.backend.application.service.FileService;
import com.backend.application.serviceImpl.ConcertServiceImpl;
import com.backend.core.concert.Concert;
import com.backend.core.concert.ConcertRepository;
import com.backend.core.member.Member;
import com.backend.core.member.MemberRepository;
import com.backend.core.reservation.Reservation;
import com.backend.core.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@RequestMapping("/concerts")
@RestController
public class ConcertApi {

    private final ConcertServiceImpl concertService;
    private final FileService fileService;
    private final MemberRepository memberRepository;
    private final ConcertRepository concertRepository;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ConcertCreateRequest request){
        Member member = (Member) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        ConcertDetailResponse response = concertService.create(request, member);
        if(response == null){
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id){
        Member member = (Member) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        ConcertDetailResponse response = concertService.findById(id, member);
        if(response == null){
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<?> getList(ConcertListRequest request){
        List<ConcertSimpleResponse> responses = concertService.findAll(request);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody ConcertUpdateRequest request) throws IOException {
        Member member = (Member) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        fileService.delete(id, request.getImgUrl());
        ConcertDetailResponse response = concertService.update(id, request, member);
        if(response == null){
            throw new ResourceNotFoundException();
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) throws IOException {
        Member member = (Member) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        fileService.delete(id, "delete");
        concertService.delete(id);
        return ResponseEntity.ok(id);
    }

    @PostMapping("/{id}/reservations")
    public ResponseEntity<?> reserve(@PathVariable Long id){
        Member member = (Member) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(concertService.reserve(id, member.getId()));
    }
}
