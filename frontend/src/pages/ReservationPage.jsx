import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getPackageById } from '../api/packageApi'
import { createReservation } from '../api/reservationApi'
import { useAuth } from '../context/AuthContext'

export default function ReservationPage() {
  const { packageId } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const [pkg, setPkg] = useState(null)
  const [passengerCount, setPassengerCount] = useState(1)
  const [loading, setLoading] = useState(false)
  const [loadingPkg, setLoadingPkg] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    if (!user) {
      navigate('/login')
      return
    }
    const fetchPackage = async () => {
      try {
        const response = await getPackageById(packageId)
        setPkg(response.data)
      } catch {
        setError('No se pudo cargar el paquete')
      } finally {
        setLoadingPkg(false)
      }
    }
    fetchPackage()
  }, [packageId, user, navigate])

  const estimatedTotal = pkg
    ? (pkg.price * passengerCount).toLocaleString('es-CL')
    : 0

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError(null)
    setLoading(true)
    try {
     const response = await createReservation({
    userId: user.email, 
    packageId,
    passengerCount: parseInt(passengerCount)
      })
      navigate(`/payments/${response.data.id}`)
    } catch (err) {
      setError(
        err.response?.data?.message || 'Error al crear la reserva'
      )
    } finally {
      setLoading(false)
    }
  }

  if (loadingPkg) return <div style={styles.center}>Cargando...</div>
  if (!pkg) return <div style={styles.center}>Paquete no encontrado</div>

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <button onClick={() => navigate(-1)} style={styles.backBtn}>
          ← Volver
        </button>

        <h1 style={styles.pageTitle}>Crear reserva</h1>

        <div style={styles.layout}>
          <div style={styles.form}>
            <div style={styles.card}>
              <h3 style={styles.cardTitle}>Información de la reserva</h3>

              {error && <div style={styles.error}>{error}</div>}

              <form onSubmit={handleSubmit}>
                <div style={styles.field}>
                  <label style={styles.label}>Cliente</label>
                  <input
                    value={user?.fullName}
                    disabled
                    style={{ ...styles.input, backgroundColor: '#f9f9f9' }}
                  />
                </div>

                <div style={styles.field}>
                  <label style={styles.label}>Cantidad de pasajeros</label>
                  <input
                    type="number"
                    min="1"
                    max={pkg.availableSlots}
                    value={passengerCount}
                    onChange={(e) => setPassengerCount(e.target.value)}
                    required
                    style={styles.input}
                  />
                  <span style={styles.hint}>
                    Máximo {pkg.availableSlots} cupos disponibles
                  </span>
                </div>

                <div style={styles.discountInfo}>
                  <p style={styles.discountTitle}>Descuentos aplicables</p>
                  <p style={styles.discountItem}>
                    {passengerCount >= 4
                      ? '✓ Descuento por grupo (5%)'
                      : '○ Descuento por grupo (requiere ≥4 personas)'}
                  </p>
                  <p style={styles.discountItem}>
                    ○ Descuento por cliente frecuente (≥3 reservas confirmadas)
                  </p>
                </div>

                <button
                  type="submit"
                  disabled={loading}
                  style={{
                    ...styles.submitBtn,
                    opacity: loading ? 0.7 : 1
                  }}
                >
                  {loading ? 'Creando reserva...' : 'Confirmar reserva'}
                </button>
              </form>
            </div>
          </div>

          <div style={styles.summary}>
            <div style={styles.card}>
              <h3 style={styles.cardTitle}>Resumen del paquete</h3>
              <p style={styles.summaryName}>{pkg.name}</p>
              <p style={styles.summaryDestination}>📍 {pkg.destination}</p>
              <div style={styles.summaryDivider} />
              <div style={styles.summaryRow}>
                <span>Precio por persona</span>
                <span>${pkg.price?.toLocaleString('es-CL')}</span>
              </div>
              <div style={styles.summaryRow}>
                <span>Pasajeros</span>
                <span>{passengerCount}</span>
              </div>
              <div style={styles.summaryDivider} />
              <div style={styles.summaryTotal}>
                <span>Total estimado</span>
                <span style={styles.totalPrice}>${estimatedTotal}</span>
              </div>
              <p style={styles.summaryNote}>
                El monto final puede variar según los descuentos aplicados
              </p>
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
  backBtn: {
    backgroundColor: 'transparent',
    border: 'none',
    color: '#666',
    cursor: 'pointer',
    fontSize: '14px',
    marginBottom: '16px',
    padding: '0'
  },
  pageTitle: {
    fontSize: '24px',
    fontWeight: '700',
    color: '#1a1a2e',
    marginBottom: '24px'
  },
  layout: {
    display: 'grid',
    gridTemplateColumns: '1fr 340px',
    gap: '24px',
    alignItems: 'start'
  },
  form: {},
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
    marginBottom: '20px'
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
  hint: {
    fontSize: '12px',
    color: '#999',
    marginTop: '4px',
    display: 'block'
  },
  discountInfo: {
    backgroundColor: '#f9f9f9',
    borderRadius: '8px',
    padding: '16px',
    marginBottom: '24px'
  },
  discountTitle: {
    fontSize: '13px',
    fontWeight: '600',
    color: '#333',
    marginBottom: '8px'
  },
  discountItem: {
    fontSize: '13px',
    color: '#666',
    marginBottom: '4px'
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
    cursor: 'pointer'
  },
  summary: {},
  summaryName: {
    fontSize: '16px',
    fontWeight: '600',
    color: '#1a1a2e',
    marginBottom: '4px'
  },
  summaryDestination: {
    fontSize: '13px',
    color: '#666',
    marginBottom: '16px'
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
  summaryTotal: {
    display: 'flex',
    justifyContent: 'space-between',
    fontSize: '16px',
    fontWeight: '600',
    color: '#1a1a2e',
    marginBottom: '8px'
  },
  totalPrice: {
    color: '#e94560',
    fontSize: '20px'
  },
  summaryNote: {
    fontSize: '11px',
    color: '#aaa',
    marginTop: '8px'
  },
  center: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: 'calc(100vh - 64px)',
    color: '#999'
  }
}
