package com.backend.application.serviceImpl;

import com.backend.application.dto.concert.*;
import com.backend.application.service.ConcertService;
import com.backend.core.concert.CategoryRepository;
import com.backend.core.concert.Concert;
import com.backend.core.concert.ConcertRepository;
import com.backend.core.concert.ConcertStatus;
import com.backend.core.member.Member;
import com.backend.core.reservation.Reservation;
import com.backend.core.reservation.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Transactional
@Service
public class ConcertServiceImpl implements ConcertService {

    private final ConcertRepository concertRepository;
    private final CategoryRepository categoryRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public ConcertDetailResponse create(ConcertCreateRequest request, Member member) {
        // TODO : validate request
        return categoryRepository.findById(request.getCategoryId()).map(category -> {
            Concert concert = concertRepository.save(request.toEntity(member, category));
            return new ConcertDetailResponse(concert, false, true);
        }).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ConcertDetailResponse findById(Long id, Member member) {
        // TODO : validate request
        return concertRepository.findById(id).map(value ->
                new ConcertDetailResponse(value,
                        hasPurchased(id, member),
                        hasOwnership(id, member)))
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConcertSimpleResponse> findAll(ConcertListRequest request) {
        // TODO : validate request
        // TODO : pagination

        ConcertStatus status = request.getConcertStatus() == null ?
                null : ConcertStatus.valueOf(request.getConcertStatus());
        return concertRepository.findAllByCriteria(status,
                request.getCategoryId(), request.getPerformerName())
                .stream().map(ConcertSimpleResponse::new).collect(toList());
    }

    @Override
    public ConcertDetailResponse update(Long id, ConcertUpdateRequest request, Member member) {
        // TODO : validate request
        return concertRepository.findById(id).map(concert -> {
            if(!concert.getPerformer().getId().equals(member.getId())){
                return null;
            }
            concert.updateInfo(request.getTitle(), request.getStartDate(), request.getEndDate(),
                    request.getDescription(), request.getMaxAudience(), request.getCurrentAudience(),
                    request.getPrice(), request.getImgUrl());
            concert.updateCategory(categoryRepository.getOne(request.getCategoryId()));
            return new ConcertDetailResponse(concert, false, true);
        }).orElse(null);
    }

    public ConcertDetailResponse updateStatus(Long id, Member member, String status){
        if(!hasOwnership(id, member)){
            return null;
        }
        Concert concert = concertRepository.getOne(id);
        ConcertStatus updateStatus = Arrays.stream(ConcertStatus.values())
                .filter(s -> s.getDesc().equals(status)).collect(toList()).get(0);
        concert.updateStatus(updateStatus);
        return new ConcertDetailResponse(concert, false, true);
    }

    public void delete(Long id) {
        concertRepository.deleteById(id);
    }

    public Long reserve(Long id, Long purchaserId) {
        Concert concert = concertRepository.getOne(id);
        concert.reserve();
        Optional<Reservation> reservation =
                reservationRepository.findFirstByConcertIdAndPurchaseId(id, purchaserId);
        if(reservation.isPresent()){
            Reservation res = reservation.get();
            res.countUp();
            return res.getId();
        }
        Member performer = concertRepository.getOne(id).getPerformer();
        Reservation newReservation= Reservation.builder()
                .concertId(id)
                .performerId(performer.getId())
                .purchaseId(purchaserId)
                .purchaseCount(1)
                .build();
        reservationRepository.save(newReservation);
        return newReservation.getId();
    }

    private boolean hasPurchased(Long concertId, Member member){
        return reservationRepository
                .findFirstByConcertIdAndPurchaseId(concertId, member.getId())
                .isPresent();
    }

    private boolean hasOwnership(Long concertId, Member member){
        return concertRepository.findById(concertId)
                .map(value -> value.getPerformer().equals(member))
                .orElse(false);
    }
}
