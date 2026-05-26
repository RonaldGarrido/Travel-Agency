import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getSalesReport, getPackageRanking } from '../../api/reportApi'
import { useAuth } from '../../context/AuthContext'

export default function AdminReportsPage() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [activeTab, setActiveTab] = useState('sales')
  const [salesData, setSalesData] = useState([])
  const [rankingData, setRankingData] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)
  const [generated, setGenerated] = useState(false)

  const handleGenerate = async (e) => {
    e.preventDefault()
    if (!startDate || !endDate) return
    setError(null)
    setLoading(true)
    try {
      const [salesRes, rankingRes] = await Promise.all([
        getSalesReport(startDate, endDate),
        getPackageRanking(startDate, endDate)
      ])
      setSalesData(salesRes.data)
      setRankingData(rankingRes.data)
      setGenerated(true)
    } catch {
      setError('Error al generar los reportes')
    } finally {
      setLoading(false)
    }
  }

  const totalSales = salesData.reduce(
    (sum, r) => sum + (r.totalAmount || 0), 0
  )

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <h1 style={styles.title}>Reportes</h1>

        <div style={styles.filterCard}>
          <form onSubmit={handleGenerate} style={styles.filterForm}>
            <div style={styles.field}>
              <label style={styles.label}>Fecha inicio</label>
              <input
                type="date"
                value={startDate}
                onChange={(e) => setStartDate(e.target.value)}
                required
                style={styles.input}
              />
            </div>
            <div style={styles.field}>
              <label style={styles.label}>Fecha término</label>
              <input
                type="date"
                value={endDate}
                onChange={(e) => setEndDate(e.target.value)}
                required
                style={styles.input}
              />
            </div>
            <button
              type="submit"
              disabled={loading}
              style={styles.generateBtn}
            >
              {loading ? 'Generando...' : 'Generar reportes'}
            </button>
          </form>
        </div>

        {error && <div style={styles.error}>{error}</div>}

        {generated && (
          <>
            <div style={styles.tabs}>
              <button
                onClick={() => setActiveTab('sales')}
                style={{
                  ...styles.tab,
                  ...(activeTab === 'sales' ? styles.activeTab : {})
                }}
              >
                Ventas por período
              </button>
              <button
                onClick={() => setActiveTab('ranking')}
                style={{
                  ...styles.tab,
                  ...(activeTab === 'ranking' ? styles.activeTab : {})
                }}
              >
                Ranking de paquetes
              </button>
            </div>

            {activeTab === 'sales' && (
              <div>
                <div style={styles.statsRow}>
                  <div style={styles.statBox}>
                    <span style={styles.statLabel}>Total reservas</span>
                    <span style={styles.statValue}>{salesData.length}</span>
                  </div>
                  <div style={styles.statBox}>
                    <span style={styles.statLabel}>Ingresos totales</span>
                    <span style={styles.statValue}>
                      ${totalSales.toLocaleString('es-CL')}
                    </span>
                  </div>
                </div>

                <div style={styles.tableWrap}>
                  <table style={styles.table}>
                    <thead>
                      <tr style={styles.thead}>
                        <th style={styles.th}>Cliente</th>
                        <th style={styles.th}>Paquete</th>
                        <th style={styles.th}>Pasajeros</th>
                        <th style={styles.th}>Total</th>
                        <th style={styles.th}>Estado</th>
                        <th style={styles.th}>Fecha</th>
                      </tr>
                    </thead>
                    <tbody>
                      {salesData.length === 0 ? (
                        <tr>
                          <td colSpan={6} style={styles.empty}>
                            Sin datos en este período
                          </td>
                        </tr>
                      ) : (
                        salesData.map((row) => (
                          <tr key={row.reservationId} style={styles.tr}>
                            <td style={styles.td}>{row.clientName}</td>
                            <td style={styles.td}>{row.packageName}</td>
                            <td style={styles.td}>{row.passengerCount}</td>
                            <td style={styles.td}>
                              ${row.totalAmount?.toLocaleString('es-CL')}
                            </td>
                            <td style={styles.td}>{row.status}</td>
                            <td style={styles.td}>
                              {row.date
                                ? new Date(row.date).toLocaleDateString('es-CL')
                                : '-'}
                            </td>
                          </tr>
                        ))
                      )}
                    </tbody>
                  </table>
                </div>
              </div>
            )}

            {activeTab === 'ranking' && (
              <div style={styles.tableWrap}>
                <table style={styles.table}>
                  <thead>
                    <tr style={styles.thead}>
                      <th style={styles.th}>#</th>
                      <th style={styles.th}>Paquete</th>
                      <th style={styles.th}>Destino</th>
                      <th style={styles.th}>Reservas</th>
                      <th style={styles.th}>Pasajeros</th>
                      <th style={styles.th}>Ingresos</th>
                    </tr>
                  </thead>
                  <tbody>
                    {rankingData.length === 0 ? (
                      <tr>
                        <td colSpan={6} style={styles.empty}>
                          Sin datos en este período
                        </td>
                      </tr>
                    ) : (
                      rankingData.map((row, index) => (
                        <tr key={row.packageId} style={styles.tr}>
                          <td style={styles.td}>
                            <span style={{
                              ...styles.rank,
                              backgroundColor: index === 0
                                ? '#FFD700'
                                : index === 1
                                ? '#C0C0C0'
                                : index === 2
                                ? '#CD7F32'
                                : '#f0f0f0',
                              color: index < 3 ? '#333' : '#999'
                            }}>
                              {index + 1}
                            </span>
                          </td>
                          <td style={styles.td}>{row.packageName}</td>
                          <td style={styles.td}>{row.destination}</td>
                          <td style={styles.td}>{row.totalReservations}</td>
                          <td style={styles.td}>{row.totalPassengers}</td>
                          <td style={styles.td}>
                            ${row.totalRevenue?.toLocaleString('es-CL')}
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>
            )}
          </>
        )}
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
  container: { maxWidth: '1100px', margin: '0 auto' },
  title: {
    fontSize: '24px',
    fontWeight: '700',
    color: '#1a1a2e',
    marginBottom: '24px'
  },
  filterCard: {
    backgroundColor: '#fff',
    borderRadius: '12px',
    padding: '24px',
    marginBottom: '24px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)'
  },
  filterForm: {
    display: 'flex',
    gap: '16px',
    alignItems: 'flex-end',
    flexWrap: 'wrap'
  },
  field: { display: 'flex', flexDirection: 'column', gap: '6px' },
  label: { fontSize: '13px', fontWeight: '500', color: '#333' },
  input: {
    padding: '10px 12px',
    borderRadius: '8px',
    border: '1px solid #ddd',
    fontSize: '14px',
    outline: 'none'
  },
  generateBtn: {
    padding: '10px 24px',
    backgroundColor: '#e94560',
    color: '#fff',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    fontWeight: '500'
  },
  error: {
    backgroundColor: '#fff0f0',
    color: '#e94560',
    padding: '12px',
    borderRadius: '8px',
    marginBottom: '16px',
    fontSize: '14px'
  },
  tabs: {
    display: 'flex',
    gap: '4px',
    marginBottom: '20px',
    backgroundColor: '#fff',
    padding: '4px',
    borderRadius: '10px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.06)',
    width: 'fit-content'
  },
  tab: {
    padding: '10px 20px',
    border: 'none',
    borderRadius: '8px',
    cursor: 'pointer',
    fontSize: '14px',
    backgroundColor: 'transparent',
    color: '#666'
  },
  activeTab: {
    backgroundColor: '#1a1a2e',
    color: '#fff'
  },
  statsRow: {
    display: 'flex',
    gap: '16px',
    marginBottom: '20px'
  },
  statBox: {
    backgroundColor: '#fff',
    borderRadius: '12px',
    padding: '20px 28px',
    display: 'flex',
    flexDirection: 'column',
    gap: '4px',
    boxShadow: '0 2px 8px rgba(0,0,0,0.06)'
  },
  statLabel: { fontSize: '13px', color: '#999' },
  statValue: { fontSize: '24px', fontWeight: '700', color: '#1a1a2e' },
  tableWrap: { overflowX: 'auto' },
  table: {
    width: '100%',
    borderCollapse: 'collapse',
    backgroundColor: '#fff',
    borderRadius: '12px',
    overflow: 'hidden',
    boxShadow: '0 2px 8px rgba(0,0,0,0.08)'
  },
  thead: { backgroundColor: '#1a1a2e' },
  th: {
    padding: '14px 16px',
    textAlign: 'left',
    fontSize: '13px',
    fontWeight: '500',
    color: '#fff'
  },
  tr: { borderBottom: '1px solid #f0f0f0' },
  td: { padding: '14px 16px', fontSize: '14px', color: '#333' },
  empty: {
    padding: '40px',
    textAlign: 'center',
    color: '#999',
    fontSize: '14px'
  },
  rank: {
    display: 'inline-flex',
    alignItems: 'center',
    justifyContent: 'center',
    width: '28px',
    height: '28px',
    borderRadius: '50%',
    fontSize: '13px',
    fontWeight: '600'
  }
}