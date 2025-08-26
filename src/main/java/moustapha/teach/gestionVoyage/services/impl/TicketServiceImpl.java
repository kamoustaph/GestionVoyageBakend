package moustapha.teach.gestionVoyage.services.impl;
import moustapha.teach.gestionVoyage.dto.TicketDetailDto;
import moustapha.teach.gestionVoyage.dto.TicketFilterDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import moustapha.teach.gestionVoyage.dto.TicketDto;
import moustapha.teach.gestionVoyage.dto.TicketCreateDto;
import moustapha.teach.gestionVoyage.entities.Reservation;
import moustapha.teach.gestionVoyage.entities.Ticket;
import moustapha.teach.gestionVoyage.mapper.TicketMapper;
import moustapha.teach.gestionVoyage.repositories.ReservationRepository;
import moustapha.teach.gestionVoyage.repositories.TicketRepository;
import moustapha.teach.gestionVoyage.enums.ReservationStatus;
import moustapha.teach.gestionVoyage.enums.StatutTicket;
import moustapha.teach.gestionVoyage.services.TicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final ReservationRepository reservationRepository;
    private final TicketMapper ticketMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketDto> getTicketById(Long id) {
        return ticketRepository.findById(id)
                .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketDto> getTicketByReservationId(Long reservationId) {
        return ticketRepository.findByReservationId(reservationId)
                .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketDto> getTicketByQrCode(String qrCode) {
        return ticketRepository.findByQrCode(qrCode)
                .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> getTicketsByStatut(StatutTicket statut) {
        return ticketRepository.findByStatut(statut)
                .stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> getTicketsByUserId(Long userId) {
        return ticketRepository.findByUserId(userId)
                .stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketDto> getTicketsByVoyageId(Long voyageId) {
        return ticketRepository.findByVoyageId(voyageId)
                .stream()
                .map(ticketMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public TicketDto createTicket(TicketCreateDto ticketCreateDto) {
        log.info("Création d'un nouveau ticket pour la réservation {}", ticketCreateDto.getReservationId());

        Reservation reservation = reservationRepository.findById(ticketCreateDto.getReservationId())
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));

        if (reservation.getStatut() != ReservationStatus.CONFIRMED) {
            throw new RuntimeException("La réservation doit être confirmée pour générer un ticket");
        }

        if (ticketRepository.findByReservationId(ticketCreateDto.getReservationId()).isPresent()) {
            throw new RuntimeException("Un ticket existe déjà pour cette réservation");
        }

        Ticket ticket = new Ticket();
        ticket.setReservation(reservation);
        ticket.setQrCode(generateUniqueQrCode());
        ticket.setStatut(StatutTicket.VALIDE);
        ticket.setCreatedAt(LocalDateTime.now());

        Ticket savedTicket = ticketRepository.save(ticket);
        log.info("Ticket créé avec l'ID {}", savedTicket.getId());

        return ticketMapper.toDto(savedTicket);
    }

    @Override
    public Optional<TicketDto> updateTicketStatut(Long id, StatutTicket nouveauStatut) {
        return ticketRepository.findById(id)
                .map(ticket -> {
                    ticket.setStatut(nouveauStatut);
                    ticket.setUpdatedAt(LocalDateTime.now());
                    return ticketMapper.toDto(ticketRepository.save(ticket));
                });
    }

    @Override
    public boolean deleteTicket(Long id) {
        if (ticketRepository.existsById(id)) {
            ticketRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean validateTicket(String qrCode) {
        return ticketRepository.findByQrCode(qrCode)
                .map(ticket -> {
                    if (ticket.getStatut() == StatutTicket.VALIDE) {
                        ticket.setStatut(StatutTicket.UTILISE);
                        ticket.setUpdatedAt(LocalDateTime.now());
                        ticketRepository.save(ticket);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

    @Override
    public void processExpiredTickets() {
        List<Ticket> expiredTickets = ticketRepository.findTicketsToExpire(LocalDateTime.now());
        for (Ticket ticket : expiredTickets) {
            ticket.setStatut(StatutTicket.EXPIRE);
            ticket.setUpdatedAt(LocalDateTime.now());
            ticketRepository.save(ticket);
        }
        log.info("Traité {} tickets expirés", expiredTickets.size());
    }

    private String generateUniqueQrCode() {
        String qrCode;
        do {
            qrCode = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (ticketRepository.existsByQrCode(qrCode));
        return qrCode;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<TicketDto> getAllTickets(Pageable pageable) {
        return ticketRepository.findAll(pageable)
                .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDto> getTicketsByStatut(StatutTicket statut, Pageable pageable) {
        return ticketRepository.findByStatut(statut, pageable)
                .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDto> getTicketsByReservationId(Long reservationId, Pageable pageable) {
        return ticketRepository.findByReservationId(reservationId, pageable)
                .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDto> getTicketsByUserId(Long userId, Pageable pageable) {
        return ticketRepository.findByUserId(userId, pageable)
                .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDto> getTicketsByVoyageId(Long voyageId, Pageable pageable) {
        return ticketRepository.findByVoyageId(voyageId, pageable)
                .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDto> getTicketsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return ticketRepository.findTicketsByDateRange(startDate, endDate, pageable)
                .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDto> getTicketsByQrCode(String qrCode, Pageable pageable) {
        return ticketRepository.findByQrCodeContaining(qrCode, pageable)
                .map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TicketDto> searchTicketsWithFilters(TicketFilterDto filterDto, Pageable pageable) {
        return ticketRepository.findTicketsWithFilters(
                filterDto.getReservationId(),
                filterDto.getUserId(),
                filterDto.getVoyageId(),
                filterDto.getStatut(),
                filterDto.getQrCode(),
                filterDto.getCreatedAtStart(),
                filterDto.getCreatedAtEnd(),
                pageable
        ).map(ticketMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TicketDetailDto> getTicketDetail(Long id) {
        return ticketRepository.findWithDetailsById(id)
                .map(ticketMapper::toDetailDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalTickets() {
        return ticketRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTicketsByStatutCount(StatutTicket statut) {
        return ticketRepository.countByStatut(statut);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTicketsByUserIdCount(Long userId) {
        return ticketRepository.countByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTicketsByVoyageIdCount(Long voyageId) {
        return ticketRepository.countByVoyageId(voyageId);
    }
}
