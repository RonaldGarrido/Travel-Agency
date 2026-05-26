import { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getPackageById } from '../api/packageApi'
import { useAuth } from '../context/AuthContext'

export default function PackageDetailPage() {
  const { id } = useParams()
  const navigate = useNavigate()
  const { user } = useAuth()
  const [pkg, setPkg] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    const fetchPackage = async () => {
      try {
        const response = await getPackageById(id)
        setPkg(response.data)
      } catch (err) {
        setError('No se pudo cargar el paquete')
      } finally {
        setLoading(false)
      }
    }
    fetchPackage()
  }, [id])

  const handleReserve = () => {
    if (!user) {
      navigate('/login')
      return
    }
    navigate(`/reservations/new/${id}`)
  }

  if (loading) return <div style={styles.center}>Cargando...</div>
  if (error) return <div style={styles.center}>{error}</div>
  if (!pkg) return null

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <button onClick={() => navigate('/packages')} style={styles.backBtn}>
          ← Volver a paquetes
        </button>

        <div style={styles.card}>
          <div style={styles.cardTop}>
            <div>
              <div style={styles.badges}>
                <span style={styles.typeBadge}>
                  {pkg.type === 'NATIONAL' ? 'Nacional' : 'Internacional'}
                </span>
                <span style={styles.statusBadge}>{pkg.status}</span>
              </div>
              <h1 style={styles.title}>{pkg.name}</h1>
              <p style={styles.destination}>📍 {pkg.destination}</p>
            </div>
            <div style={styles.priceBox}>
              <span style={styles.priceLabel}>Precio por persona</span>
              <span style={styles.price}>
                ${pkg.price?.toLocaleString('es-CL')}
              </span>
              <span style={styles.slots}>
                {pkg.availableSlots} cupos disponibles
              </span>
            </div>
          </div>

          <div style={styles.divider} />

          <div style={styles.grid}>
            <div style={styles.section}>
              <h3 style={styles.sectionTitle}>Descripción</h3>
              <p style={styles.text}>{pkg.description}</p>
            </div>

            <div style={styles.section}>
              <h3 style={styles.sectionTitle}>Fechas</h3>
              <div style={styles.dateRow}>
                <div style={styles.dateBox}>
                  <span style={styles.dateLabel}>Fecha de inicio</span>
                  <span style={styles.dateValue}>{pkg.startDate}</span>
                </div>
                <div style={styles.dateArrow}>→</div>
                <div style={styles.dateBox}>
                  <span style={styles.dateLabel}>Fecha de término</span>
                  <span style={styles.dateValue}>{pkg.endDate}</span>
                </div>
              </div>
            </div>

            {pkg.includedServices && (
              <div style={styles.section}>
                <h3 style={styles.sectionTitle}>Servicios incluidos</h3>
                <p style={styles.text}>{pkg.includedServices}</p>
              </div>
            )}

            {pkg.conditions && (
              <div style={styles.section}>
                <h3 style={styles.sectionTitle}>Condiciones</h3>
                <p style={styles.text}>{pkg.conditions}</p>
              </div>
            )}

            {pkg.restrictions && (
              <div style={styles.section}>
                <h3 style={styles.sectionTitle}>Restricciones</h3>
                <p style={styles.text}>{pkg.restrictions}</p>
              </div>
            )}
          </div>

          <div style={styles.divider} />

          <div style={styles.footer}>
            <div>
              <p style={styles.footerNote}>
                El monto total se calculará según la cantidad de pasajeros
              </p>
              {!user && (
                <p style={styles.loginNote}>
                  Debes iniciar sesión para realizar una reserva
                </p>
              )}
            </div>
            <button
              onClick={handleReserve}
              disabled={pkg.availableSlots === 0}
              style={{
                ...styles.reserveBtn,
                opacity: pkg.availableSlots === 0 ? 0.5 : 1,
                cursor: pkg.availableSlots === 0 ? 'not-allowed' : 'pointer'
              }}
            >
              {pkg.availableSlots === 0 ? 'Sin cupos' : 'Reservar ahora'}
            </button>
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
    marginBottom: '20px',
    padding: '0'
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: '16px',
    padding: '32px',
    boxShadow: '0 4px 20px rgba(0,0,0,0.08)'
  },
  cardTop: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'flex-start',
    flexWrap: 'wrap',
    gap: '20px'
  },
  badges: {
    display: 'flex',
    gap: '8px',
    marginBottom: '12px'
  },
  typeBadge: {
    padding: '4px 10px',
    backgroundColor: '#e8f4fd',
    color: '#1a6ea8',
    borderRadius: '20px',
    fontSize: '12px',
    fontWeight: '500'
  },
  statusBadge: {
    padding: '4px 10px',
    backgroundColor: '#e8f8e8',
    color: '#2d8a2d',
    borderRadius: '20px',
    fontSize: '12px',
    fontWeight: '500'
  },
  title: {
    fontSize: '28px',
    fontWeight: '700',
    color: '#1a1a2e',
    marginBottom: '8px'
  },
  destination: {
    fontSize: '16px',
    color: '#666'
  },
  priceBox: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'flex-end',
    gap: '4px'
  },
  priceLabel: {
    fontSize: '12px',
    color: '#999'
  },
  price: {
    fontSize: '32px',
    fontWeight: '700',
    color: '#e94560'
  },
  slots: {
    fontSize: '13px',
    color: '#2d8a2d'
  },
  divider: {
    height: '1px',
    backgroundColor: '#f0f0f0',
    margin: '24px 0'
  },
  grid: {
    display: 'flex',
    flexDirection: 'column',
    gap: '24px'
  },
  section: {
    display: 'flex',
    flexDirection: 'column',
    gap: '8px'
  },
  sectionTitle: {
    fontSize: '16px',
    fontWeight: '600',
    color: '#1a1a2e'
  },
  text: {
    fontSize: '14px',
    color: '#555',
    lineHeight: '1.6'
  },
  dateRow: {
    display: 'flex',
    alignItems: 'center',
    gap: '16px'
  },
  dateBox: {
    display: 'flex',
    flexDirection: 'column',
    gap: '4px',
    backgroundColor: '#f9f9f9',
    padding: '12px 20px',
    borderRadius: '8px'
  },
  dateLabel: {
    fontSize: '11px',
    color: '#999',
    textTransform: 'uppercase'
  },
  dateValue: {
    fontSize: '15px',
    fontWeight: '600',
    color: '#333'
  },
  dateArrow: {
    fontSize: '20px',
    color: '#ccc'
  },
  footer: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    flexWrap: 'wrap',
    gap: '16px'
  },
  footerNote: {
    fontSize: '13px',
    color: '#999'
  },
  loginNote: {
    fontSize: '13px',
    color: '#e94560',
    marginTop: '4px'
  },
  reserveBtn: {
    padding: '14px 32px',
    backgroundColor: '#e94560',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    fontSize: '16px',
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