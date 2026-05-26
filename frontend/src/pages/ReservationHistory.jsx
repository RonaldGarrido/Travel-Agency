import React, { useState, useEffect } from 'react';

const styles = {
  page: {
    minHeight: 'calc(100vh - 64px)',
    backgroundColor: '#f5f5f5',
    padding: '32px 16px'
  },
  container: {
    maxWidth: '800px',
    margin: '0 auto'
  },
  title: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#1a1a2e',
    marginBottom: '24px'
  },
  list: {
    display: 'flex',
    flexDirection: 'column',
    gap: '16px'
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: '12px',
    padding: '24px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)'
  },
  cardTop: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    flexWrap: 'wrap',
    gap: '12px'
  },
  packageName: {
    fontSize: '18px',
    fontWeight: '600',
    color: '#1a1a2e',
    marginBottom: '4px'
  },
  meta: {
    fontSize: '14px',
    color: '#666'
  },
  date: {
    fontSize: '13px',
    color: '#999',
    marginTop: '4px'
  },
  right: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-end',
    gap: '8px'
  },
  statusBadge: {
    padding: '4px 12px',
    borderRadius: '20px',
    fontSize: '13px',
    fontWeight: '500'
  },
  amount: {
    fontSize: '20px',
    fontWeight: '700',
    color: '#e94560'
  },
  cardActions: {
    display: 'flex',
    gap: '12px',
    marginTop: '16px'
  },
  payBtn: {
    padding: '10px 20px',
    backgroundColor: '#e94560',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500'
  },
  cancelBtn: {
    padding: '10px 20px',
    backgroundColor: 'transparent',
    color: '#999',
    border: '1px solid #ddd',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px'
  },
  empty: {
    textAlign: 'center',
    padding: '60px',
    color: '#999'
  },
  exploreBtn: {
    marginTop: '16px',
    padding: '12px 24px',
    backgroundColor: '#e94560',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500'
  }
};

export default function ReservationHistory() {
  const [reservations, setReservations] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Simulación de carga
    setTimeout(() => {
      setReservations([
        { id: 1, name: 'Playa Caribeña', date: '2024-02-15', status: 'Confirmada', amount: 1200 },
        { id: 2, name: 'Montaña Nevada', date: '2024-03-10', status: 'Pendiente', amount: 950 }
      ]);
      setLoading(false);
    }, 500);
  }, []);

  if (loading) {
    return <div style={styles.empty}>Cargando historial...</div>;
  }

  if (reservations.length === 0) {
    return (
      <div style={styles.page}>
        <div style={styles.container}>
          <h1 style={styles.title}>Historial de Reservas</h1>
          <div style={styles.empty}>
            <p>No tienes reservas aún</p>
            <button style={styles.exploreBtn}>Explorar Catálogo</button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <h1 style={styles.title}>Historial de Reservas</h1>
        <div style={styles.list}>
          {reservations.map(reservation => (
            <div key={reservation.id} style={styles.card}>
              <div style={styles.cardTop}>
                <div>
                  <h3 style={styles.packageName}>{reservation.name}</h3>
                  <p style={styles.meta}>Reserva #{reservation.id}</p>
                  <p style={styles.date}>{reservation.date}</p>
                </div>
                <div style={styles.right}>
                  <span style={{...styles.statusBadge, backgroundColor: reservation.status === 'Confirmada' ? '#e8f5e9' : '#fff3e0', color: reservation.status === 'Confirmada' ? '#2d8a2d' : '#f57c00'}}>
                    {reservation.status}
                  </span>
                  <span style={styles.amount}>${reservation.amount}</span>
                </div>
              </div>
              <div style={styles.cardActions}>
                <button style={styles.payBtn}>Ver Detalles</button>
                <button style={styles.cancelBtn}>Cancelar</button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
