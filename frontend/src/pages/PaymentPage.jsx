import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getReservationById } from '../api/reservationApi'
import { processPayment } from '../api/paymentApi'
import { useAuth } from '../context/AuthContext'

export default function PaymentPage() {
  const { reservationId } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const [reservation, setReservation] = useState(null)
  const [loading, setLoading] = useState(false)
  const [loadingRes, setLoadingRes] = useState(true)
  const [error, setError] = useState(null)
  const [success, setSuccess] = useState(false)
  const [cardData, setCardData] = useState({
    cardNumber: '',
    cardExpiry: '',
    cvv: ''
  })

  useEffect(() => {
    if (!user) {
      navigate('/login')
      return
    }
    const fetchReservation = async () => {
      try {
        const response = await getReservationById(reservationId)
        setReservation(response.data)
      } catch {
        setError('No se pudo cargar la reserva')
      } finally {
        setLoadingRes(false)
      }
    }
    fetchReservation()
  }, [reservationId, user, navigate])

  const handleChange = (e) => {
    setCardData({ ...cardData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
      await processPayment({
        reservationId,
        cardNumber: cardData.cardNumber.replace(/\s/g, ''),
        cardExpiry: cardData.cardExpiry,
        cvv: cardData.cvv
      })
      setSuccess(true)
    } catch (err) {
      setError(err.response?.data?.message || 'Error al procesar el pago')
    } finally {
      setLoading(false)
    }
  }

  const formatCardNumber = (value) => {
    return value
      .replace(/\D/g, '')
      .substring(0, 16)
      .replace(/(.{4})/g, '$1 ')
      .trim()
  }

  if (loadingRes) return <div style={styles.center}>Cargando...</div>
  if (!reservation) return <div style={styles.center}>Reserva no encontrada</div>

  if (success) {
    return (
      <div style={styles.page}>
        <div style={styles.successCard}>
          <div style={styles.successIcon}>✓</div>
          <h2 style={styles.successTitle}>¡Pago realizado con éxito!</h2>
          <p style={styles.successText}>
            Tu reserva ha sido confirmada. ¡Disfruta tu viaje!
          </p>
          <div style={styles.successDetails}>
            <p><strong>Paquete:</strong> {reservation.packageName}</p>
            <p><strong>Pasajeros:</strong> {reservation.passengerCount}</p>
            <p><strong>Total pagado:</strong> ${reservation.finalAmount?.toLocaleString('es-CL')}</p>
          </div>
          <button
            onClick={() => navigate('/my-reservations')}
            style={styles.successBtn}
          >
            Ver mis reservas
          </button>
        </div>
      </div>
    )
  }

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <h1 style={styles.pageTitle}>Pago de reserva</h1>

        <div style={styles.layout}>
          <div style={styles.card}>
            <h3 style={styles.cardTitle}>Datos de la tarjeta</h3>
            <p style={styles.simNote}>
              Pago simulado — ingresa cualquier dato de prueba
            </p>

            {error && <div style={styles.error}>{error}</div>}

            <form onSubmit={handleSubmit}>
              <div style={styles.field}>
                <label style={styles.label}>Número de tarjeta</label>
                <input
                  name="cardNumber"
                  value={cardData.cardNumber}
                  onChange={(e) =>
                    setCardData({
                      ...cardData,
                      cardNumber: formatCardNumber(e.target.value)
                    })
                  }
                  placeholder="1234 5678 9012 3456"
                  required
                  maxLength={19}
                  style={styles.input}
                />
              </div>

              <div style={styles.row}>
                <div style={styles.field}>
                  <label style={styles.label}>Fecha de expiración</label>
                  <input
                    name="cardExpiry"
                    value={cardData.cardExpiry}
                    onChange={handleChange}
                    placeholder="MM/YY"
                    required
                    maxLength={5}
                    style={styles.input}
                  />
                </div>
                <div style={styles.field}>
                  <label style={styles.label}>CVV</label>
                  <input
                    name="cvv"
                    value={cardData.cvv}
                    onChange={handleChange}
                    placeholder="123"
                    required
                    maxLength={4}
                    style={styles.input}
                  />
                </div>
              </div>

              <button
                type="submit"
                disabled={loading}
                style={{
                  ...styles.submitBtn,
                  opacity: loading ? 0.7 : 1
                }}
              >
                {loading
                  ? 'Procesando pago...'
                  : `Pagar $${reservation.finalAmount?.toLocaleString('es-CL')}`}
              </button>
            </form>
          </div>

          <div style={styles.summary}>
            <div style={styles.card}>
              <h3 style={styles.cardTitle}>Resumen de la reserva</h3>
              <p style={styles.summaryName}>{reservation.packageName}</p>
              <div style={styles.summaryDivider} />
              <div style={styles.summaryRow}>
                <span>Pasajeros</span>
                <span>{reservation.passengerCount}</span>
              </div>
              <div style={styles.summaryRow}>
                <span>Subtotal</span>
                <span>${reservation.baseAmount?.toLocaleString('es-CL')}</span>
              </div>
              {reservation.discountAmount > 0 && (
                <div style={styles.summaryRow}>
                  <span>Descuentos</span>
                  <span style={{ color: '#2d8a2d' }}>
                    -${reservation.discountAmount?.toLocaleString('es-CL')}
                  </span>
                </div>
              )}
              {reservation.discountDetail && (
                <p style={styles.discountDetail}>{reservation.discountDetail}</p>
              )}
              <div style={styles.summaryDivider} />
              <div style={styles.summaryTotal}>
                <span>Total a pagar</span>
                <span style={styles.totalPrice}>
                  ${reservation.finalAmount?.toLocaleString('es-CL')}
                </span>
              </div>
            </div>
          </div>
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
    maxWidth: '900px',
    margin: '0 auto'
  },
  pageTitle: {
    fontSize: '24px',
    fontWeight: '700',
    color: '#1a1a2e',
    marginBottom: '24px'
  },
  layout: {
    display: 'grid',
    gridTemplateColumns: '1fr 300px',
    gap: '24px',
    alignItems: 'start'
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: '12px',
    padding: '24px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)'
  },
  cardTitle: {
    fontSize: '18px',
    fontWeight: '600',
    color: '#1a1a2e',
    marginBottom: '8px'
  },
  simNote: {
    fontSize: '12px',
    color: '#999',
    backgroundColor: '#fffbea',
    padding: '8px 12px',
    borderRadius: '6px',
    marginBottom: '20px'
  },
  error: {
    backgroundColor: '#fff0f0',
    color: '#e94560',
    padding: '12px',
    borderRadius: '8px',
    fontSize: '14px',
    marginBottom: '16px'
  },
  field: {
    marginBottom: '20px',
    flex: 1
  },
  label: {
    display: 'block',
    fontSize: '14px',
    fontWeight: '500',
    color: '#333',
    marginBottom: '6px'
  },
  input: {
    width: '100%',
    padding: '12px 16px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    fontSize: '14px',
    outline: 'none',
    boxSizing: 'border-box'
  },
  row: {
    display: 'flex',
    gap: '16px'
  },
  submitBtn: {
    width: '100%',
    padding: '14px',
    backgroundColor: '#e94560',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    fontSize: '16px',
    fontWeight: '600',
    cursor: 'pointer',
    marginTop: '8px'
  },
  summary: {},
  summaryName: {
    fontSize: '15px',
    fontWeight: '600',
    color: '#1a1a2e',
    marginBottom: '12px'
  },
  summaryDivider: {
    height: '1px',
    backgroundColor: '#f0f0f0',
    margin: '12px 0'
  },
  summaryRow: {
    display: 'flex',
    justifyContent: 'space-between',
    fontSize: '14px',
    color: '#555',
    marginBottom: '8px'
  },
  discountDetail: {
    fontSize: '12px',
    color: '#2d8a2d',
    fontStyle: 'italic',
    marginTop: '4px'
  },
  summaryTotal: {
    display: 'flex',
    justifyContent: 'space-between',
    fontSize: '16px',
    fontWeight: '600',
    color: '#1a1a2e'
  },
  totalPrice: {
    color: '#e94560',
    fontSize: '20px'
  },
  successCard: {
    maxWidth: '500px',
    margin: '80px auto',
    backgroundColor: '#fff',
    borderRadius: '16px',
    padding: '48px',
    textAlign: 'center',
    boxShadow: '0 4px 20px rgba(0,0,0,0.1)'
  },
  successIcon: {
    width: '64px',
    height: '64px',
    backgroundColor: '#e8f8e8',
    color: '#2d8a2d',
    borderRadius: '50%',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '28px',
    margin: '0 auto 20px'
  },
  successTitle: {
    fontSize: '24px',
    fontWeight: '700',
    color: '#1a1a2e',
    marginBottom: '12px'
  },
  successText: {
    color: '#666',
    marginBottom: '24px'
  },
  successDetails: {
    backgroundColor: '#f9f9f9',
    borderRadius: '8px',
    padding: '16px',
    textAlign: 'left',
    marginBottom: '24px',
    fontSize: '14px',
    lineHeight: '1.8',
    color: '#555'
  },
  successBtn: {
    padding: '12px 32px',
    backgroundColor: '#e94560',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    fontSize: '15px',
    fontWeight: '600',
    cursor: 'pointer'
  },
  center: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: 'calc(100vh - 64px)',
    color: '#999'
  }
}