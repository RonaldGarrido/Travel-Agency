import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getReservationsByUser, cancelReservation } from '../api/reservationApi'
import { useAuth } from '../context/AuthContext'

export default function MyReservationsPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [reservations, setReservations] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!user) {
      navigate('/login')
      return
    }
    fetchReservations()
  }, [user, navigate])

  const fetchReservations = async () => {
    try {
      const response = await getReservationsByUser(user.email)
      setReservations(response.data)
    } catch {
      setError('Error al cargar las reservas')
    } finally {
      setLoading(false)
    }
  }

  const handleCancel = async (id) => {
    if (!window.confirm('¿Estás seguro de cancelar esta reserva?')) return
    try {
      await cancelReservation(id)
      fetchReservations()
    } catch (err) {
      alert(err.response?.data?.message || 'Error al cancelar la reserva')
    }
  }

  const statusLabel = {
    PENDING_PAYMENT: { text: 'Pendiente de pago', color: '#f0a500', bg: '#fff8e1' },
    CONFIRMED: { text: 'Confirmada', color: '#2d8a2d', bg: '#e8f8e8' },
    CANCELLED: { text: 'Cancelada', color: '#e94560', bg: '#fff0f0' },
    EXPIRED: { text: 'Expirada', color: '#999', bg: '#f5f5f5' }
  }

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <h1 style={styles.title}>Mis reservas</h1>

        {loading && <p style={styles.center}>Cargando reservas...</p>}
        {error && <p style={styles.error}>{error}</p>}

        {!loading && reservations.length === 0 && (
          <div style={styles.empty}>
            <p>No tienes reservas aún.</p>
            <button
              onClick={() => navigate('/packages')}
              style={styles.exploreBtn}
            >
              Explorar paquetes
            </button>
          </div>
        )}

        <div style={styles.list}>
          {reservations.map((res) => {
            const status = statusLabel[res.status] || statusLabel.EXPIRED
            return (
              <div key={res.id} style={styles.card}>
                <div style={styles.cardTop}>
                  <div>
                    <h3 style={styles.packageName}>{res.packageName}</h3>
                    <p style={styles.meta}>
                      {res.passengerCount} pasajero(s)
                    </p>
                    <p style={styles.date}>
                      Reservado el{' '}
                      {new Date(res.createdAt).toLocaleDateString('es-CL')}
                    </p>
                  </div>
                  <div style={styles.right}>
                    <span
                      style={{
                        ...styles.statusBadge,
                        color: status.color,
                        backgroundColor: status.bg
                      }}
                    >
                      {status.text}
                    </span>
                    <p style={styles.amount}>
                      ${res.finalAmount?.toLocaleString('es-CL')}
                    </p>
                  </div>
                </div>

                {res.discountDetail && (
                  <p style={styles.discount}>
                    Descuentos: {res.discountDetail}
                  </p>
                )}

                <div style={styles.cardActions}>
                  {res.status === 'PENDING_PAYMENT' && (
                    <>
                      <button
                        onClick={() => navigate(`/payments/${res.id}`)}
                        style={styles.payBtn}
                      >
                        Pagar ahora
                      </button>
                      <button
                        onClick={() => handleCancel(res.id)}
                        style={styles.cancelBtn}
                      >
                        Cancelar
                      </button>
                    </>
                  )}
                </div>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}

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
  discount: {
    fontSize: '12px',
    color: '#2d8a2d',
    fontStyle: 'italic',
    marginTop: '8px'
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
  },
  error: {
    color: '#e94560',
    textAlign: 'center'
  },
  center: {
    textAlign: 'center',
    color: '#999'
  }
}
