import { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import { getAvailablePackages } from '../api/packageApi'

export default function PackagesPage() {
  const [packages, setPackages] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [filters, setFilters] = useState({
    destination: '',
    minPrice: '',
    maxPrice: '',
    type: ''
  })
  const navigate = useNavigate()

  useEffect(() => {
    fetchPackages()
  }, [])

  const fetchPackages = async (activeFilters = {}) => {
    try {
      setLoading(true)
      setError(null)
      const cleanFilters = Object.fromEntries(
        Object.entries(activeFilters).filter(([_, v]) => v !== '')
      )
      const response = await getAvailablePackages(cleanFilters)
      setPackages(response.data)
    } catch (err) {
      setError('Error al cargar los paquetes')
    } finally {
      setLoading(false)
    }
  }

  const handleFilterChange = (e) => {
    setFilters({ ...filters, [e.target.name]: e.target.value })
  }

  const handleSearch = (e) => {
    e.preventDefault()
    fetchPackages(filters)
  }

  const handleClear = () => {
    setFilters({ destination: '', minPrice: '', maxPrice: '', type: '' })
    fetchPackages()
  }

  return (
    <div style={styles.page}>
      <div style={styles.hero}>
        <h1 style={styles.heroTitle}>Descubre tu próximo destino</h1>
        <p style={styles.heroSubtitle}>
          Explora nuestros paquetes turísticos nacionales e internacionales
        </p>
      </div>

      <div style={styles.container}>
        <form onSubmit={handleSearch} style={styles.filterBar}>
          <input
            name="destination"
            placeholder="Destino"
            value={filters.destination}
            onChange={handleFilterChange}
            style={styles.input}
          />
          <input
            name="minPrice"
            type="number"
            placeholder="Precio mínimo"
            value={filters.minPrice}
            onChange={handleFilterChange}
            style={styles.input}
          />
          <input
            name="maxPrice"
            type="number"
            placeholder="Precio máximo"
            value={filters.maxPrice}
            onChange={handleFilterChange}
            style={styles.input}
          />
          <select
            name="type"
            value={filters.type}
            onChange={handleFilterChange}
            style={styles.select}
          >
            <option value="">Todos los tipos</option>
            <option value="NATIONAL">Nacional</option>
            <option value="INTERNATIONAL">Internacional</option>
          </select>
          <button type="submit" style={styles.searchBtn}>
            Buscar
          </button>
          <button type="button" onClick={handleClear} style={styles.clearBtn}>
            Limpiar
          </button>
        </form>

        {loading && (
          <div style={styles.center}>
            <p>Cargando paquetes...</p>
          </div>
        )}

        {error && (
          <div style={styles.error}>
            <p>{error}</p>
          </div>
        )}

        {!loading && !error && packages.length === 0 && (
          <div style={styles.center}>
            <p>No se encontraron paquetes disponibles.</p>
          </div>
        )}

        <div style={styles.grid}>
          {packages.map((pkg) => (
            <div key={pkg.id} style={styles.card}>
              <div style={styles.cardHeader}>
                <span style={styles.typeBadge}>
                  {pkg.type === 'NATIONAL' ? 'Nacional' : 'Internacional'}
                </span>
                <span style={styles.statusBadge}>Disponible</span>
              </div>

              <h3 style={styles.cardTitle}>{pkg.name}</h3>
              <p style={styles.destination}>📍 {pkg.destination}</p>

              <p style={styles.description}>
                {pkg.description?.length > 100
                  ? pkg.description.substring(0, 100) + '...'
                  : pkg.description}
              </p>

              <div style={styles.cardDetails}>
                <div style={styles.detail}>
                  <span style={styles.detailLabel}>Inicio</span>
                  <span style={styles.detailValue}>{pkg.startDate}</span>
                </div>
                <div style={styles.detail}>
                  <span style={styles.detailLabel}>Término</span>
                  <span style={styles.detailValue}>{pkg.endDate}</span>
                </div>
                <div style={styles.detail}>
                  <span style={styles.detailLabel}>Cupos</span>
                  <span style={styles.detailValue}>{pkg.availableSlots}</span>
                </div>
              </div>

              <div style={styles.cardFooter}>
                <span style={styles.price}>
                  ${pkg.price?.toLocaleString('es-CL')}
                </span>
                <button
                  onClick={() => navigate(`/packages/${pkg.id}`)}
                  style={styles.detailBtn}
                >
                  Ver detalle
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

const styles = {
  page: {
    minHeight: '100vh',
    backgroundColor: '#f5f5f5'
  },
  hero: {
    backgroundColor: '#1a1a2e',
    padding: '60px 32px',
    textAlign: 'center'
  },
  heroTitle: {
    color: '#fff',
    fontSize: '36px',
    fontWeight: '700',
    marginBottom: '12px'
  },
  heroSubtitle: {
    color: '#ccc',
    fontSize: '16px'
  },
  container: {
    maxWidth: '1200px',
    margin: '0 auto',
    padding: '32px 16px'
  },
  filterBar: {
    display: 'flex',
    gap: '12px',
    flexWrap: 'wrap',
    marginBottom: '32px',
    backgroundColor: '#fff',
    padding: '20px',
    borderRadius: '12px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)'
  },
  input: {
    flex: 1,
    minWidth: '150px',
    padding: '10px 14px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    fontSize: '14px',
    outline: 'none'
  },
  select: {
    flex: 1,
    minWidth: '150px',
    padding: '10px 14px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    fontSize: '14px',
    outline: 'none',
    backgroundColor: '#fff'
  },
  searchBtn: {
    padding: '10px 24px',
    backgroundColor: '#e94560',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500'
  },
  clearBtn: {
    padding: '10px 24px',
    backgroundColor: 'transparent',
    color: '#666',
    border: '1px solid #ddd',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px'
  },
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))',
    gap: '24px'
  },
  card: {
    backgroundColor: '#fff',
    borderRadius: '12px',
    padding: '24px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)',
    display: 'flex',
    flexDirection: 'column',
    gap: '12px',
    transition: 'transform 0.2s, box-shadow 0.2s',
    cursor: 'pointer'
  },
  cardHeader: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
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
  cardTitle: {
    fontSize: '18px',
    fontWeight: '600',
    color: '#1a1a2e'
  },
  destination: {
    fontSize: '14px',
    color: '#666'
  },
  description: {
    fontSize: '14px',
    color: '#777',
    lineHeight: '1.5'
  },
  cardDetails: {
    display: 'flex',
    justifyContent: 'space-between',
    backgroundColor: '#f9f9f9',
    borderRadius: '8px',
    padding: '12px'
  },
  detail: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    gap: '4px'
  },
  detailLabel: {
    fontSize: '11px',
    color: '#999',
    textTransform: 'uppercase'
  },
  detailValue: {
    fontSize: '13px',
    fontWeight: '500',
    color: '#333'
  },
  cardFooter: {
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    marginTop: '8px'
  },
  price: {
    fontSize: '22px',
    fontWeight: '700',
    color: '#e94560'
  },
  detailBtn: {
    padding: '10px 20px',
    backgroundColor: '#1a1a2e',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500'
  },
  center: {
    textAlign: 'center',
    padding: '60px',
    color: '#999'
  },
  error: {
    textAlign: 'center',
    padding: '20px',
    backgroundColor: '#fff0f0',
    borderRadius: '8px',
    color: '#e94560',
    marginBottom: '24px'
  }
}