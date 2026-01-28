package training.afpa.cda24060.squatrbnb.dao;

import org.hibernate.Session;
import training.afpa.cda24060.squatrbnb.model.Reservation;
import training.afpa.cda24060.squatrbnb.model.enums.StatutReservation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ReservationDAO extends GenericDAO<Reservation, Long> {

    public ReservationDAO() {
        super(Reservation.class);
    }

    /**
     * Réservations d'un hôte
     */
    public List<Reservation> findByHoteId(Long hoteId) {
        try (Session session = getSession()) {
            String hql = """
                FROM Reservation r 
                JOIN FETCH r.logement l 
                JOIN FETCH r.locataire 
                WHERE l.hote.id = :hoteId 
                ORDER BY r.dateReservation DESC
                """;
            return session.createQuery(hql, Reservation.class)
                    .setParameter("hoteId", hoteId)
                    .getResultList();
        }
    }

    /**
     * Réservations d'un hôte avec filtres et pagination
     */
    public List<Reservation> findByHote(Long hoteId, String statut, Long logementId, int limit, int offset) {
        try (Session session = getSession()) {
            StringBuilder hql = new StringBuilder("""
                FROM Reservation r 
                JOIN FETCH r.logement l 
                JOIN FETCH r.locataire 
                WHERE l.hote.id = :hoteId
                """);

            if (statut != null && !statut.isBlank()) {
                hql.append(" AND r.statut = :statut");
            }
            if (logementId != null) {
                hql.append(" AND l.id = :logementId");
            }

            hql.append(" ORDER BY r.dateReservation DESC");

            var query = session.createQuery(hql.toString(), Reservation.class)
                    .setParameter("hoteId", hoteId);

            if (statut != null && !statut.isBlank()) {
                query.setParameter("statut", StatutReservation.valueOf(statut));
            }
            if (logementId != null) {
                query.setParameter("logementId", logementId);
            }

            return query
                    .setFirstResult(offset)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    /**
     * Réservations récentes d'un hôte
     */
    public List<Reservation> findRecentesByHote(Long hoteId, int limit) {
        try (Session session = getSession()) {
            String hql = """
                FROM Reservation r 
                JOIN FETCH r.logement l 
                JOIN FETCH r.locataire 
                WHERE l.hote.id = :hoteId 
                ORDER BY r.dateReservation DESC
                """;
            return session.createQuery(hql, Reservation.class)
                    .setParameter("hoteId", hoteId)
                    .setMaxResults(limit)
                    .getResultList();
        }
    }

    /**
     * Compte les réservations d'un hôte
     */
    public int countByHote(Long hoteId, String statut, Long logementId) {
        try (Session session = getSession()) {
            StringBuilder hql = new StringBuilder("""
                SELECT COUNT(r) FROM Reservation r 
                JOIN r.logement l 
                WHERE l.hote.id = :hoteId
                """);

            if (statut != null && !statut.isBlank()) {
                hql.append(" AND r.statut = :statut");
            }
            if (logementId != null) {
                hql.append(" AND l.id = :logementId");
            }

            var query = session.createQuery(hql.toString(), Long.class)
                    .setParameter("hoteId", hoteId);

            if (statut != null && !statut.isBlank()) {
                query.setParameter("statut", StatutReservation.valueOf(statut));
            }
            if (logementId != null) {
                query.setParameter("logementId", logementId);
            }

            return query.uniqueResult().intValue();
        }
    }

    /**
     * Compte par statut
     */
    public int countByHoteAndStatut(Long hoteId, String statut) {
        return countByHote(hoteId, statut, null);
    }

    /**
     * Accepter une réservation
     */
    public boolean accepter(Long reservationId, Long hoteId, String message) {
        try (Session session = getSession()) {
            var tx = session.beginTransaction();

            Reservation resa = session.get(Reservation.class, reservationId);
            if (resa == null || !resa.getLogement().getHoteId().equals(hoteId)) {
                return false;
            }

//            resa.setStatut(StatutReservation.CONFIRMEE);
            resa.setDateConfirmation(LocalDateTime.now());

            session.merge(resa);
            tx.commit();

            // TODO: Envoyer notification/email au locataire

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Refuser une réservation
     */
    public boolean refuser(Long reservationId, Long hoteId, String raison, String message) {
        try (Session session = getSession()) {
            var tx = session.beginTransaction();

            Reservation resa = session.get(Reservation.class, reservationId);
            if (resa == null || !resa.getLogement().getHoteId().equals(hoteId)) {
                return false;
            }

            resa.setStatut(StatutReservation.REFUSEE);
            resa.setDateAnnulation(LocalDateTime.now());
            resa.setMotifAnnulation(raison);

            session.merge(resa);
            tx.commit();

            // TODO: Envoyer notification/email au locataire

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}