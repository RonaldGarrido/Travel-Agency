import React, { useState, useEffect } from 'react';

const styles = {
  page: {
    minHeight: 'calc(100vh - 64px)',
    backgroundColor: '#f5f5f5',
    padding: '32px 16px'
  },
  container: {
    maxWidth: '1200px',
    margin: '0 auto'
  },
  title: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#1a1a2e',
    marginBottom: '24px'
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
    gap: '20px',
    marginBottom: '32px'
  },
  statCard: {
    backgroundColor: '#fff',
    borderRadius: '12px',
    padding: '20px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)'
  },
  statLabel: {
    fontSize: '14px',
    color: '#666',
    marginBottom: '8px'
  },
  statValue: {
    fontSize: '32px',
    fontWeight: '700',
    color: '#e94560'
  },
  section: {
    backgroundColor: '#fff',
    borderRadius: '12px',
    padding: '24px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
    marginBottom: '24px'
  },
  sectionTitle: {
    fontSize: '20px',
    fontWeight: '600',
    color: '#1a1a2e',
    marginBottom: '16px'
  },
  table: {
    width: '100%',
    borderCollapse: 'collapse'
  },
  th: {
    textAlign: 'left',
    padding: '12px',
    borderBottom: '2px solid #eee',
    color: '#666',
    fontSize: '14px',
    fontWeight: '600'
  },
  td: {
    padding: '12px',
    borderBottom: '1px solid #eee'
  },
  actionBtn: {
    padding: '6px 12px',
    marginRight: '8px',
    border: 'none',
    borderRadius: '6px',
    cursor: 'pointer',
    fontSize: '13px',
    fontWeight: '500'
  },
  approveBtn: {
    backgroundColor: '#e8f5e9',
    color: '#2d8a2d'
  },
  rejectBtn: {
    backgroundColor: '#ffebee',
    color: '#e94560'
  }
};

export default function AdminDashboard() {
  const [stats, setStats] = useState({ totalUsers: 150, totalReservations: 485, totalRevenue: 125400 });
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setTimeout(() => {
      setLoading(false);
    }, 500);
  }, []);

  if (loading) {
    return <div style={{...styles.page, textAlign: 'center'}}>Cargando panel...</div>;
  }

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <h1 style={styles.title}>Panel de Administración</h1>

        <div style={styles.grid}>
          <div style={styles.statCard}>
            <p style={styles.statLabel}>Usuarios Totales</p>
            <p style={styles.statValue}>{stats.totalUsers}</p>
          </div>
          <div style={styles.statCard}>
            <p style={styles.statLabel}>Reservas Totales</p>
            <p style={styles.statValue}>{stats.totalReservations}</p>
          </div>
          <div style={styles.statCard}>
            <p style={styles.statLabel}>Ingresos Totales</p>
            <p style={styles.statValue}>${stats.totalRevenue}</p>
          </div>
        </div>

        <div style={styles.section}>
          <h2 style={styles.sectionTitle}>Reservas Pendientes</h2>
          <table style={styles.table}>
            <thead>
              <tr>
                <th style={styles.th}>Usuario</th>
                <th style={styles.th}>Paquete</th>
                <th style={styles.th}>Fecha</th>
                <th style={styles.th}>Monto</th>
                <th style={styles.th}>Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td style={styles.td}>Juan Pérez</td>
                <td style={styles.td}>Playa Caribeña</td>
                <td style={styles.td}>2024-02-15</td>
                <td style={styles.td}>$1,200</td>
                <td style={styles.td}>
                  <button style={{...styles.actionBtn, ...styles.approveBtn}}>Aprobar</button>
                  <button style={{...styles.actionBtn, ...styles.rejectBtn}}>Rechazar</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
